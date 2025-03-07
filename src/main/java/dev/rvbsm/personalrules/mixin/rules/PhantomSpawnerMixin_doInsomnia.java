package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.spawner.PhantomSpawner;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(PhantomSpawner.class)
public abstract class PhantomSpawnerMixin_doInsomnia {

    @ModifyExpressionValue(
        method = "spawn",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"))
    private boolean ignoresInsomnia(boolean original, @Local ServerPlayerEntity player) {
        return original || !PersonalRulesHelper.getBoolean(player, GameRules.DO_INSOMNIA).orElse(true);
    }
}
