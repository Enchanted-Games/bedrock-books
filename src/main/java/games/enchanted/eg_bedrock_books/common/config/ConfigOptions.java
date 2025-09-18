package games.enchanted.eg_bedrock_books.common.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.Logging;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.config.option.BoolOption;
import games.enchanted.eg_bedrock_books.common.config.option.ConfigOption;
import games.enchanted.eg_bedrock_books.common.config.option.IntOption;
import games.enchanted.eg_bedrock_books.common.config.option.KeyOption;
import games.enchanted.eg_bedrock_books.platform.PlatformHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigOptions {
    // general
    public static final ConfigOption<Boolean> CLOSE_BOOK_WHEN_RUNNING_COMMAND;
    public static final ConfigOption<Boolean> VANILLA_BOOK_KEY_ENABLED;
    public static final ConfigOption<InputConstants.Key> VANILLA_BOOK_KEY;

    // visual
    public static final ConfigOption<Integer> RIBBON_HEIGHT;

    // debug
    public static final ConfigOption<Boolean> DEBUG_TEXT_BOUNDS;
    public static final ConfigOption<Boolean> DEBUG_WIDGET_BOUNDS;
    public static final ConfigOption<Boolean> DEBUG_CONTAINER_DATA;

    private static final List<ConfigOption<?>> OPTIONS = new ArrayList<>();

    static {
        CLOSE_BOOK_WHEN_RUNNING_COMMAND = new BoolOption(
            true,
            false,
            "close_book_when_running_command"
        );
        registerOption(CLOSE_BOOK_WHEN_RUNNING_COMMAND);

        VANILLA_BOOK_KEY_ENABLED = new BoolOption(
            true,
            true,
            "vanilla_book_key_enabled"
        );
        registerOption(VANILLA_BOOK_KEY_ENABLED);

        VANILLA_BOOK_KEY = new KeyOption(
            InputConstants.getKey(InputConstants.KEY_LALT, 0),
            InputConstants.getKey(InputConstants.KEY_LALT, 0),
            "vanilla_book_key"
        );
        registerOption(VANILLA_BOOK_KEY);


        RIBBON_HEIGHT = new IntOption(
            22,
            22,
            "ribbon_height"
        );
        registerOption(RIBBON_HEIGHT);


        DEBUG_TEXT_BOUNDS = new BoolOption(
            false,
            false,
            "debug_text_bounds"
        );
        registerOption(DEBUG_TEXT_BOUNDS);

        DEBUG_WIDGET_BOUNDS = new BoolOption(
            false,
            false,
            "debug_widget_bounds"
        );
        registerOption(DEBUG_WIDGET_BOUNDS);

        DEBUG_CONTAINER_DATA = new BoolOption(
            false,
            false,
            "debug_container_Data"
        );
        registerOption(DEBUG_CONTAINER_DATA);
    }

    private static void registerOption(ConfigOption<?> option) {
        OPTIONS.add(option);
    }

    private static final String FILE_NAME = ModConstants.MOD_ID + ".json";

    private static File getConfigFile() {
        return PlatformHelper.getConfigPath().resolve(FILE_NAME).toFile();
    }

    public static void saveIfAnyDirtyOptions() {
        if(OPTIONS.stream().noneMatch(ConfigOption::isDirty)) return;
        for (ConfigOption<?> option : OPTIONS) {
            if(option.isDirty()) option.applyPendingValue();
        }
        saveConfig();
    }

    public static void saveConfig() {
        JsonObject root = new JsonObject();

        for (ConfigOption<?> option : OPTIONS) {
            root.add(option.getJsonKey(), option.toJson());
        }

        Gson gson = new Gson();
        String encodedJson = gson.toJson(root);

        try (FileWriter writer = new FileWriter(getConfigFile())) {
            writer.write(encodedJson);
        } catch (IOException e) {
            Logging.error("Failed to write config file '{}', {}", FILE_NAME, e);
        }
    }

    public static void readConfig() {
        Gson gson = new Gson();
        JsonObject decodedConfig = new JsonObject();

        try {
            JsonReader jsonReader = gson.newJsonReader(new FileReader(getConfigFile()));
            jsonReader.setStrictness(Strictness.LENIENT);
            decodedConfig = JsonParser.parseReader(jsonReader).getAsJsonObject();
        } catch (JsonParseException e) {
            Logging.error("Failed to parse config file '{}', {}", FILE_NAME, e);
        } catch (FileNotFoundException e) {
            Logging.info("Config file '{}' not found", FILE_NAME);
            saveConfig();
        }

        for (ConfigOption<?> option : OPTIONS) {
            option.fromJson(decodedConfig);
        }
    }
}
