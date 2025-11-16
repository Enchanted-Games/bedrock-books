package games.enchanted.eg_bedrock_books.common.mixin.accessor;

import net.minecraft.client.gui.components.AbstractScrollArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractScrollArea.class)
public interface AbstractScrollAreaAccessor {
    @Accessor("scrolling")
    boolean eg_bedrock_books$isScrolling();
}
