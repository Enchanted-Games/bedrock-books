package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import games.enchanted.eg_bedrock_books.common.duck.BookSignScreenAdditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookSignScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BookSignScreen.class)
public class BookSignScreenMixin implements BookSignScreenAdditions {
    @Unique
    private Screen eg_bedrock_books$returnScreen = null;

    @Override
    public void eg_bedrock_books$setReturnScreen(Screen screen) {
        this.eg_bedrock_books$returnScreen = screen;
    }

    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"),
        method = "method_71541"
    )
    private void eg_bedrock_books$modifyReturnScreenIfPresent(Minecraft instance, Screen old, Operation<Void> original) {
        if(this.eg_bedrock_books$returnScreen != null) {
            original.call(instance, this.eg_bedrock_books$returnScreen);
            return;
        }
        original.call(instance, old);
    }
}
