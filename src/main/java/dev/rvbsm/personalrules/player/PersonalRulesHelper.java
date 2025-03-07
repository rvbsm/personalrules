package dev.rvbsm.personalrules.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

import java.util.Optional;

import dev.rvbsm.personalrules.api.PersonalRulesAccess;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

public final class PersonalRulesHelper {

    private PersonalRulesHelper() {
        throw new AssertionError("utility class");
    }

    public static Optional<Boolean> getBoolean(PlayerEntity player, GameRules.Key<GameRules.BooleanRule> rule) {
        return ((PersonalRulesAccess) player).personalrules$getPersonalRules().getBoolean(rule);
    }

    public static boolean booleanOrElse(
        PlayerEntity player,
        GameRules.Key<GameRules.BooleanRule> rule,
        GameRules gameRules,
        Operation<Boolean> original
    ) {
        return getBoolean(player, rule).orElseGet(() -> original.call(gameRules, rule));
    }

    public static Optional<Integer> getInteger(PlayerEntity player, GameRules.Key<GameRules.IntRule> rule) {
        return ((PersonalRulesAccess) player).personalrules$getPersonalRules().getInteger(rule);
    }

    public static int integerOrElse(
        PlayerEntity player,
        GameRules.Key<GameRules.IntRule> rule,
        GameRules gameRules,
        Operation<Integer> original
    ) {
        return getInteger(player, rule).orElseGet(() -> original.call(gameRules, rule));
    }
}
