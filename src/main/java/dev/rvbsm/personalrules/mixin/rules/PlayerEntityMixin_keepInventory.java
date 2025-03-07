package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Optional;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin_keepInventory extends LivingEntity {

    protected PlayerEntityMixin_keepInventory(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(
        method = {"dropInventory", "getExperienceToDrop"}, at = @At(
        value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean getPersonal(
        GameRules instance,
        GameRules.Key<GameRules.BooleanRule> rule,
        Operation<Boolean> original
    ) {
        return PersonalRulesHelper.getBoolean((PlayerEntity) (Object) this, rule)
            .or(() -> Optional.ofNullable(this.getPrimeAdversary())
                .filter(PlayerEntity.class::isInstance)
                .flatMap(adv -> PersonalRulesHelper.getBoolean((PlayerEntity) adv, rule)))
            .orElseGet(() -> original.call(instance, rule));
    }
}
