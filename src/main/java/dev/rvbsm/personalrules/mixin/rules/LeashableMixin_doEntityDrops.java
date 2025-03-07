package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(Leashable.class)
public interface LeashableMixin_doEntityDrops {

    @WrapOperation(
        method = "tickLeash", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private static <E extends Entity & Leashable> boolean getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.BooleanRule> rule,
        Operation<Boolean> original,
        @Local(argsOnly = true) E entity
    ) {
        final Leashable.LeashData leashData = entity.getLeashData();
        if (leashData.leashHolder instanceof PlayerEntity player) {
            return PersonalRulesHelper.booleanOrElse(player, rule, instance, original);
        }

        return original.call(instance, rule);
    }
}
