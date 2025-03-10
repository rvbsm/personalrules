package dev.rvbsm.personalrules;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PersonalRulesManager {

    public static final Map<String, GameRules.Key<?>> SUPPORTED_KEYS = PersonalRules.SUPPORTED_RULES.stream()
        .collect(ImmutableMap.toImmutableMap(GameRules.Key::getName, Function.identity()));

    private static final Map<GameRules.Key<?>, Integer> DEFAULT_CONFIG = ImmutableMap.ofEntries(
        Map.entry(GameRules.KEEP_INVENTORY, 2),
        Map.entry(GameRules.DO_MOB_LOOT, 2),
        Map.entry(GameRules.PROJECTILES_CAN_BREAK_BLOCKS, 2),
        Map.entry(GameRules.DO_TILE_DROPS, 2),
        Map.entry(GameRules.DO_ENTITY_DROPS, 2),
        Map.entry(GameRules.NATURAL_REGENERATION, 2),
        Map.entry(GameRules.REDUCED_DEBUG_INFO, 0),
        Map.entry(GameRules.DO_LIMITED_CRAFTING, 2),
        Map.entry(GameRules.DISABLE_RAIDS, 4),
        Map.entry(GameRules.DO_INSOMNIA, 0),
        Map.entry(GameRules.DO_IMMEDIATE_RESPAWN, 0),
        Map.entry(GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY, 0),
        Map.entry(GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY, 0),
        Map.entry(GameRules.DROWNING_DAMAGE, 2),
        Map.entry(GameRules.FALL_DAMAGE, 2),
        Map.entry(GameRules.FIRE_DAMAGE, 2),
        Map.entry(GameRules.FREEZE_DAMAGE, 2),
        Map.entry(GameRules.DO_PATROL_SPAWNING, 2),
        Map.entry(GameRules.DO_TRADER_SPAWNING, 2),
        Map.entry(GameRules.DO_WARDEN_SPAWNING, 2),
        Map.entry(GameRules.FORGIVE_DEAD_PLAYERS, 0),
        Map.entry(GameRules.ENDER_PEARLS_VANISH_ON_DEATH, 0));

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalRulesManager.class);
    private static final String CONFIG_NAME = "personalrules.conf";

    private final Path configPath;
    private Map<GameRules.Key<?>, Integer> personalRules = ImmutableMap.of();
    private int commandPermissionLevel = 5;
    private boolean isDirty = false;

    public PersonalRulesManager(@NotNull MinecraftServer server) {
        this.configPath = server.getSavePath(WorldSavePath.ROOT).resolve(CONFIG_NAME);
    }

    public void load() {
        this.readConfig();

        if (this.isDirty) {
            this.saveConfig();
        }
    }

    public void unload() {
        if (this.isDirty) {
            this.saveConfig();
        }

        this.personalRules = ImmutableMap.of();
    }

    private void readConfig() {
        if (!Files.exists(this.configPath)) {
            this.personalRules = DEFAULT_CONFIG;
            this.isDirty = true;
        } else try (final var reader = Files.newBufferedReader(this.configPath)) {
            final var parsedRules = new HashMap<GameRules.Key<?>, Integer>();

            int minPermissionLevel = 5;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                final String[] parsedEntry = line.split("\\s+", 2);
                if (parsedEntry.length < 2) {
                    this.isDirty = true;
                    continue;
                }

                final GameRules.Key<?> parsedKey = SUPPORTED_KEYS.get(parsedEntry[0]);
                if (parsedKey == null) {
                    LOGGER.warn("Unsupported gamerule: {}", parsedEntry[0]);
                    this.isDirty = true;
                    continue;
                }

                final int parsedPermissionLevel;
                switch (parsedEntry[1]) {
                    case "true", "0" -> parsedPermissionLevel = 0;
                    case "1", "2", "3", "4" -> parsedPermissionLevel = Integer.parseInt(parsedEntry[1]);
                    case "ops" -> parsedPermissionLevel = 4;
                    case "false" -> parsedPermissionLevel = 5;

                    default -> {
                        this.isDirty = true;
                        continue;
                    }
                }

                parsedRules.put(parsedKey, parsedPermissionLevel);
                minPermissionLevel = Math.min(minPermissionLevel, parsedPermissionLevel);
            }

            if (parsedRules.size() < PersonalRules.SUPPORTED_RULES.size()) {
                PersonalRules.SUPPORTED_RULES.stream()
                    .filter(key -> !parsedRules.containsKey(key))
                    .forEach(key -> parsedRules.put(key, 5));
                this.isDirty = true;
            }

            this.personalRules = ImmutableMap.copyOf(parsedRules);
            this.commandPermissionLevel = minPermissionLevel;
        } catch (IOException e) {
            LOGGER.warn("Could not parse a config", e);
        }
    }

    private void saveConfig() {
        try (final var writer = Files.newBufferedWriter(this.configPath)) {
            for (final GameRules.Key<?> rule : PersonalRules.SUPPORTED_RULES) {
                final String ruleKey = rule.getName();
                final int rulePermissionLevel = this.personalRules.get(rule);

                writer.write("%s %d\n".formatted(ruleKey, rulePermissionLevel));
            }

            this.isDirty = false;
        } catch (IOException e) {
            LOGGER.warn("Could not save a config", e);
        }
    }

    public boolean hasPermissionLevel(@NotNull ServerCommandSource src) {
        return src.isExecutedByPlayer() && src.hasPermissionLevel(this.commandPermissionLevel);
    }

    public boolean hasPermissionLevel(@NotNull ServerCommandSource src, GameRules.Key<?> key) {
        return src.hasPermissionLevel(this.personalRules.getOrDefault(key, 5));
    }
}
