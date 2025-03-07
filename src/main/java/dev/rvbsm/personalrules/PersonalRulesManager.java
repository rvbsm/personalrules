package dev.rvbsm.personalrules;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameRules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import dev.rvbsm.personalrules.player.PersonalRules;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PersonalRulesManager {

    public static final Map<String, GameRules.Key<?>> SUPPORTED_KEYS = PersonalRules.SUPPORTED_RULES.stream()
        .collect(ImmutableMap.toImmutableMap(GameRules.Key::getName, Function.identity()));

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalRulesManager.class);
    private static final String CONFIG_NAME = "personalrules.conf";

    private static MinecraftServer runningServer;
    private static Map<GameRules.Key<?>, Integer> personalRules = ImmutableMap.of();
    private static int commandPermissionLevel;

    private static Path getConfigPath() {
        return runningServer.getSavePath(WorldSavePath.ROOT).resolve(CONFIG_NAME);
    }

    public static void load(MinecraftServer server) {
        if (runningServer != null) {
            throw new IllegalStateException("The manager is already occupied by another server instance");
        }

        runningServer = server;

        final Path configPath = getConfigPath();
        if (!Files.exists(configPath)) {
            // TODO?: funny default state
            personalRules = PersonalRules.SUPPORTED_RULES.stream()
                .collect(ImmutableMap.toImmutableMap(Function.identity(), (i) -> 0));
            save();
            return;
        }

        try (final var reader = Files.newBufferedReader(configPath)) {
            final var parsedRules = new HashMap<GameRules.Key<?>, Integer>();

            int minPermissionLevel = 5;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                final String[] parsedEntry = line.split("\\s+", 2);

                final GameRules.Key<?> parsedRule = SUPPORTED_KEYS.get(parsedEntry[0]);
                if (parsedRule == null) {
                    LOGGER.warn("Unsupported gamerule: {}", parsedEntry[0]);
                    return;
                }

                final int parsedPermissionLevel = switch (parsedEntry[1]) {
                    case "true", "0" -> 0;
                    case "1", "2", "3", "4" -> Integer.parseInt(parsedEntry[1]);
                    case "ops" -> 4;

                    default -> 5; // disabled
                };

                parsedRules.put(parsedRule, parsedPermissionLevel);
                minPermissionLevel = Math.min(minPermissionLevel, parsedPermissionLevel);
            }

            personalRules = ImmutableMap.copyOf(parsedRules);
            commandPermissionLevel = minPermissionLevel;
        } catch (IOException e) {
            LOGGER.warn("Could not load a config", e);
        }
    }

    public static void unload() {
        save();

        runningServer = null;
        personalRules = ImmutableMap.of();
    }

    public static void save() {
        if (runningServer == null) {
            throw new IllegalStateException("Cannot save the config without server running");
        }

        final Path configPath = getConfigPath();
        try (final var writer = Files.newBufferedWriter(configPath)) {
            for (final GameRules.Key<?> rule : PersonalRules.SUPPORTED_RULES) {
                final String ruleKey = rule.getName();
                final boolean isEnabled = personalRules.containsKey(rule);

                writer.write("%s %s".formatted(ruleKey, isEnabled));
            }
        } catch (IOException e) {
            LOGGER.warn("Could not save a config", e);
        }
    }

    public static Map<GameRules.Key<?>, Integer> getPersonalRules() {
        return personalRules;
    }

    public static int getCommandPermissionLevel() {
        return commandPermissionLevel;
    }

    public static boolean isAvailable(GameRules.Key<?> rule, int permissionLevel) {
        return personalRules.get(rule) <= permissionLevel;
    }
}
