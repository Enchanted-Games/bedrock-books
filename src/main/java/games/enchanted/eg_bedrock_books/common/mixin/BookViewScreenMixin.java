package games.enchanted.eg_bedrock_books.common.mixin;

import games.enchanted.eg_bedrock_books.common.screen.BedrockBookViewScreen;
import games.enchanted.eg_bedrock_books.common.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookViewScreen.class)
public class BookViewScreenMixin {
    @Shadow
    private BookViewScreen.BookAccess bookAccess;

    @Unique
    private boolean eg_bedrock_books$preventScreenChange = false;

    @Inject(
        at = @At("HEAD"),
        method = "init"
    )
    private void eg_bedrock_books$replaceViewScreenConditionally(CallbackInfo ci) {
        if(this.eg_bedrock_books$preventScreenChange) return;
        if(ScreenUtil.shouldOpenVanillaWrittenScreen()) {
            this.eg_bedrock_books$preventScreenChange = true;
            return;
        }
        ScreenUtil.setScreen(Minecraft.getInstance(), new BedrockBookViewScreen(this.bookAccess));
    }
}
