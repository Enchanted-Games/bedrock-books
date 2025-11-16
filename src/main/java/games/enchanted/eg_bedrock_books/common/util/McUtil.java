package games.enchanted.eg_bedrock_books.common.util;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

public class McUtil {
    public static
    //? if minecraft: >= 1.21.9 {
    Window
    //?} else {
    /*long
    *///?}
    getWindow() {
        //? if minecraft: >= 1.21.9 {
        return Minecraft.getInstance().getWindow();
        //?} else {
        /*return Minecraft.getInstance().getWindow().getWindow();
        *///?}
    }
}
