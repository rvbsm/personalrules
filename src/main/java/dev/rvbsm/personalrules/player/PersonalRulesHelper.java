package dev.rvbsm.personalrules.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

import java.util.Optional;
import java.util.function.Supplier;

import dev.rvbsm.personalrules.api.PersonalRulesAccess;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.jetbrains.annotations.Nullable;

public final class PersonalRulesHelper {

    private PersonalRulesHelper() {
        throw new AssertionError("utility class");
    }

    public static Optional<Boolean> getBoolean(PlayerEntity player, GameRules.Key<GameRules.BooleanRule> rule) {
        return ((PersonalRulesAccess) player).personalrules$getPersonalRules().getBoolean(rule);
    }

    public static boolean booleanOrElse(
        @Nullable Entity entity,
        GameRules.Key<GameRules.BooleanRule> rule,
        GameRules gameRules,
        Operation<Boolean> original
    ) {
        final Supplier<Boolean> originalSupplier = () -> original.call(gameRules, rule);

        if (entity == null || !entity.isPlayer()) {
            return originalSupplier.get();
        }

        return getBoolean((PlayerEntity) entity, rule).orElseGet(originalSupplier);
    }

    public static Optional<Integer> getInteger(PlayerEntity player, GameRules.Key<GameRules.IntRule> rule) {
        return ((PersonalRulesAccess) player).personalrules$getPersonalRules().getInteger(rule);
    }

    public static int integerOrElse(
        @Nullable Entity entity,
        GameRules.Key<GameRules.IntRule> rule,
        GameRules gameRules,
        Operation<Integer> original
    ) {
        final Supplier<Integer> originalSupplier = () -> original.call(gameRules, rule);

        if (entity == null || !entity.isPlayer()) {
            return originalSupplier.get();
        }

        return getInteger((PlayerEntity) entity, rule).orElseGet(originalSupplier);
    }
}
