package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import games.enchanted.eg_bedrock_books.common.screen.BedrockBookViewScreen;
import games.enchanted.eg_bedrock_books.common.util.ScreenUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if minecraft: <= 26.1 {
/*import net.minecraft.client.Minecraft;
*///? } else {
import net.minecraft.client.gui.Gui;
//? }

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @WrapOperation(
        at = @At(
            value = "INVOKE",
            //? if minecraft: <= 26.1 {
            /*target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"
            *///? } else {
            target = "Lnet/minecraft/client/gui/Gui;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"
            //? }
        ),
        method = "handleOpenBook"
    )
    private void eg_bedrock_books$modifyBookViewScreen(
        //? if minecraft: <= 26.1 {
        /*Minecraft instance,
         *///? } else {
        Gui instance,
        //? }
        Screen old,
        Operation<Void> original,
        @Local BookViewScreen.BookAccess bookAccess
    ) {
        if(ScreenUtil.shouldOpenVanillaWrittenScreen()) {
            original.call(instance, old);
            return;
        }
        original.call(instance, new BedrockBookViewScreen(bookAccess));
    }
}
