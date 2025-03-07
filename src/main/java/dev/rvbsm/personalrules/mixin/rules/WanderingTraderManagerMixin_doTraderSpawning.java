package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.WanderingTraderManager;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(WanderingTraderManager.class)
public abstract class WanderingTraderManagerMixin_doTraderSpawning {

    @ModifyExpressionValue(
        method = "trySpawn", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/world/ServerWorld;getRandomAlivePlayer()Lnet/minecraft/server/network/ServerPlayerEntity;"))
    private ServerPlayerEntity canSpawn(
        @Nullable ServerPlayerEntity original,
        @Cancellable CallbackInfoReturnable<Boolean> cir
    ) {
        if (original != null && !PersonalRulesHelper.getBoolean(original, GameRules.DO_TRADER_SPAWNING).orElse(true)) {
            cir.setReturnValue(false);
        }

        return original;
    }
}
