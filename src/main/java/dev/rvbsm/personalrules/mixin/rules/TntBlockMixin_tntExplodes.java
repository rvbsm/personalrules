package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.explosion.Explosion;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

//@Mixin(TntBlock.class)
public abstract class TntBlockMixin_tntExplodes {

//    @WrapOperation(
//        method = "primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)Z",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
//    private static boolean getPersonalIgniter(
//        GameRules instance,
//        GameRules.Key<GameRules.BooleanRule> rule,
//        Operation<Boolean> original,
//        @Local(argsOnly = true) LivingEntity igniter
//    ) {
//        return PersonalRulesHelper.booleanOrElse(igniter, rule, instance, original);
//    }
//
//    @WrapOperation(
//        method = "onDestroyedByExplosion", at = @At(
//        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
//    private boolean getPersonalByExplosion(
//        GameRules instance,
//        GameRules.Key<GameRules.BooleanRule> rule,
//        Operation<Boolean> original,
//        @Local(argsOnly = true) Explosion explosion
//    ) {
//        return PersonalRulesHelper.booleanOrElse(explosion.getCausingEntity(), rule, instance, original);
//    }
//
//    @WrapOperation(
//        method = "onUseWithItem", at = @At(
//        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
//    private boolean getPersonalOnUse(
//        GameRules instance,
//        GameRules.Key<GameRules.BooleanRule> rule,
//        Operation<Boolean> original,
//        @Local(argsOnly = true) PlayerEntity player
//    ) {
//        return PersonalRulesHelper.booleanOrElse(player, rule, instance, original);
//    }
}
