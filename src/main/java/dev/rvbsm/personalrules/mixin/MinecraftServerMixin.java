package dev.rvbsm.personalrules.mixin;

import net.minecraft.server.MinecraftServer;

import dev.rvbsm.personalrules.PersonalRulesMod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(
        method = "runServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z",
            shift = At.Shift.AFTER))
    private void onServerFinishSetup(CallbackInfo ci) {
        PersonalRulesMod.getInstance().onServerStarted((MinecraftServer) (Object) this);
    }

    @Inject(
        method = "runServer",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;shutdown()V"))
    private void onServerShutdown(CallbackInfo ci) {
        PersonalRulesMod.getInstance().onServerStopped();
    }
}
