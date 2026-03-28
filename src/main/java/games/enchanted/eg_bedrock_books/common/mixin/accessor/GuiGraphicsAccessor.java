package games.enchanted.eg_bedrock_books.common.mixin.accessor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphicsExtractor.class)
public interface GuiGraphicsAccessor {
    @Invoker("componentHoverEffect")
    void eg_bedrock_books$componentHoverEffect(final Font font, final Style hoveredStyle, final int xMouse, final int yMouse);
}
