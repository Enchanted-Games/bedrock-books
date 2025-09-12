package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.screen.BedrockBookViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"),
        method = "handleOpenBook"
    )
    private void eg_bedrock_books$modifyBookViewScreen(Minecraft instance, Screen old, Operation<Void> original, @Local BookViewScreen.BookAccess bookAccess) {
        if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT)) {
            original.call(instance, old);
            return;
        }
        original.call(instance, new BedrockBookViewScreen(bookAccess));
    }
}
