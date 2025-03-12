package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.util.Optional;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin_doMobLoot {

    @Shadow
    public abstract ServerPlayerEntity getLovingPlayer();

    @WrapOperation(
        method = "breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;Lnet/minecraft/entity/passive/PassiveEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.BooleanRule> rule,
        Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 1) AnimalEntity other
    ) {
        final ServerPlayerEntity player = Optional.ofNullable(this.getLovingPlayer()).orElseGet(other::getLovingPlayer);
        return PersonalRulesHelper.booleanOrElse(player, rule, instance, original);
    }
}
