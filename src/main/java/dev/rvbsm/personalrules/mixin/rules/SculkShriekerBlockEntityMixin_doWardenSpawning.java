package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(SculkShriekerBlockEntity.class)
public abstract class SculkShriekerBlockEntityMixin_doWardenSpawning {

    @WrapOperation(
        method = "shriek(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/entity/SculkShriekerBlockEntity;canWarn(Lnet/minecraft/server/world/ServerWorld;)Z"))
    private boolean canWarn(
        SculkShriekerBlockEntity instance,
        ServerWorld world,
        Operation<Boolean> original,
        @Local(argsOnly = true) ServerPlayerEntity player
    ) {
        return PersonalRulesHelper.getBoolean(player, GameRules.DO_WARDEN_SPAWNING)
            .orElseGet(() -> original.call(instance, world));
    }
}
