package games.enchanted.eg_bedrock_books.common.screen.widget.scroll;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

//? if minecraft: >= 1.21.9 {
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import games.enchanted.eg_bedrock_books.common.mixin.accessor.AbstractScrollAreaAccessor;
//?}

import java.util.List;
import java.util.Objects;

public class ConfigList extends VerticalScrollContainerWidget<ConfigList.Entry> {
    public static final int SCROLLBAR_WIDTH = 12;
    private static final ResourceLocation SCROLLER_HANDLE_SPRITE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/scroller_handle");
    private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/scroller_background");
    private static final ResourceLocation SCROLLER_BACKGROUND_FILLED_SPRITE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/scroller_background_filled");

    public ConfigList(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setPosition(x, y);
    }

    public void addHorizontal(AbstractWidget widget, MultiLineTextWidget label) {
        this.addChild(new HorizontalEntry(widget, label));
    }

    public void addStacked(AbstractWidget widget, MultiLineTextWidget label) {
        this.addChild(new StackedEntry(widget, label));
    }

    @Override
    protected int scrollbarWidth() {
        return SCROLLBAR_WIDTH;
    }

    @Override
    protected double scrollRate() {
        return 10;
    }

    @Override
    protected void renderScrollbar(
        GuiGraphics graphics
        //? if minecraft: >= 1.21.9 {
        , int mouseX,
        int mouseY
        //?}
    ) {
        final int HANDLE_WIDTH = 14;
        final int HANDLE_HEIGHT = 6;
        final int BACKGROUND_WIDTH = 6;
        if (this.scrollbarVisible()) {
            int top = this.getY();
            int bottom = this.getBottom();
            int barX = this.scrollBarX();
            double scrollAmount = this.scrollAmount() / this.maxScrollAmount();
            int handleY = (int) ((this.getBottom() - top) * scrollAmount);
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SCROLLER_BACKGROUND_SPRITE,
                barX,
                top,
                BACKGROUND_WIDTH,
                bottom - top
            );
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SCROLLER_BACKGROUND_FILLED_SPRITE,
                barX,
                top,
                BACKGROUND_WIDTH,
                handleY
            );
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SCROLLER_HANDLE_SPRITE,
                barX - 4,
                handleY + top - (HANDLE_HEIGHT / 2),
                HANDLE_WIDTH,
                HANDLE_HEIGHT
            );
            //? if minecraft: >= 1.21.9 {
            if (this.isOverScrollbar(mouseX, mouseY)) {
                graphics.requestCursor(((AbstractScrollAreaAccessor) this).eg_bedrock_books$isScrolling() ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
            }
            //?}
        }
    }


    protected abstract static class Entry extends Child {
        protected static final Margin MARGINS = new Margin(0, 3);

        @Override
        public Margin getMargins() {
            return MARGINS;
        }
    }

    protected static class HorizontalEntry extends Entry {
        protected final AbstractWidget child;
        protected final MultiLineTextWidget label;

        protected HorizontalEntry(AbstractWidget child, MultiLineTextWidget label) {
            this.child = child;
            this.label = label;
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(label, child);
        }
        @Override

        public @NotNull List<? extends NarratableEntry> narratableChildren() {
            return List.of(label, child);
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            int left = getContentX();
            int middleY = getContentYMiddle();
            int right = getContentRight();

            this.label.setY(middleY - this.label.getHeight() / 2);
            this.label.setX(left);
            this.label.render(guiGraphics, mouseX, mouseY, partialTicks);

            this.child.setY(middleY - this.child.getHeight() / 2);
            this.child.setX(right - this.child.getWidth());
            this.child.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        @Override
        public int height() {
            return Math.max(this.child.getHeight(), this.label.getHeight());
        }
    }

    protected static class StackedEntry extends HorizontalEntry {
        protected StackedEntry(AbstractWidget child, MultiLineTextWidget label) {
            super(child, label);
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            int top = getContentY();
            int left = getContentX();

            this.label.setY(top);
            this.label.setX(left);
            this.label.render(guiGraphics, mouseX, mouseY, partialTicks);

            this.child.setY(this.label.getBottom());
            this.child.setX(left);
            this.child.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        @Override
        public int height() {
            return this.child.getHeight() + this.label.getHeight() + getMargins().totalBlock();
        }
    }
}
