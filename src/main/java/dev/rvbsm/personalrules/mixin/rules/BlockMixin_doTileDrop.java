package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(Block.class)
public abstract class BlockMixin_doTileDrop {

    @WrapWithCondition(
        method = "afterBreak", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V"))
    private boolean shouldDrop(
        BlockState state,
        World world,
        BlockPos pos,
        BlockEntity blockEntity,
        Entity entity,
        ItemStack tool
    ) {
        if (entity instanceof PlayerEntity player) {
            return PersonalRulesHelper.getBoolean(player, GameRules.DO_TILE_DROPS).orElse(true);
        }

        return true;
    }
}
