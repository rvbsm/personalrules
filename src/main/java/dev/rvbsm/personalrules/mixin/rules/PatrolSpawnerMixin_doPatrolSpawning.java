package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.spawner.PatrolSpawner;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(PatrolSpawner.class)
public abstract class PatrolSpawnerMixin_doPatrolSpawning {

    @ModifyExpressionValue(
        method = "spawn",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
    private boolean ignoresPatrol(boolean original, @Local PlayerEntity player) {
        return original || !PersonalRulesHelper.getBoolean(player, GameRules.DO_PATROL_SPAWNING).orElse(true);
    }
}
