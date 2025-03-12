package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin_forgiveDeadPlayer {

    @WrapOperation(
        method = "onDeath", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z",
        ordinal = 1))
    private boolean getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.BooleanRule> rule,
        Operation<Boolean> original
    ) {
        return PersonalRulesHelper.booleanOrElse((ServerPlayerEntity) (Object) this, rule, instance, original);
    }
}
