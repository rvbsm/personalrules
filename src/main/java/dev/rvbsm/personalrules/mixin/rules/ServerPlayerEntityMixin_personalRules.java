package dev.rvbsm.personalrules.mixin.rules;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.rvbsm.personalrules.api.PersonalRulesAccess;
import dev.rvbsm.personalrules.player.PersonalRules;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin_personalRules extends PlayerEntity implements PersonalRulesAccess {

    @Unique
    private static final String PERSONAL_RULES_KEY = "personalrules:PersonalRules";

    @Unique
    private PersonalRules personalRules;

    private ServerPlayerEntityMixin_personalRules(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initPersonalRules(CallbackInfo ci) {
        this.personalRules = new PersonalRules(this.getWorld().getEnabledFeatures());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readPersonalRules(NbtCompound nbt, CallbackInfo ci) {
        final Dynamic<NbtElement> nbtDynamic = new Dynamic<>(NbtOps.INSTANCE, nbt.get(PERSONAL_RULES_KEY));
        this.personalRules = new PersonalRules(this.getWorld().getEnabledFeatures(), nbtDynamic);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writePersonalRules(NbtCompound nbt, CallbackInfo ci) {
        nbt.put(PERSONAL_RULES_KEY, this.personalRules.toNbt());
    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    private void copyPersonalRules(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.personalRules = ((PersonalRulesAccess) oldPlayer).personalrules$getPersonalRules();
    }

    @Override
    public @NotNull PersonalRules personalrules$getPersonalRules() {
        return this.personalRules;
    }
}
