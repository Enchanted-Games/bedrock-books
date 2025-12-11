package games.enchanted.eg_bedrock_books.common.screen;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.ComponentTextAreaView;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.TextAreaView;
import games.enchanted.eg_bedrock_books.common.util.ColourUtil;
import games.enchanted.eg_bedrock_books.common.util.InputUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//? if minecraft: >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}

public class BedrockBookViewScreen extends AbstractBedrockBookScreen<Component, TextAreaView<Component>> {
    protected static final Component BOOK_VIEW_TITLE = Component.translatable("book.view.title");
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/view_background.png");;

    protected static final int FOOTER_BUTTON_WIDTH = 200;
    protected static final int TEXT_OFFSET_LEFT = 4;
    protected static final int TEXT_OFFSET_RIGHT = 4;
    protected static final int TEXT_OFFSET_TOP = 10;

    protected static final int PAGE_CLICK_BOUNDS_EXTRA_PADDING = 2;

    protected int leftPageX = 0;
    protected int leftPageY = 0;
    protected List<FormattedCharSequence> leftPageSplitLines = List.of();
    protected int rightPageX = 0;
    protected int rightPageY = 0;
    protected List<FormattedCharSequence> rightPageSplitLines = List.of();

    protected double mouseX = 0;
    protected double mouseY = 0;

    @Nullable protected Style styleUnderMouseCursor = null;

    public BedrockBookViewScreen(BookViewScreen.BookAccess bookAccess) {
        super(BOOK_VIEW_TITLE, false);

        this.pages = bookAccess.pages();
    }

    public BedrockBookViewScreen() {
        this(BookViewScreen.EMPTY_ACCESS);
    }

    @Override
    protected void makeFooterButtons() {
        this.footerButtonLayout.addChild(Button.builder(CommonComponents.GUI_DONE, button -> {
            this.onClose();
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.setPosition((this.width / 2) - FOOTER_BUTTON_WIDTH / 2, (this.height / 2) + 90);
    }

    @Override
    protected TextViewAndWidget<Component, TextAreaView<Component>> createTextWidgetAndView(int x, int y, PageSide side) {
        if(side == PageSide.LEFT) {
            this.leftPageX = x + 8 - getHorizontalLeftPageTextOffset();
            this.leftPageY = y + getVerticalTextOffset();
        } else {
            this.rightPageX = x + getHorizontalRightPageTextOffset();
            this.rightPageY = y + getVerticalTextOffset();
        }

        return new TextViewAndWidget<>(new ComponentTextAreaView(component -> {
            int offsetIndex = this.getCurrentLeftPageIndex() + (side == PageSide.LEFT ? 0 : 1);
            if(side == PageSide.LEFT) {
                this.leftPageSplitLines = this.splitPage(offsetIndex);
            } else {
                this.rightPageSplitLines = this.splitPage(offsetIndex);
            }
        }), null);
    }

    protected int getHorizontalLeftPageTextOffset() {
        return TEXT_OFFSET_LEFT;
    }
    protected int getHorizontalRightPageTextOffset() {
        return TEXT_OFFSET_RIGHT;
    }
    protected int getVerticalTextOffset() {
        return TEXT_OFFSET_TOP;
    }

    protected List<FormattedCharSequence> splitPage(int index) {
        Component page = this.getPageOrEmpty(index);

        if(ConfigOptions.IMPROVE_TEXT_CONTRAST_IN_HC.getValue() && ModConstants.isHighContrastPackActive()) {
            List<Component> modifiedPageElements = new ArrayList<>();
            page.toFlatList().forEach(component -> {
                TextColor textColour = component.getStyle().getColor();
                int colour = 0xffffff;
                if(textColour != null) {
                    colour = ColourUtil.makeRGBHighContrastAgainstBlack(textColour.getValue());
                }
                modifiedPageElements.add(component.copy().withColor(colour));
            });
            page = ComponentUtils.formatList(modifiedPageElements, Component.empty());
        }

        return this.font.split(page, PAGE_TEXT_WIDTH);
    }

    @Override
    protected Component getEmptyPageContent() {
        return CommonComponents.EMPTY;
    }

    @Override
    protected void turnForwardPage() {
        super.turnForwardPage();
        refreshStyleUnderMouse();
    }

    @Override
    protected void turnBackPage() {
        super.turnBackPage();
        refreshStyleUnderMouse();
    }

    public void refreshPageContent() {
        this.leftPageSplitLines = this.splitPage(this.getCurrentLeftPageIndex());
        this.rightPageSplitLines = this.splitPage(this.getCurrentLeftPageIndex() + 1);
    }

    @Override
    public boolean mouseClicked(
        //? if minecraft: >= 1.21.9 {
        MouseButtonEvent mouseButtonEvent, boolean doubleClick
        //?} else {
        /*double mouseX, double mouseY, int button
         *///?}
    ) {
        Style clickedStyle = getStyleAt(mouseX, mouseY);
        //? if minecraft: >= 1.21.9 {
        int button = mouseButtonEvent.button();
        //?}

        if (button == InputConstants.MOUSE_BUTTON_LEFT && clickedStyle != null && handleClickEvent(this.minecraft, clickedStyle.getClickEvent())) return true;

        //? if minecraft: >= 1.21.9 {
        return super.mouseClicked(mouseButtonEvent, doubleClick);
        //?} else {
        /*return super.mouseClicked(mouseX, mouseY, button);
        *///?}
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        refreshStyleUnderMouse();
    }

    protected void refreshStyleUnderMouse() {
        this.styleUnderMouseCursor = getStyleAt(this.mouseX, this.mouseY);
    }

    @Nullable
    public Style getStyleAt(double x, double y) {
        if (this.leftPageSplitLines.isEmpty() && this.rightPageSplitLines.isEmpty()) {
            return null;
        }

        boolean closestToLeftHorizontally = (x < this.leftPageX + PAGE_TEXT_WIDTH + PAGE_CLICK_BOUNDS_EXTRA_PADDING);

        int closestPageX = closestToLeftHorizontally ? this.leftPageX : this.rightPageX;
        int closestPageY = closestToLeftHorizontally ? this.leftPageY : this.rightPageY;

        // not within a page bounds
        if(x < closestPageX - PAGE_CLICK_BOUNDS_EXTRA_PADDING || x > closestPageX + PAGE_CLICK_BOUNDS_EXTRA_PADDING + PAGE_TEXT_WIDTH) return null;
        if(y < closestPageY - PAGE_CLICK_BOUNDS_EXTRA_PADDING || y > closestPageY + PAGE_CLICK_BOUNDS_EXTRA_PADDING + PAGE_TEXT_HEIGHT) return null;

        int clampedRelativeX = (int) Math.clamp(x - closestPageX, 0, closestPageX + PAGE_TEXT_WIDTH);
        int clampedRelativeY = (int) Math.clamp(y - closestPageY, 0, closestPageY + PAGE_TEXT_HEIGHT);

        Minecraft minecraft = Minecraft.getInstance();
        int lineIndex = clampedRelativeY / minecraft.font.lineHeight;
        if (lineIndex >= 0 && lineIndex < (closestToLeftHorizontally ? this.leftPageSplitLines.size() : this.rightPageSplitLines.size())) {
            FormattedCharSequence line = (closestToLeftHorizontally ? this.leftPageSplitLines : this.rightPageSplitLines).get(lineIndex);

            //? if minecraft: <= 1.21.10 {
            /*return minecraft.font.getSplitter().componentStyleAtWidth(line, clampedRelativeX);
            *///?} else {
            ActiveTextCollector.ClickableStyleFinder styleFinder = new ActiveTextCollector.ClickableStyleFinder(this.getFont(), (int) x, (int) y);
            this.findClickableStylesInPages(styleFinder);
            return styleFinder.result();
            //?}
        }

        return null;
    }

    //? if minecraft: >= 1.21.11 {
    protected void findClickableStylesInPages(ActiveTextCollector.ClickableStyleFinder styleFinder) {
        visitPageText((text, x, y) -> styleFinder.accept(x, y, text), PageSide.LEFT);
        visitPageText((text, x, y) -> styleFinder.accept(x, y, text), PageSide.RIGHT);
    }
    //? }

    protected void visitBothPagesText(TextConsumer consumer) {
        visitPageText(consumer, PageSide.LEFT);
        visitPageText(consumer, PageSide.RIGHT);
    }

    protected void visitPageText(TextConsumer consumer, PageSide side) {
        if(side == PageSide.LEFT) {
            int leftLines = Math.min(PAGE_TEXT_HEIGHT / this.font.lineHeight, this.leftPageSplitLines.size());
            for (int i = 0; i < leftLines; ++i) {
                consumer.accept(this.leftPageSplitLines.get(i), this.leftPageX, this.leftPageY + i * this.font.lineHeight);
            }
        } else {
            int rightLines = Math.min(PAGE_TEXT_HEIGHT / this.font.lineHeight, this.rightPageSplitLines.size());
            for (int i = 0; i < rightLines; ++i) {
                consumer.accept(this.rightPageSplitLines.get(i), this.rightPageX, this.rightPageY + i * this.font.lineHeight);
            }
        }
    }

    protected boolean handleClickEvent(Minecraft minecraft, @Nullable ClickEvent clickEvent) {
        if(clickEvent == null) return false;
        switch (clickEvent) {
            case ClickEvent.RunCommand(String string):
                if(ConfigOptions.CLOSE_BOOK_WHEN_RUNNING_COMMAND.getValue()) {
                    closeServerContainer();
                }
                if(minecraft.player != null) {
                    clickCommandAction(minecraft.player, string, ConfigOptions.CLOSE_BOOK_WHEN_RUNNING_COMMAND.getValue() ? null : this);
                }
                break;
            case ClickEvent.ChangePage(int i):
                setPageIndex(i - 1);
                break;
            default:
                defaultHandleGameClickEvent(clickEvent, minecraft, this);
        }
        return true;
    }

    protected void closeServerContainer() {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        visitBothPagesText((text, x, y) -> {
            guiGraphics.drawString(
                this.font,
                text,
                x,
                y,
                this.getTextColour(),
                TEXT_SHADOW
            );
        });

        guiGraphics.renderComponentHoverEffect(this.font, this.styleUnderMouseCursor, mouseX, mouseY);

        if(InputUtil.shouldShowDebugTextBound()) {
            guiGraphics.fillGradient(this.leftPageX - PAGE_CLICK_BOUNDS_EXTRA_PADDING, this.leftPageY - PAGE_CLICK_BOUNDS_EXTRA_PADDING, this.leftPageX + PAGE_TEXT_WIDTH + PAGE_CLICK_BOUNDS_EXTRA_PADDING, this.leftPageY + PAGE_TEXT_HEIGHT + PAGE_CLICK_BOUNDS_EXTRA_PADDING, 0x22ff0000, 0x22ff0000);
            guiGraphics.fillGradient(this.rightPageX - PAGE_CLICK_BOUNDS_EXTRA_PADDING, this.rightPageY - PAGE_CLICK_BOUNDS_EXTRA_PADDING, this.rightPageX + PAGE_TEXT_WIDTH + PAGE_CLICK_BOUNDS_EXTRA_PADDING, this.rightPageY + PAGE_TEXT_HEIGHT + PAGE_CLICK_BOUNDS_EXTRA_PADDING, 0x22ff0000, 0x22ff0000);
        }
        if(InputUtil.shouldShowDebugVariables()) {
            guiGraphics.drawString(this.font, "style under cursor: ", 0, this.height - this.font.lineHeight * 2, -1);
            guiGraphics.drawString(this.font, this.styleUnderMouseCursor == null ? "<none>" : this.styleUnderMouseCursor.toString(), 0, this.height - this.font.lineHeight, -1);
        }
    }

    @Override
    protected Identifier getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

    protected void setBookAccess(BookViewScreen.BookAccess bookAccess) {
        this.pages = bookAccess.pages();
        ensureEvenPageIndex(Math.clamp(this.getCurrentLeftPageIndex(), 0, Math.max(0, bookAccess.getPageCount() - 1)));
        updateVisibleContents();
    }

    protected void setPageIndex(int index) {
        ensureEvenPageIndex(Math.clamp(index, 0, Math.max(0, getCurrentAmountOfPages() - 1)));
        updateVisibleContents();
    }

    @FunctionalInterface
    protected interface TextConsumer {
        void accept(FormattedCharSequence text, int x, int y);
    }
}
