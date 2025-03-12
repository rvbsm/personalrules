package dev.rvbsm.personalrules.mixin.rules;

import com.mojang.brigadier.context.CommandContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

import java.util.Map;
import java.util.stream.Stream;

@Mixin(GameRules.class)
public interface GameRulesAccess {

    @Invoker("streamAllRules")
    static Stream<Map.Entry<GameRules.Key<?>, GameRules.Type<?>>> callStreamAllRules(FeatureSet enabledFeatures) {
        throw new AssertionError("replaced by mixin");
    }

    @Mixin(GameRules.Rule.class)
    interface RuleAccess {

        @Invoker("deserialize")
        void callDeserialize(String value);

        @Invoker("setFromArgument")
        void callSetFromArgument(CommandContext<ServerCommandSource> context, String name);
    }
}
