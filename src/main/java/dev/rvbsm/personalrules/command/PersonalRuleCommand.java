package dev.rvbsm.personalrules.command;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

import java.util.Optional;

import dev.rvbsm.personalrules.PersonalRulesMod;
import dev.rvbsm.personalrules.PersonalRulesTranslation;
import dev.rvbsm.personalrules.api.PersonalRulesAccess;
import dev.rvbsm.personalrules.mixin.rules.GameRulesAccess;
import dev.rvbsm.personalrules.player.PersonalRules;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public final class PersonalRuleCommand {

    public static void register(
        CommandDispatcher<ServerCommandSource> dispatcher,
        CommandRegistryAccess registryAccess
    ) {
        final LiteralArgumentBuilder<ServerCommandSource> commandBuilder = CommandManager.literal("personalrule")
            .requires(src -> PersonalRulesMod.getInstance().getPersonalRulesManager().hasPermissionLevel(src));

        new GameRules(registryAccess.getEnabledFeatures()).accept(new GameRules.Visitor() {
            @Override
            public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                if (PersonalRules.SUPPORTED_RULES.contains(key)) {
                    commandBuilder.then(CommandManager.literal(key.getName())
                        .requires(src -> PersonalRulesMod.getInstance()
                            .getPersonalRulesManager()
                            .hasPermissionLevel(src, key))
                        .executes(ctx -> executeQuery(ctx.getSource(), key))
                        .then(type.argument("value").executes(ctx -> executeApply(ctx, key)))
                        .then(CommandManager.literal("reset").executes(ctx -> executeRevoke(ctx.getSource(), key))));
                }
            }
        });

        dispatcher.register(commandBuilder);
    }

    private static <T extends GameRules.Rule<T>> int executeApply(
        CommandContext<ServerCommandSource> ctx,
        GameRules.Key<T> key
    ) {
        final ServerCommandSource src = ctx.getSource();
        final ServerPlayerEntity player = src.getPlayer();
        final ServerWorld world = src.getWorld();

        final Optional<T> personalRule = ((PersonalRulesAccess) player).personalrules$getPersonalRules().applyRule(key);
        final T gameRule = world.getGameRules().get(key);

        personalRule.ifPresentOrElse(
            rule -> {
                ((GameRulesAccess.RuleAccess) rule).callSetFromArgument(ctx, "value");
                PersonalRules.changed(player, key, rule);

                src.sendMessage(PersonalRulesTranslation.translatable(
                    "command",
                    "personalrule.set",
                    key.getName(),
                    rule.toString(),
                    gameRule.toString()));
            },
            () -> src.sendError(PersonalRulesTranslation.translatable(
                "command",
                "personalrule.deactivated",
                key.getName(),
                gameRule.toString())));

        return personalRule.map(GameRules.Rule::getCommandResult).orElse(0);
    }

    private static <T extends GameRules.Rule<T>> int executeRevoke(ServerCommandSource src, GameRules.Key<T> key) {
        final ServerPlayerEntity player = src.getPlayer();
        final ServerWorld world = src.getWorld();

        ((PersonalRulesAccess) player).personalrules$getPersonalRules().revokeRule(key);
        final T gameRule = world.getGameRules().get(key);
        PersonalRules.changed(player, key, gameRule);

        src.sendMessage(PersonalRulesTranslation.translatable(
            "command",
            "personalrule.deactivated",
            key.getName(),
            gameRule.toString()));

        return Command.SINGLE_SUCCESS;
    }

    private static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource src, GameRules.Key<T> key) {
        final ServerPlayerEntity player = src.getPlayer();
        final ServerWorld world = src.getWorld();

        final Optional<T> personalRule = ((PersonalRulesAccess) player).personalrules$getPersonalRules().get(key);
        final T gameRule = world.getGameRules().get(key);

        personalRule.ifPresentOrElse(
            rule -> src.sendMessage(PersonalRulesTranslation.translatable(
                "command",
                "personalrule.query",
                key.getName(),
                rule.toString(),
                gameRule.toString())),
            () -> src.sendError(PersonalRulesTranslation.translatable(
                "command",
                "personalrule.deactivated",
                key.getName(),
                gameRule.toString())));

        return personalRule.map(GameRules.Rule::getCommandResult).orElse(0);
    }
}
