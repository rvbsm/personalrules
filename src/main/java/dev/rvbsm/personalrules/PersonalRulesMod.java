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

    public static PersonalRulesMod getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        instance = this;

        PersonalRulesTranslation.load(Language.DEFAULT_LANGUAGE); // TODO?: multi-lang
    }

    public void onServerStarted(MinecraftServer server) {
        PersonalRulesManager.load(server);
    }

    public void onServerStopped() {
        PersonalRulesManager.unload();
    }

    public void registerCommands(
        CommandDispatcher<ServerCommandSource> dispatcher,
        CommandManager.RegistrationEnvironment environment,
        CommandRegistryAccess registryAccess
    ) {
        PersonalRuleCommand.register(dispatcher);
    }
}
