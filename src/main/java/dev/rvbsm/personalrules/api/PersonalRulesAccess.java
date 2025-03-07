package dev.rvbsm.personalrules.api;

import org.jetbrains.annotations.NotNull;

import dev.rvbsm.personalrules.player.PersonalRules;

public interface PersonalRulesAccess {

    @NotNull PersonalRules personalrules$getPersonalRules();
}
