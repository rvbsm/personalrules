package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Debug(export = true)
@Mixin(RaidManager.class)
public abstract class RaidManagerMixin_disableRaids {

    @WrapOperation(
        method = "startRaid", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.BooleanRule> rule,
        Operation<Boolean> original,
        @Local(argsOnly = true) ServerPlayerEntity player
    ) {
        return PersonalRulesHelper.booleanOrElse(player, rule, instance, original);
    }
}
