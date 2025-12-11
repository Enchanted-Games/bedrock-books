package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import games.enchanted.eg_bedrock_books.common.duck.AbstractSliderButtonAdditions;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AbstractSliderButton.class, priority = 995)
public class AbstractSliderButtonMixin {
    @WrapMethod(method = "getSprite")
    private Identifier eg_bedrock_books$callCustomGetSpriteIfApplicable(Operation<Identifier> original) {
        if(this instanceof AbstractSliderButtonAdditions additions) {
            return additions.eg_bedrock_books$getSprite();
        }
        return original.call();
    }

    @WrapMethod(method = "getHandleSprite")
    private Identifier eg_bedrock_books$callCustomGetHandleSpriteIfApplicable(Operation<Identifier> original) {
        if(this instanceof AbstractSliderButtonAdditions additions) {
            return additions.eg_bedrock_books$getHandleSprite();
        }
        return original.call();
    }
}
