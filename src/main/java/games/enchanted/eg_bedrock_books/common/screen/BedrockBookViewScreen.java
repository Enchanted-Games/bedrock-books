package games.enchanted.eg_bedrock_books.common.screen;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.ComponentTextAreaView;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.TextAreaView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

public class BedrockBookViewScreen extends AbstractBedrockBookScreen<Component, TextAreaView<Component>> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/view_background.png");;

    protected static final int FOOTER_BUTTON_WIDTH = 200;
    protected static final int TEXT_OFFSET_LEFT = 4;
    protected static final int TEXT_OFFSET_TOP = 10;

    protected static final int PAGE_CLICK_BOUNDS_EXTRA_PADDING = 4;

    protected int leftPageX = 0;
    protected int leftPageY = 0;
    protected List<FormattedCharSequence> leftPageSplitLines = List.of();
    protected int rightPageX = 0;
    protected int rightPageY = 0;
    protected List<FormattedCharSequence> rightPageSplitLines = List.of();

    public BedrockBookViewScreen(BookViewScreen.BookAccess bookAccess) {
        super(BOOK_VIEW_TITLE, false);

        this.pages = bookAccess.pages();
    }

    @Override
    protected void init() {
        super.init();

        // footer buttons
        this.footerButtonLayout = LinearLayout.horizontal().spacing(FOOTER_BUTTON_SPACING);
        this.footerButtonLayout.addChild(Button.builder(CommonComponents.GUI_DONE, button -> {
            saveAndClose();
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
            int offsetIndex = this.getCurrentLeftPageIndex() + (side == PageSide.LEFT ? 0 : 1);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style clickedStyle = getStyleAt(mouseX, mouseY, null);

        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && clickedStyle != null && handleComponentClicked(clickedStyle)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Nullable
    public Style getStyleAt(double x, double y, @Nullable GuiGraphics guiGraphics) {
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

        if(guiGraphics != null && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            drawXY(closestToLeftHorizontally ? 0 : 1, closestToLeftHorizontally ? 0 : 1, 0, guiGraphics);
            drawXY(closestPageX, closestPageY, 8, guiGraphics);
            drawXY(clampedRelativeX, clampedRelativeY, 16, guiGraphics);
        }

        Minecraft minecraft = Minecraft.getInstance();
        int lineIndex = clampedRelativeY / minecraft.font.lineHeight;
        if (lineIndex >= 0 && lineIndex < (closestToLeftHorizontally ? this.leftPageSplitLines.size() : this.rightPageSplitLines.size())) {
            FormattedCharSequence line = (closestToLeftHorizontally ? this.leftPageSplitLines : this.rightPageSplitLines).get(lineIndex);
            return minecraft.font.getSplitter().componentStyleAtWidth(line, clampedRelativeX);
        }

        return null;
    }

    @Override
    protected void handleClickEvent(Minecraft minecraft, ClickEvent clickEvent) {
        switch (clickEvent) {
            case ClickEvent.RunCommand(String string):
                closeServerContainer();
                if(minecraft.player != null) {
                    clickCommandAction(minecraft.player, string, null);
                }
                break;
            case ClickEvent.ChangePage(int i):
                setPageIndex(i - 1);
                break;
            default:
                defaultHandleGameClickEvent(clickEvent, minecraft, this);
        }
    }

    protected void closeServerContainer() {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int leftLines = Math.min(PAGE_TEXT_HEIGHT / this.font.lineHeight, this.leftPageSplitLines.size());
        for (int i = 0; i < leftLines; ++i) {
            guiGraphics.drawString(
                this.font,
                this.leftPageSplitLines.get(i),
                this.leftPageX,
                this.leftPageY + i * this.font.lineHeight,
                TEXT_COLOUR,
                TEXT_SHADOW
            );
        }

        int rightLines = Math.min(PAGE_TEXT_HEIGHT / this.font.lineHeight, this.rightPageSplitLines.size());
        for (int i = 0; i < rightLines; ++i) {
            guiGraphics.drawString(
                this.font,
                this.rightPageSplitLines.get(i),
                this.rightPageX,
                this.rightPageY + i * this.font.lineHeight,
                TEXT_COLOUR,
                TEXT_SHADOW
            );
        }

        if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            guiGraphics.fillGradient(this.leftPageX, this.leftPageY, this.leftPageX + PAGE_TEXT_WIDTH, this.leftPageY + PAGE_TEXT_HEIGHT, 0x22000000, 0x22000000);
            guiGraphics.fillGradient(this.rightPageX, this.rightPageY, this.rightPageX + PAGE_TEXT_WIDTH, this.rightPageY + PAGE_TEXT_HEIGHT, 0x22000000, 0x22000000);
        }

        guiGraphics.renderComponentHoverEffect(this.font, getStyleAt(mouseX, mouseY, guiGraphics), mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

    protected void setBookAccess(BookViewScreen.BookAccess bookAccess) {
        this.pages = bookAccess.pages();
        ensureEvenPageIndex(Math.clamp(this.getCurrentLeftPageIndex(), 0, bookAccess.getPageCount() - 1));
        updateVisibleContents();
    }

    protected void setPageIndex(int index) {
        ensureEvenPageIndex(Math.clamp(index, 0, getCurrentAmountOfPages() - 1));
        updateVisibleContents();
    }

    private void drawXY(int x, int y, int yoffset, GuiGraphics guiGraphics) {
        String string = String.format(Locale.ROOT, "%s, %s", x, y);
        guiGraphics.drawString(font, string, 0, 0 + yoffset, -1);
    }
}
