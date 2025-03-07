package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(Angerable.class)
public interface AngerableMixin_forgiveDeadPlayer {

    @WrapOperation(method = "forgive", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean getPersonal(
            GameRules instance,
            GameRules.Key<GameRules.BooleanRule> rule,
            Operation<Boolean> original,
            @Local(argsOnly = true) PlayerEntity player) {
        return PersonalRulesHelper.booleanOrElse(player, rule, instance, original);
    }
}
