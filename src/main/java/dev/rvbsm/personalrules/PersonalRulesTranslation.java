package dev.rvbsm.personalrules;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PersonalRulesTranslation {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalRulesTranslation.class);
    private static final Gson GSON = new GsonBuilder().setStrictness(Strictness.LENIENT).create();

    private static final String LANG_FORMAT = PersonalRulesMod.ASSETS_ROOT + "/lang/%s.json";

    private static Map<String, String> translations = Collections.emptyMap();

    public static void load(String lang) {
        final var langPath = LANG_FORMAT.formatted(lang);
        try (final var input = PersonalRulesTranslation.class.getResourceAsStream(langPath)) {
            if (input != null) {
                try (final var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                    translations = GSON.fromJson(reader, new TypeToken<>() {});
                    return;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load translations", e);
            translations = Collections.emptyMap();
        }

        translations = Collections.emptyMap();
    }

    public static MutableText translatable(String type, String key, Object... args) {
        final var translationKey = "%s.%s.%s".formatted(PersonalRulesMod.ID, type, key);
        final String translation = translations.getOrDefault(translationKey, translationKey);

        return Text.literal(translation.formatted(args));
    }
}
