package games.enchanted.eg_bedrock_books.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import net.minecraft.client.Minecraft;

public class InputUtil {
    public static boolean shouldOpenVanillaBook() {
        InputConstants.Key key = ConfigOptions.VANILLA_BOOK_KEY.getValue();
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getValue());
    }
}
