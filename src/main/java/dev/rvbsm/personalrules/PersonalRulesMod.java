package dev.rvbsm.personalrules;

import dev.rvbsm.personalrules.command.PersonalRuleCommand;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Language;

import net.fabricmc.api.ModInitializer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class PersonalRulesMod implements ModInitializer {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "personalrules";
    public static final String ASSETS_ROOT = "/assets/" + ID;

    private static PersonalRulesMod instance;

    private PersonalRulesManager personalRulesManager;

    public static PersonalRulesMod getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Cannot get an instance before initialisation phase");
        }

        return instance;
    }

    @Override
    public void onInitialize() {
        instance = this;

        PersonalRulesTranslation.load(Language.DEFAULT_LANGUAGE); // TODO?: multi-lang
    }

    public void onServerStarting(MinecraftServer server) {
        this.personalRulesManager = new PersonalRulesManager(server);
        this.personalRulesManager.load();
    }

    public void onServerStopped() {
        this.personalRulesManager.unload();
        this.personalRulesManager = null;
    }

    public void onServerSaving(boolean force) {
        this.personalRulesManager.saveConfig(force);
    }

    public void registerCommands(
        CommandDispatcher<ServerCommandSource> dispatcher,
        CommandManager.RegistrationEnvironment environment,
        CommandRegistryAccess registryAccess
    ) {
        PersonalRuleCommand.register(dispatcher, registryAccess);
    }

    public PersonalRulesManager getPersonalRulesManager() {
        return this.personalRulesManager;
    }
}
