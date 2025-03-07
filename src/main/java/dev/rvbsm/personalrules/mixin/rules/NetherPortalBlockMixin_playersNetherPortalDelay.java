package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin_playersNetherPortalDelay {

    @WrapOperation(
        method = "getPortalDelay", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getInt(Lnet/minecraft/world/GameRules$Key;)I"))
    private int getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.IntRule> rule,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return PersonalRulesHelper.integerOrElse((PlayerEntity) entity, rule, instance, original);
    }
}
