package games.enchanted.eg_bedrock_books.common.screen;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BedrockBookEditScreen extends Screen {
    private static final int BACKGROUND_WIDTH = 512;
    private static final int BACKGROUND_HEIGHT = 256;
    private static final int PAGE_EDIT_BOX_WIDTH = 122;
    private static final int PAGE_EDIT_BOX_HEIGHT = 134;
    private static final int CENTER_PADDING = 22;
    private static final String BOOK_EDIT_TITLE = "book.edit.title";
    private static final String BOOK_VIEW_TITLE = "book.view.title";
    private static final String BOOK_PAGE_INDICATOR = "book.pageIndicator";

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/background.png");
    private static final int TURN_PAGE_BUTTON_SIZE = 24;
    private static final Component PAGE_LEFT_BUTTON_LABEL = Component.translatable("book.page_button.previous");
    private static final CustomSpriteButton.ButtonConfig PAGE_LEFT_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_left"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_left_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_left_focus")
    );
    private static final Component PAGE_RIGHT_BUTTON_LABEL = Component.translatable("book.page_button.next");
    private static final CustomSpriteButton.ButtonConfig PAGE_RIGHT_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_right"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_right_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_right_focus")
    );

    protected static final int MAX_PAGES = WritableBookContent.MAX_PAGES;

    // pagination
    protected int currentLeftPageIndex;
    protected List<String> pages = new ArrayList<>();

    private CustomSpriteButton turnLeftButton;
    private CustomSpriteButton turnRightButton;

    // two visible pages
    protected Component leftPageNumberMessage;
    private MultiLineEditBox leftPageEditBox;
    protected Component rightPageNumberMessage;
    private MultiLineEditBox rightPageEditBox;

    protected final boolean canCreateNewPages = true;

    public BedrockBookEditScreen(Player owner, ItemStack book, InteractionHand hand, WritableBookContent writableBookContent) {
        super(Component.translatable(BOOK_EDIT_TITLE));

        this.currentLeftPageIndex = 0;
        writableBookContent.getPages(Minecraft.getInstance().isTextFilteringEnabled()).forEach(this.pages::add);
        if(this.pages.isEmpty()) {
            addPage("");
            addPage("");
        } else if (this.pages.size() == 1) {
            addPage("");
        }
    }

    @Override
    protected void init() {
        final int editBoxYPos = (this.height / 2) - PAGE_EDIT_BOX_HEIGHT + 52;

        // left page

        this.leftPageEditBox = MultiLineEditBox.builder()
            .setTextColor(-16777216)
            .setCursorColor(-16777216)
            .setShowDecorations(false)
            .setShowBackground(false)
            .setTextShadow(false)
            .setX((this.width / 2) - (CENTER_PADDING / 2) - PAGE_EDIT_BOX_WIDTH)
            .setY(editBoxYPos)
        .build(this.font, PAGE_EDIT_BOX_WIDTH, PAGE_EDIT_BOX_HEIGHT, CommonComponents.EMPTY);
        this.leftPageEditBox.setCharacterLimit(1024);
        this.leftPageEditBox.setLineLimit(126 / this.font.lineHeight);
        this.leftPageEditBox.setValueListener(newValue -> setPageContent(newValue, this.currentLeftPageIndex));
        this.addRenderableWidget(this.leftPageEditBox);

        this.turnLeftButton = new CustomSpriteButton(
            (this.width / 2) - 146,
            (this.height / 2) + 54,
            TURN_PAGE_BUTTON_SIZE,
            TURN_PAGE_BUTTON_SIZE,
            (button) -> this.turnBackPage(),
            PAGE_LEFT_BUTTON_LABEL,
            PAGE_LEFT_BUTTON_CONFIG
        );
        this.addRenderableWidget(turnLeftButton);

        // right page

        this.rightPageEditBox = MultiLineEditBox.builder()
            .setTextColor(-16777216)
            .setCursorColor(-16777216)
            .setShowDecorations(false)
            .setShowBackground(false)
            .setTextShadow(false)
            .setX((this.width / 2) + (CENTER_PADDING / 2))
            .setY(editBoxYPos)
            .build(this.font, PAGE_EDIT_BOX_WIDTH, PAGE_EDIT_BOX_HEIGHT, CommonComponents.EMPTY);
        this.rightPageEditBox.setCharacterLimit(1024);
        this.rightPageEditBox.setLineLimit(126 / this.font.lineHeight);
        this.rightPageEditBox.setValueListener(newValue -> setPageContent(newValue, this.currentLeftPageIndex + 1));
        this.addRenderableWidget(this.rightPageEditBox);

        this.turnRightButton = new CustomSpriteButton(
            (this.width / 2) + 123,
            (this.height / 2) + 54,
            TURN_PAGE_BUTTON_SIZE,
            TURN_PAGE_BUTTON_SIZE,
            (button) -> this.turnForwardPage(),
            PAGE_RIGHT_BUTTON_LABEL,
            PAGE_RIGHT_BUTTON_CONFIG
        );
        this.addRenderableWidget(turnRightButton);

        // general setup
        updateVisibleContents();
    }

    protected void updateVisibleContents() {
        this.turnLeftButton.visible = true;
        this.turnRightButton.visible = true;

        if(this.currentLeftPageIndex <= 1) {
            this.turnLeftButton.visible = false;
        }
        if((this.currentLeftPageIndex + 1 >= this.getCurrentAmountOfPages() && !this.canCreateNewPages) || this.currentLeftPageIndex + 2 >= MAX_PAGES) {
            this.turnRightButton.visible = false;
        }

        this.leftPageNumberMessage = getPageIndicatorMessage(false);
        this.leftPageEditBox.setValue(getOrCreatePageIfPossible(this.currentLeftPageIndex), true);

        this.rightPageNumberMessage = getPageIndicatorMessage(true);
        this.rightPageEditBox.setValue(getOrCreatePageIfPossible(this.currentLeftPageIndex + 1), true);
    }

    protected void turnForwardPage() {
        if(this.currentLeftPageIndex + 2 >= MAX_PAGES) return;
        if(this.currentLeftPageIndex + 2 > this.getCurrentAmountOfPages() && this.canCreateNewPages) {
            addPage("");
            addPage("");
        }
        this.currentLeftPageIndex += 2;
        updateVisibleContents();
    }

    protected void turnBackPage() {
        if(this.currentLeftPageIndex - 2 < 0) {
            this.currentLeftPageIndex = 0;
        } else {
            this.currentLeftPageIndex -= 2;
        }
        updateVisibleContents();
    }

    protected void addPage(String contents) {
        if(!this.canCreateNewPages) return;
        addPage(contents, this.pages.size());
    }

    protected void addPage(String contents, int index) {
        if(!this.canCreateNewPages) return;
        if(this.pages.size() >= MAX_PAGES) return;
        this.pages.add(index, contents);
    }

    protected String getOrCreatePageIfPossible(int index) {
        if(index > this.pages.size() - 1) {
            if(!this.canCreateNewPages) return "";
            addPage("");
            updateVisibleContents();
        }
        return this.pages.get(index);
    }

    protected void setPageContent(String contents, int index) {
        if(index > this.pages.size() - 1) {
            this.addPage(contents);
            return;
        }
        this.pages.set(index, contents);
    }

    protected int getCurrentAmountOfPages() {
        return this.pages.size();
    }

    protected Component getPageIndicatorMessage(boolean forRightPage) {
        int offsetIndex = this.currentLeftPageIndex + (forRightPage ? 2 : 1);
        if(offsetIndex > this.getCurrentAmountOfPages()) return CommonComponents.EMPTY;
        return Component.translatable(BOOK_PAGE_INDICATOR, offsetIndex, this.getCurrentAmountOfPages());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert this.minecraft != null;
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN && this.turnLeftButton.visible) {
            this.turnLeftButton.onPress();
            this.turnLeftButton.playDownSound(this.minecraft.getSoundManager());
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_UP && this.turnRightButton.visible) {
            this.turnRightButton.onPress();
            this.turnLeftButton.playDownSound(this.minecraft.getSoundManager());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public @NotNull Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.getPageIndicatorMessage(false), this.getPageIndicatorMessage(true));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        final int pageNumberYPos = (this.height / 2) - PAGE_EDIT_BOX_HEIGHT + 42;

        final int leftPageNumberWidth = this.font.width(this.leftPageNumberMessage);
        guiGraphics.drawString(
            this.font,
            this.leftPageNumberMessage,
            (this.width / 2) - (CENTER_PADDING / 2) - (PAGE_EDIT_BOX_WIDTH / 2) - (leftPageNumberWidth / 2),
            pageNumberYPos,
            0xffbca387,
            false
        );

        final int rightPageNumberWidth = this.font.width(this.rightPageNumberMessage);
        guiGraphics.drawString(
            this.font,
            this.rightPageNumberMessage,
            (this.width / 2) + (CENTER_PADDING / 2) + (PAGE_EDIT_BOX_WIDTH / 2) - (rightPageNumberWidth / 2),
            pageNumberYPos,
            0xffbca387,
            false
        );
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND_TEXTURE,
            (this.width / 2) - (BACKGROUND_WIDTH / 2),
            (this.height / 2) - (BACKGROUND_HEIGHT / 2),
            0f,
            0f,
            BACKGROUND_WIDTH,
            BACKGROUND_HEIGHT,
            BACKGROUND_WIDTH,
            BACKGROUND_HEIGHT
        );
    }
}
