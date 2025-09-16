package games.enchanted.eg_bedrock_books.common.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import games.enchanted.eg_bedrock_books.common.Logging;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.config.option.BoolOption;
import games.enchanted.eg_bedrock_books.common.config.option.ConfigOption;
import games.enchanted.eg_bedrock_books.common.config.option.IntOption;
import games.enchanted.eg_bedrock_books.platform.PlatformHelper;

import java.io.*;
import java.util.List;

public class ConfigOptions {
    public static final ConfigOption<Boolean> KEEP_BOOK_OPEN_WHEN_RUNNING_COMMAND = new BoolOption(
        false,
        false,
        "keep_book_open_when_running_command"
    );

    public static final ConfigOption<Integer> RIBBON_HEIGHT = new IntOption(
        22,
        22,
        "ribbon_height"
    );


    private static final List<ConfigOption<?>> OPTIONS = List.of(
        KEEP_BOOK_OPEN_WHEN_RUNNING_COMMAND,
        RIBBON_HEIGHT
    );

    private static final String FILE_NAME = ModConstants.MOD_ID + ".json";

    private static File getConfigFile() {
        return PlatformHelper.getConfigPath().resolve(FILE_NAME).toFile();
    }

    public static void saveIfAnyDirtyOptions() {
        if(OPTIONS.stream().noneMatch((ConfigOption::isDirty))) return;
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
