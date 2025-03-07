package dev.rvbsm.personalrules.command;

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dev.rvbsm.personalrules.PersonalRulesManager;
import dev.rvbsm.personalrules.PersonalRulesTranslation;
import dev.rvbsm.personalrules.api.PersonalRulesAccess;
import dev.rvbsm.personalrules.mixin.rules.GameRulesAccess;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public final class PersonalRuleCommand {

    public static void register(
        CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        final LiteralArgumentBuilder<ServerCommandSource> commandBuilder = CommandManager.literal("personalrule")
            .requires(src -> src.isExecutedByPlayer() &&
                src.hasPermissionLevel(PersonalRulesManager.getCommandPermissionLevel()));

        commandBuilder.executes(ctx -> executeList(ctx.getSource()))
            .then(CommandManager.literal("remove")
                .then(CommandManager.argument("personalrule", StringArgumentType.word())
                    .suggests((ctx, b) -> ruleSuggestions(ctx.getSource(), b))
                    .executes(ctx -> executeRemove(ctx.getSource(), getFromArgument(ctx, "personalrule")))))
            .then(CommandManager.argument("personalrule", StringArgumentType.word())
                .suggests((ctx, b) -> ruleSuggestions(ctx.getSource(), b))
                .executes(ctx -> executeQuery(ctx.getSource(), getFromArgument(ctx, "personalrule")))
                .then(CommandManager.argument("value", StringArgumentType.word())
                    .suggests((ctx, b) -> valueSuggestions(ctx, "personalrule", b))
                    .executes(ctx -> executeSet(ctx, getFromArgument(ctx, "personalrule")))));

        dispatcher.register(commandBuilder);
    }

    private static GameRules.Key<?> getFromArgument(CommandContext<ServerCommandSource> ctx, String name) {
        final String rule = StringArgumentType.getString(ctx, name);
        return PersonalRulesManager.SUPPORTED_KEYS.get(rule);
    }

    private static void setFromArgument(GameRules.Rule<?> rule, CommandContext<ServerCommandSource> ctx, String name) {
        final String value = StringArgumentType.getString(ctx, name);

        switch (rule) {
            case GameRules.BooleanRule booleanRule -> booleanRule.set(Boolean.parseBoolean(value), null);
            case GameRules.IntRule intRule -> intRule.set(Integer.parseInt(value, 10), null);

            default -> throw new IllegalArgumentException("Tried to modify unknown rule type");
        }
    }

    private static CompletableFuture<Suggestions> ruleSuggestions(ServerCommandSource src, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(
            PersonalRulesManager.getPersonalRules()
                .entrySet()
                .stream()
                .filter(entry -> src.hasPermissionLevel(entry.getValue()))
                .map(entry -> entry.getKey().getName()), builder);
    }

    private static CompletableFuture<Suggestions> valueSuggestions(
        CommandContext<ServerCommandSource> ctx,
        String name,
        SuggestionsBuilder builder
    ) {
        final GameRules.Key<?> key = getFromArgument(ctx, name);
        final GameRules.Type<?> type = GameRulesAccess.getRuleTypes().get(key);
        final ArgumentType<?> argumentType = ((GameRulesAccess.TypeAccess) type).getArgumentType().get();

        return argumentType.listSuggestions(ctx, builder);
    }

    private static <T extends GameRules.Rule<T>> int executeSet(
        CommandContext<ServerCommandSource> ctx,
        GameRules.Key<T> key
    ) {
        final ServerCommandSource src = ctx.getSource();
        final ServerPlayerEntity player = src.getPlayer();
        final ServerWorld world = src.getWorld();

        final Optional<T> personalRule = ((PersonalRulesAccess) player).personalrules$getPersonalRules().addRule(key);
        final T gameRule = world.getGameRules().get(key);

        personalRule.ifPresentOrElse(
            rule -> {
                setFromArgument(rule, ctx, "value");
                ((PersonalRulesAccess) player).personalrules$getPersonalRules().update(key, rule);

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

    private static <T extends GameRules.Rule<T>> int executeRemove(ServerCommandSource src, GameRules.Key<T> key) {
        final ServerPlayerEntity player = src.getPlayer();
        final ServerWorld world = src.getWorld();

        ((PersonalRulesAccess) player).personalrules$getPersonalRules().removeRule(key);
        final T gameRule = world.getGameRules().get(key);
        ((PersonalRulesAccess) player).personalrules$getPersonalRules().update(key, gameRule);

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

    private static int executeList(ServerCommandSource src) {
        final ServerPlayerEntity player = src.getPlayer();
        final ServerWorld world = src.getWorld();

        final Map<GameRules.Key<?>, GameRules.Rule<?>> rules = ((PersonalRulesAccess) player).personalrules$getPersonalRules()
            .getRules();

        if (rules.isEmpty()) {
            src.sendError(PersonalRulesTranslation.translatable("command", "personalrule.list.empty"));
            return 0;
        }

        final MutableText message = Text.empty();
        message.append(PersonalRulesTranslation.translatable("command", "personalrule.list.head")
            .formatted(Formatting.BOLD));

        rules.forEach((key, rule) -> {
            message.append("\n");

            final GameRules.Rule<?> gameRule = world.getGameRules().get(key);
            message.append(PersonalRulesTranslation.translatable(
                "command",
                "personalrule.list.rule",
                key.getName(),
                rule.toString(),
                gameRule.toString()));
        });

        src.sendMessage(message);
        return rules.size();
    }
}
