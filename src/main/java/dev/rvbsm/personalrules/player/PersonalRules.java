package dev.rvbsm.personalrules.player;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import dev.rvbsm.personalrules.mixin.rules.GameRulesAccess;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DynamicLike;
import org.jetbrains.annotations.Nullable;

public final class PersonalRules {

    public static final Set<GameRules.Key<?>> SUPPORTED_RULES = ImmutableSet.of(
        GameRules.KEEP_INVENTORY,
        GameRules.DO_MOB_LOOT,
        GameRules.PROJECTILES_CAN_BREAK_BLOCKS,
        GameRules.DO_TILE_DROPS,
        GameRules.DO_ENTITY_DROPS,
        GameRules.NATURAL_REGENERATION,
        GameRules.REDUCED_DEBUG_INFO,
        GameRules.DO_LIMITED_CRAFTING,
        GameRules.DISABLE_RAIDS,
        GameRules.DO_INSOMNIA,
        GameRules.DO_IMMEDIATE_RESPAWN,
        GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY,
        GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY,
        GameRules.DROWNING_DAMAGE,
        GameRules.FALL_DAMAGE,
        GameRules.FIRE_DAMAGE,
        GameRules.FREEZE_DAMAGE,
        GameRules.DO_PATROL_SPAWNING,
        GameRules.DO_TRADER_SPAWNING,
        GameRules.DO_WARDEN_SPAWNING,
        GameRules.FORGIVE_DEAD_PLAYERS,
        GameRules.ENDER_PEARLS_VANISH_ON_DEATH);
//        GameRules.TNT_EXPLODES);

    private static final Map<GameRules.Key<?>, BiConsumer<ServerPlayerEntity, GameRules.Rule<?>>> RULES_CALLBACKS = ImmutableMap.ofEntries(
        Map.entry(
            GameRules.REDUCED_DEBUG_INFO, (player, rule) -> player.networkHandler.sendPacket(new EntityStatusS2CPacket(
                player,
                ((GameRules.BooleanRule) rule).get() ? EntityStatuses.USE_REDUCED_DEBUG_INFO : EntityStatuses.USE_FULL_DEBUG_INFO))),

        Map.entry(
            GameRules.DO_LIMITED_CRAFTING,
            (player, rule) -> player.networkHandler.sendPacket(new GameStateChangeS2CPacket(
                GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED,
                ((GameRules.BooleanRule) rule).get() ? 1.0F : GameStateChangeS2CPacket.DEMO_OPEN_SCREEN))),

        Map.entry(
            GameRules.DO_IMMEDIATE_RESPAWN,
            (player, rule) -> player.networkHandler.sendPacket(new GameStateChangeS2CPacket(
                GameStateChangeS2CPacket.IMMEDIATE_RESPAWN,
                ((GameRules.BooleanRule) rule).get() ? 1.0F : GameStateChangeS2CPacket.DEMO_OPEN_SCREEN))));

    private final Map<GameRules.Key<?>, GameRules.Rule<?>> rules;
    private final Set<GameRules.Key<?>> appliedRules;
    private final FeatureSet enabledFeatures;

    public PersonalRules(FeatureSet enabledFeatures, DynamicLike<?> values) {
        this(enabledFeatures);
        this.load(values);
    }

    public PersonalRules(FeatureSet enabledFeatures) {
        this(
            GameRulesAccess.callStreamAllRules(enabledFeatures)
                .filter(entry -> SUPPORTED_RULES.contains(entry.getKey()))
                .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> entry.getValue().createRule())),
            enabledFeatures);
    }

    private PersonalRules(Map<GameRules.Key<?>, GameRules.Rule<?>> rules, FeatureSet enabledFeatures) {
        this.rules = rules;
        this.enabledFeatures = enabledFeatures;
        this.appliedRules = new HashSet<>();
    }

    public static void changed(@Nullable ServerPlayerEntity player, GameRules.Key<?> key, GameRules.Rule<?> rule) {
        if (player != null && RULES_CALLBACKS.containsKey(key)) {
            RULES_CALLBACKS.get(key).accept(player, rule);
        }
    }

    public NbtCompound toNbt() {
        final NbtCompound nbt = new NbtCompound();

        this.rules.forEach((key, value) -> {
            if (appliedRules.contains(key)) {
                nbt.putString(key.getName(), value.serialize());
            }
        });

        return nbt;
    }

    private void load(DynamicLike<?> values) {
        this.rules.forEach((key, rule) -> values.get(key.getName()).asString().ifSuccess(value -> {
            this.applyRule(key);
            ((GameRulesAccess.RuleAccess) rule).callDeserialize(value);
        }));
    }

    public <T extends GameRules.Rule<T>> Optional<T> get(GameRules.Key<T> key) {
        if (!this.appliedRules.contains(key)) {
            return Optional.empty();
        }

        final T rule = (T) this.rules.get(key);
        if (rule == null) {
            throw new IllegalArgumentException("Tried to access invalid personal rule");
        }

        return Optional.of(rule);
    }

    public Optional<Boolean> getBoolean(GameRules.Key<GameRules.BooleanRule> key) {
        return this.get(key).map(GameRules.BooleanRule::get);
    }

    public Optional<Integer> getInteger(GameRules.Key<GameRules.IntRule> key) {
        return this.get(key).map(GameRules.IntRule::get);
    }

    public <T extends GameRules.Rule<T>> Optional<T> applyRule(GameRules.Key<T> key) {
        this.appliedRules.add(key);
        return this.get(key);
    }

    public <T extends GameRules.Rule<T>> void revokeRule(GameRules.Key<T> key) {
        this.appliedRules.remove(key);
    }
}
