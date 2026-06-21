package games.enchanted.eg_bedrock_books.common.util;

import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

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

    public static void setScreen(Minecraft minecraft, Screen screen) {
        //? if minecraft: <= 26.1 {
        /*minecraft.setScreen(screen);
        *///? } else {
        minecraft.gui.setScreen(screen);
        //? }
    }
}
