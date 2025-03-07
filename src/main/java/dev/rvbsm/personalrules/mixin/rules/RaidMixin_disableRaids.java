package dev.rvbsm.personalrules.mixin.rules;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Mixin(Raid.class)
public abstract class RaidMixin_disableRaids {

    @Shadow
    @Final
    private ServerBossBar bar;

    @Shadow
    public abstract void invalidate();

    @Inject(
        method = "tick", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/village/raid/Raid;updateBarToPlayers()V", shift = At.Shift.AFTER))
    private void isDisabled(CallbackInfo ci) {
        final boolean allDisabledRaid = this.bar.getPlayers()
            .stream()
            .allMatch(player -> PersonalRulesHelper.getBoolean(player, GameRules.DISABLE_RAIDS).orElse(false));

        if (allDisabledRaid) {
            this.invalidate();
        }
    }
}
