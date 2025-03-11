package dev.rvbsm.personalrules.mixin;

import net.minecraft.server.MinecraftServer;

import dev.rvbsm.personalrules.PersonalRulesMod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(
        method = "runServer",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"))
    private void onServerSetup(CallbackInfo ci) {
        PersonalRulesMod.getInstance().onServerStarting((MinecraftServer) (Object) this);
    }

    @Inject(
        method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;shutdown()V"))
    private void onServerShutdown(CallbackInfo ci) {
        PersonalRulesMod.getInstance().onServerStopping();
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void onServerSaving(
        boolean suppressLogs,
        boolean flush,
        boolean force,
        CallbackInfoReturnable<Boolean> cir
    ) {
        PersonalRulesMod.getInstance().onServerSaving(force);
    }
}
