package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

//@Mixin(TntMinecartEntity.class)
public abstract class TntMinecartEntityMixin_tntExplodes {

//    @WrapOperation(
//        method = {"explode(Lnet/minecraft/entity/damage/DamageSource;D)V", "prime"}, at = @At(
//        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
//    private boolean getPersonal(
//        GameRules instance,
//        GameRules.Key<GameRules.BooleanRule> rule,
//        Operation<Boolean> original,
//        @Local(argsOnly = true) DamageSource source
//    ) {
//        return PersonalRulesHelper.booleanOrElse(source.getAttacker(), rule, instance, original);
//    }
}
