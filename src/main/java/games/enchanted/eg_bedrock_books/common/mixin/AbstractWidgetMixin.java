package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import games.enchanted.eg_bedrock_books.common.duck.AbstractWidgetAdditions;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractWidget.class)
public class AbstractWidgetMixin {
    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;getRectangle()Lnet/minecraft/client/gui/navigation/ScreenRectangle;"),
        method = "render"
    )
    private ScreenRectangle eg_bedrock_books$overrideRectangle(AbstractWidget instance, Operation<ScreenRectangle> original) {
        if(this instanceof AbstractWidgetAdditions abstractWidgetAdditions) {
            return abstractWidgetAdditions.eg_bedrock_books$getTooltipRectangle();
        }
        return original.call(instance);
    }
}
