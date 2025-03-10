package dev.rvbsm.personalrules;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameRules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import dev.rvbsm.personalrules.player.PersonalRules;

public final class PersonalRulesManager {

    private static final Map<String, GameRules.Key<?>> SUPPORTED_RULE_KEYS = PersonalRules.SUPPORTED_RULES.stream()
        .collect(ImmutableMap.toImmutableMap(GameRules.Key::getName, Function.identity()));

    private static final Map<GameRules.Key<?>, Integer> DEFAULT_RULES = ImmutableMap.ofEntries(
        Map.entry(GameRules.KEEP_INVENTORY, 2),
        Map.entry(GameRules.DO_MOB_LOOT, 2),
        Map.entry(GameRules.PROJECTILES_CAN_BREAK_BLOCKS, 2),
        Map.entry(GameRules.DO_TILE_DROPS, 2),
        Map.entry(GameRules.DO_ENTITY_DROPS, 2),
        Map.entry(GameRules.NATURAL_REGENERATION, 2),
        Map.entry(GameRules.REDUCED_DEBUG_INFO, 0),
        Map.entry(GameRules.DO_LIMITED_CRAFTING, 2),
        Map.entry(GameRules.DISABLE_RAIDS, 2),
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
    private final Object2IntMap<GameRules.Key<?>> personalRules = new Object2IntArrayMap<>(DEFAULT_RULES);
    private int minimalPermissionLevel = 5;
    private boolean isDirty = false;

    public PersonalRulesManager(@NotNull MinecraftServer server) {
        this.configPath = server.getSavePath(WorldSavePath.ROOT).resolve(CONFIG_NAME);
    }

    public void load() {
        this.readConfig();
        this.saveConfig(false);
    }

    public void unload() {
        this.saveConfig(false);
        this.personalRules.clear();
    }

    private void readConfig() {
        try {
            if (!Files.exists(this.configPath) || Files.size(this.configPath) == 0) {
                this.isDirty = true;

                return;
            }

            int parsedSize = 0;
            try (final var lines = Files.lines(this.configPath)) {
                for (final String line : (Iterable<String>) lines::iterator) {
                    final String[] parsedEntry = line.split("\\s+", 2);
                    if (parsedEntry.length != 2) {
                        this.isDirty = true;
                        continue;
                    }

                    final GameRules.Key<?> parsedKey = SUPPORTED_RULE_KEYS.get(parsedEntry[0]);
                    if (parsedKey == null) {
                        LOGGER.warn("Unsupported personal rule: {}", parsedEntry[0]);
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
                            LOGGER.warn("Unknown permission level: {}", parsedEntry[1]);
                            this.isDirty = true;
                            continue;
                        }
                    }

                    this.personalRules.put(parsedKey, parsedPermissionLevel);
                    this.minimalPermissionLevel = Math.min(this.minimalPermissionLevel, parsedPermissionLevel);
                    parsedSize++;
                }

                if (this.personalRules.size() != DEFAULT_RULES.size()) {
                    DEFAULT_RULES.forEach((key, level) -> this.personalRules.putIfAbsent(key, level.intValue()));
                    this.isDirty = true;
                } else if (this.personalRules.size() != parsedSize) {
                    this.isDirty = true;
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Could not parse a config", e);
        }
    }

    public void saveConfig(boolean force) {
        if (!this.isDirty && !force) {
            return;
        }

        try (final var writer = Files.newBufferedWriter(this.configPath)) {
            for (final Map.Entry<String, GameRules.Key<?>> ruleEntry : SUPPORTED_RULE_KEYS.entrySet()) {
                final String ruleKey = ruleEntry.getKey();
                final int ruleLevel = this.personalRules.getInt(ruleEntry.getValue());

                writer.write("%s %s\n".formatted(
                    ruleKey, switch (ruleLevel) {
                        case 0 -> "true";
                        case 2 -> "ops";
                        case 5 -> "false";
                        default -> Integer.toString(ruleLevel, 10);
                    }));
            }

            this.isDirty = false;
        } catch (IOException e) {
            LOGGER.warn("Could not save a config", e);
        }
    }

    public boolean hasPermissionLevel(@NotNull ServerCommandSource src) {
        return src.isExecutedByPlayer() && src.hasPermissionLevel(this.minimalPermissionLevel);
    }

    public boolean hasPermissionLevel(@NotNull ServerCommandSource src, GameRules.Key<?> key) {
        return src.isExecutedByPlayer() && src.hasPermissionLevel(this.personalRules.getInt(key));
    }

    public int getPermissionLevel(GameRules.Key<?> key) {
        return this.personalRules.getInt(key);
    }

    public void updatePermissionLevel(GameRules.Key<?> key, int permissionLevel) {
        final int prevPermissionLevel = this.personalRules.put(key, permissionLevel);

        if (this.minimalPermissionLevel > permissionLevel) {
            this.minimalPermissionLevel = permissionLevel;
            this.isDirty = true;
        } else if (this.minimalPermissionLevel == prevPermissionLevel) {
            if (!this.personalRules.containsValue(this.minimalPermissionLevel)) {
                this.minimalPermissionLevel = permissionLevel;
                this.isDirty = true;
            }
        }
    }
}
