package dev.rvbsm.personalrules.mixin.rules;

import com.mojang.brigadier.arguments.ArgumentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.world.GameRules;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Mixin(GameRules.class)
public interface GameRulesAccess {

    @Accessor("RULE_TYPES")
    static Map<GameRules.Key<?>, GameRules.Type<?>> getRuleTypes() {
        throw new AssertionError("replaced by mixin");
    }

    @Invoker("streamAllRules")
    static Stream<Map.Entry<GameRules.Key<?>, GameRules.Type<?>>> callStreamAllRules(FeatureSet enabledFeatures) {
        throw new AssertionError("replaced by mixin");
    }

    @Mixin(GameRules.Rule.class)
    interface RuleAccess {

        @Invoker("deserialize")
        void callDeserialize(String value);
    }

    @Mixin(GameRules.Type.class)
    interface TypeAccess {

        @Accessor("argumentType")
        Supplier<ArgumentType<?>> getArgumentType();
    }
}
