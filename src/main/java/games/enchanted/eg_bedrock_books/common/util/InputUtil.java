package games.enchanted.eg_bedrock_books.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import org.lwjgl.glfw.GLFW;

//? if minecraft: >= 1.21.9 {
import net.minecraft.client.input.KeyEvent;
//?}

public class InputUtil {
    public static boolean shouldOpenVanillaBook() {
        if(!ConfigOptions.VANILLA_BOOK_KEY_ENABLED.getValue()) return false;
        InputConstants.Key key = ConfigOptions.VANILLA_BOOK_KEY.getValue();
        return InputConstants.isKeyDown(McUtil.getWindow(), key.getValue());
    }

    public static boolean shouldShowDebugTextBound() {
        if(!ConfigOptions.DEBUG_TEXT_BOUNDS.getValue()) return false;
        return InputConstants.isKeyDown(McUtil.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean shouldShowDebugWidgetBound() {
        if(!ConfigOptions.DEBUG_WIDGET_BOUNDS.getValue()) return false;
        return InputConstants.isKeyDown(McUtil.getWindow(), GLFW.GLFW_KEY_RIGHT_ALT);
    }

    public static boolean shouldShowDebugContainerData() {
        if(!ConfigOptions.DEBUG_CONTAINER_DATA.getValue()) return false;
        return InputConstants.isKeyDown(McUtil.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean shouldShowDebugVariables() {
        if(!ConfigOptions.DEBUG_VARIABLES.getValue()) return false;
        return InputConstants.isKeyDown(McUtil.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static InputConstants.Key getKey(int key) {
        return getKey(key, 0);
    }

    public static InputConstants.Key getKey(int key, int scancode) {
        //? if minecraft: >= 1.21.9 {
        return InputConstants.getKey(new KeyEvent(key, scancode, 0));
        //?} else {
        /*return InputConstants.getKey(key, scancode);
         *///?}
    }
}
