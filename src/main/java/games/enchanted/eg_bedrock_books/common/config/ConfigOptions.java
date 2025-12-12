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
import games.enchanted.eg_bedrock_books.common.util.InputUtil;
import games.enchanted.eg_bedrock_books.platform.PlatformHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigOptions {
    // general
    public static final ConfigOption<Boolean> CLOSE_BOOK_WHEN_RUNNING_COMMAND;
    public static final ConfigOption<InputConstants.Key> MOVE_FORWARD_PAGE_KEY;
    public static final ConfigOption<InputConstants.Key> MOVE_BACKWARD_PAGE_KEY;

    // visual
    public static final ConfigOption<Boolean> SHOW_X_BUTTON;
    public static final ConfigOption<Integer> RIBBON_HEIGHT;
    public static final ConfigOption<Boolean> IMPROVE_TEXT_CONTRAST_IN_HC;
    public static final ConfigOption<Boolean> AUTO_ENABLE_BEDROCK_BOOKS_HC_PACK;

    // screen preference
    public static final ConfigOption<InputConstants.Key> INVERSE_SCREEN_PREFERENCE_KEY;
    public static final ConfigOption<Boolean> PREFER_VANILLA_EDIT_SCREEN;
    public static final ConfigOption<Boolean> PREFER_VANILLA_WRITTEN_SCREEN;
    public static final ConfigOption<Boolean> PREFER_VANILLA_LECTERN_SCREEN;
    public static final ConfigOption<Boolean> PREFER_VANILLA_SIGN_SCREEN;

    // debug
    public static final ConfigOption<Boolean> DEBUG_TEXT_BOUNDS;
    public static final ConfigOption<Boolean> DEBUG_WIDGET_BOUNDS;
    public static final ConfigOption<Boolean> DEBUG_CONTAINER_DATA;
    public static final ConfigOption<Boolean> DEBUG_VARIABLES;

    private static final List<ConfigOption<?>> OPTIONS = new ArrayList<>();

    static {
        CLOSE_BOOK_WHEN_RUNNING_COMMAND = registerOption(new BoolOption(
            true,
            true,
            "close_book_when_running_command"
        ));

        MOVE_FORWARD_PAGE_KEY = registerOption(new KeyOption(
            InputUtil.getKey(InputConstants.KEY_PAGEDOWN),
            InputUtil.getKey(InputConstants.KEY_PAGEDOWN),
            "move_forward_page_key"
        ));

        MOVE_BACKWARD_PAGE_KEY = registerOption(new KeyOption(
            InputUtil.getKey(InputConstants.KEY_PAGEUP),
            InputUtil.getKey(InputConstants.KEY_PAGEUP),
            "move_backward_page_key"
        ));


        SHOW_X_BUTTON = registerOption(new BoolOption(
            true,
            true,
            "show_x_button"
        ));

        RIBBON_HEIGHT = registerOption(new IntOption(
            76,
            76,
            "ribbon_height"
        ));

        IMPROVE_TEXT_CONTRAST_IN_HC = registerOption(new BoolOption(
            true,
            true,
            "improve_text_contrast_in_hc"
        ));

        AUTO_ENABLE_BEDROCK_BOOKS_HC_PACK = registerOption(new BoolOption(
            true,
            true,
            "auto_enable_bedrock_books_hc_pack"
        ));


        INVERSE_SCREEN_PREFERENCE_KEY = registerOption(new KeyOption(
            InputUtil.getKey(InputConstants.KEY_LALT),
            InputUtil.getKey(InputConstants.KEY_LALT),
            "inverse_screen_preference_key"
        ));
        PREFER_VANILLA_EDIT_SCREEN = registerOption(new BoolOption(
            false,
            false,
            "prefer_vanilla_edit_screen"
        ));
        PREFER_VANILLA_WRITTEN_SCREEN = registerOption(new BoolOption(
            false,
            false,
            "prefer_vanilla_written_screen"
        ));
        PREFER_VANILLA_LECTERN_SCREEN = registerOption(new BoolOption(
            false,
            false,
            "prefer_vanilla_lectern_screen"
        ));
        PREFER_VANILLA_SIGN_SCREEN = registerOption(new BoolOption(
            false,
            false,
            "prefer_vanilla_sign_screen"
        ));


        DEBUG_TEXT_BOUNDS = registerOption(new BoolOption(
            false,
            false,
            "debug_text_bounds"
        ));

        DEBUG_WIDGET_BOUNDS = registerOption(new BoolOption(
            false,
            false,
            "debug_widget_bounds"
        ));

        DEBUG_CONTAINER_DATA = registerOption(new BoolOption(
            false,
            false,
            "debug_container_data"
        ));

        DEBUG_VARIABLES = registerOption(new BoolOption(
            false,
            false,
            "debug_variables"
        ));
    }

    private static <T> ConfigOption<T> registerOption(ConfigOption<T> option) {
        OPTIONS.add(option);
        return option;
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

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

    public static void resetAndSaveAllOptions() {
        for (ConfigOption<?> option : OPTIONS) {
            option.resetToDefault(true);
        }
        saveConfig();
    }

    public static void clearAllPendingValues() {
        for (ConfigOption<?> option : OPTIONS) {
            option.clearPendingValue();
        }
    }
}
