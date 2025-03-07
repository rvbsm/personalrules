package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin_keepInventory extends PlayerEntityMixin_keepInventory {

    protected ServerPlayerEntityMixin_keepInventory(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(
        method = "copyFrom", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.BooleanRule> rule,
        Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 1) ServerPlayerEntity player
    ) {
        return PersonalRulesHelper.booleanOrElse(player, rule, instance, original);
    }
}
