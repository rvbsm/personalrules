package dev.rvbsm.personalrules.mixin.rules;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.GameRules;

import dev.rvbsm.personalrules.player.PersonalRulesHelper;

@Debug(export = true)
@Mixin(Raid.class)
public abstract class RaidMixin_disableRaids {

    @Shadow
    public abstract void invalidate();

    @Inject(
        method = "updateBarToPlayers", at = @At(
        value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private void havePartyDisabledRaid_it(
        CallbackInfo ci,
        @Share("partyDisabledRaid") LocalBooleanRef partyDisabledRaid,
        @Local ServerPlayerEntity player
    ) {
        if (partyDisabledRaid.get() && !PersonalRulesHelper.getBoolean(player, GameRules.DISABLE_RAIDS).orElse(false)) {
            partyDisabledRaid.set(false);
        }
    }

    // FIXME: this should NOT be done inside this method
    //        but i couldn't think of other way without iterating through all players again
    @Inject(method = "updateBarToPlayers", at = @At("TAIL"))
    private void havePartyDisabledRaid_tail(
        CallbackInfo ci,
        @Share("partyDisabledRaid") LocalBooleanRef partyDisabledRaid
    ) {
        if (partyDisabledRaid.get()) {
            this.invalidate();
        }
    }
}
