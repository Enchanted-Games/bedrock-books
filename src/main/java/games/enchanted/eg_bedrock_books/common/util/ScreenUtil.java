package games.enchanted.eg_bedrock_books.common.util;

import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;

public class ScreenUtil {
    public static boolean shouldOpenVanillaEditScreen() {
        return ConfigOptions.PREFER_VANILLA_EDIT_SCREEN.getValue() != InputUtil.vanillaBookKeyHeld();
    }

    public static boolean shouldOpenVanillaWrittenScreen() {
        return ConfigOptions.PREFER_VANILLA_WRITTEN_SCREEN.getValue() != InputUtil.vanillaBookKeyHeld();
    }

    public static boolean shouldOpenVanillaLecternScreen() {
        return ConfigOptions.PREFER_VANILLA_LECTERN_SCREEN.getValue() != InputUtil.vanillaBookKeyHeld();
    }
}
