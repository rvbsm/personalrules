package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(ForgetAngryAtTargetTask.class)
public abstract class ForgetAngryAtTargetTaskMixin_forgiveDeadPlayer {

    @WrapOperation(
        method = "method_47144",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private static boolean getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.BooleanRule> rule,
        Operation<Boolean> original,
        @Local(argsOnly = true) LivingEntity entity
    ) {
        return PersonalRulesHelper.booleanOrElse(entity, rule, instance, original);
    }
}
