package games.enchanted.eg_bedrock_books.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class InputUtil {
    public static boolean shouldOpenVanillaBook() {
        if(!ConfigOptions.VANILLA_BOOK_KEY_ENABLED.getValue()) return false;
        InputConstants.Key key = ConfigOptions.VANILLA_BOOK_KEY.getValue();
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getValue());
    }

    public static boolean shouldShowDebugTextBound() {
        if(!ConfigOptions.DEBUG_TEXT_BOUNDS.getValue()) return false;
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean shouldShowDebugWidgetBound() {
        if(!ConfigOptions.DEBUG_TEXT_BOUNDS.getValue()) return false;
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT);
    }

    public static boolean shouldShowDebugContainerData() {
        if(!ConfigOptions.DEBUG_CONTAINER_DATA.getValue()) return false;
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean shouldShowDebugVariables() {
        if(!ConfigOptions.DEBUG_CONTAINER_DATA.getValue()) return false;
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
}
