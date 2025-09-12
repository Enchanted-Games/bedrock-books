package games.enchanted.eg_bedrock_books.common.screen;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.ComponentTextAreaView;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.TextAreaView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class BedrockBookViewScreen extends AbstractBedrockBookScreen<Component, TextAreaView<Component>> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/view_background.png");;

    protected static final int FOOTER_BUTTON_WIDTH = 200;
    protected static final int TEXT_OFFSET_LEFT = 4;
    protected static final int TEXT_OFFSET_TOP = 10;

    protected int splitPagesAt;
    protected int leftPageX = 0;
    protected int leftPageY = 0;
    protected List<FormattedCharSequence> leftPageSplitLines = List.of();
    protected int rightPageX = 0;
    protected int rightPageY = 0;
    protected List<FormattedCharSequence> rightPageSplitLines = List.of();

    public BedrockBookViewScreen(BookViewScreen.BookAccess bookAccess) {
        super(BOOK_VIEW_TITLE, false);

        this.pages = bookAccess.pages();
        this.splitPagesAt = this.currentLeftPageIndex;
    }

    @Override
    protected void init() {
        super.init();

        // footer buttons
        this.footerButtonLayout = LinearLayout.horizontal().spacing(FOOTER_BUTTON_SPACING);
        this.footerButtonLayout.addChild(Button.builder(CommonComponents.GUI_DONE, button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(null);
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.setPosition((this.width / 2) - FOOTER_BUTTON_WIDTH / 2, (this.height / 2) + 90);
        this.footerButtonLayout.arrangeElements();
        this.footerButtonLayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected TextViewAndWidget<Component, TextAreaView<Component>> createTextWidgetAndView(int x, int y, PageSide side) {
        if(side == PageSide.LEFT) {
            this.leftPageX = x + TEXT_OFFSET_LEFT;
            this.leftPageY = y + TEXT_OFFSET_TOP;
        } else {
            this.rightPageX = x + TEXT_OFFSET_LEFT;
            this.rightPageY = y + TEXT_OFFSET_TOP;
        }

        return new TextViewAndWidget<>(new ComponentTextAreaView(component -> {
            int offsetIndex = this.currentLeftPageIndex + (side == PageSide.LEFT ? 0 : 1);
            if(side == PageSide.LEFT) {
                this.leftPageSplitLines = this.splitPage(offsetIndex);
            } else {
                this.rightPageSplitLines = this.splitPage(offsetIndex);
            }
        }), null);
    }

    protected List<FormattedCharSequence> splitPage(int index) {
        return this.font.split(this.getPageOrEmpty(index), PAGE_TEXT_WIDTH);
    }

    @Override
    Component getEmptyPageContent() {
        return CommonComponents.EMPTY;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int leftLines = Math.min(PAGE_TEXT_HEIGHT / this.font.lineHeight, this.leftPageSplitLines.size());
        for (int i = 0; i < leftLines; ++i) {
            FormattedCharSequence formattedCharSequence = this.leftPageSplitLines.get(i);
            guiGraphics.drawString(this.font, formattedCharSequence, this.leftPageX, this.leftPageY + i * this.font.lineHeight, TEXT_COLOUR, TEXT_SHADOW);
        }
        int rightLines = Math.min(PAGE_TEXT_HEIGHT / this.font.lineHeight, this.rightPageSplitLines.size());
        for (int i = 0; i < rightLines; ++i) {
            FormattedCharSequence formattedCharSequence = this.rightPageSplitLines.get(i);
            guiGraphics.drawString(this.font, formattedCharSequence, this.rightPageX, this.rightPageY + i * this.font.lineHeight, TEXT_COLOUR, TEXT_SHADOW);
        }
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }
}
