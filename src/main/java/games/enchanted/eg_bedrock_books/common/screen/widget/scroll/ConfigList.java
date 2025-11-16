package games.enchanted.eg_bedrock_books.common.screen.widget.scroll;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.mixin.accessor.AbstractScrollAreaAccessor;
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

import java.util.List;

public class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry> {
    private static final ResourceLocation SCROLLER_HANDLE_SPRITE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/scroller_handle");
    private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/scroller_background");
    private static final ResourceLocation SCROLLER_BACKGROUND_FILLED_SPRITE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/scroller_background_filled");

    public ConfigList(Minecraft minecraft, int width, int height, int x, int y) {
        super(minecraft, width + 10, height, y, 20);
        this.setPosition(x, y);
    }

    public void addHorizontal(AbstractWidget widget, MultiLineTextWidget label) {
        this.addEntry(new HorizontalEntry(widget, label));
    }

    public void addStacked(AbstractWidget widget, MultiLineTextWidget label) {
        this.addEntry(new StackedEntry(widget, label));
    }

    @Override
    public int getRowWidth() {
        return this.width - 16;
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft() - 10;
    }

    @Override
    protected int scrollBarX() {
        return super.scrollBarX() - 3;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderScrollbar(GuiGraphics graphics, int mouseX, int mouseY) {
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
            //? }
        }
    }

    protected abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
    }

    protected static class HorizontalEntry extends Entry {
        static final int GAP = 4;
        protected final AbstractWidget child;
        protected final MultiLineTextWidget label;

        protected HorizontalEntry(AbstractWidget child, MultiLineTextWidget label) {
            this.child = child;
            this.label = label;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(label, child);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(label, child);
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            this.label.setY(this.getContentY());
            this.label.setX(this.getContentX());
            this.label.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.child.setY(getContentYMiddle() - (GAP * 2));
            this.child.setX(this.getContentRight() - this.child.getWidth() + 4);
            this.child.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        @Override
        public int getHeight() {
            return Math.max(this.child.getHeight(), this.label.getHeight()) + GAP;
        }
    }

    protected static class StackedEntry extends HorizontalEntry {
        static final int BETWEEN_WIDGET_GAP = 2;

        protected StackedEntry(AbstractWidget child, MultiLineTextWidget label) {
            super(child, label);
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            this.label.setY(this.getContentY());
            this.label.setX(this.getContentX());
            this.label.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.child.setY(this.label.getBottom() + BETWEEN_WIDGET_GAP);
            this.child.setX(this.getContentX());
            this.child.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        @Override
        public int getHeight() {
            return this.child.getHeight() + BETWEEN_WIDGET_GAP + this.label.getHeight() + GAP;
        }
    }
}
