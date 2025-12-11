//? if minecraft: >= 1.21.11 {
package games.enchanted.eg_bedrock_books.common.screen.text;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;


import java.util.function.Consumer;

public class ClickableAndHoverTextCollector extends ActiveTextCollector.ClickableStyleFinder {
    private final Font font;
    private final int testX;
    private final int testY;

    private final Consumer<Style> styleTester = style -> {
        if (style.getClickEvent() != null || style.getHoverEvent() != null || style.getInsertion() != null) {
            this.result = style;
        }
    };

    @Nullable
    private Style result;

    public ClickableAndHoverTextCollector(Font font, int testX, int testY) {
        super(font, testX, testY);
        this.font = font;
        this.testX = testX;
        this.testY = testY;
    }

    @Override
    public void accept(
        final TextAlignment alignment, final int anchorX, final int y, final ActiveTextCollector.Parameters parameters, final FormattedCharSequence text
    ) {
        int leftX = alignment.calculateLeft(anchorX, this.font, text);
        GuiTextRenderState renderState = new GuiTextRenderState(
            this.font, text, parameters.pose(), leftX, y, ARGB.white(parameters.opacity()), 0, true, true, parameters.scissor()
        );
        ActiveTextCollector.findElementUnderCursor(renderState, this.testX, this.testY, this.styleTester);
    }

    @Override
    public @Nullable Style result() {
        return this.result;
    }
}
//? }
