package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(TntEntity.class)
public abstract class TntEntityMixin_tntExplodes {

    @Shadow
    public abstract @Nullable LivingEntity getOwner();

    @WrapOperation(
        method = "explode", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.BooleanRule> rule,
        Operation<Boolean> original
    ) {
        if (this.getOwner() instanceof PlayerEntity player) {
            return PersonalRulesHelper.booleanOrElse(player, rule, instance, original);
        }

        return original.call(instance, rule);
    }
}
