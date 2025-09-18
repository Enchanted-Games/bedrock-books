package games.enchanted.eg_bedrock_books.common.screen;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.config.ConfigScreenBehaviour;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
import games.enchanted.eg_bedrock_books.common.screen.widget.EditControls;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.TextAreaView;
import games.enchanted.eg_bedrock_books.common.util.InputUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.component.WritableBookContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBedrockBookScreen<PageContent, TextView extends TextAreaView<PageContent>> extends Screen {
    // book spacing
    protected static final int BACKGROUND_WIDTH = 512;
    protected static final int BACKGROUND_HEIGHT = 256;
    protected static final int PAGE_EDIT_BOX_WIDTH = 122;
    protected static final int PAGE_EDIT_BOX_HEIGHT = 134;
    protected static final int PAGE_TEXT_WIDTH = 114;
    protected static final int PAGE_TEXT_HEIGHT = 128;
    protected static final int CENTER_PADDING = 22;

    // text style
    protected static final int CURSOR_COLOUR = 0xff000000;
    protected static final int TEXT_COLOUR = 0xff000000;
    protected static final int PAGE_INDICATOR_COLOUR = 0xffbca387;
    protected static final boolean TEXT_SHADOW = false;

    // footer button spacing
    protected static final int FOOTER_BUTTON_WIDTH = 90;
    protected static final int FOOTER_BUTTON_SPACING = 8;

    // translations
    protected static final String BOOK_PAGE_INDICATOR = "book.pageIndicator";
    protected static final Component SIGN_BUTTON_COMPONENT = Component.translatable("book.signButton");
    protected static final Component SAVE_BUTTON_COMPONENT = Component.translatable("selectWorld.edit.save");

    // textures
    private static final int TURN_PAGE_BUTTON_SIZE = 24;
    private static final Component PAGE_LEFT_BUTTON_LABEL = Component.translatable("book.page_button.previous");
    private static final CustomSpriteButton.ButtonConfig PAGE_LEFT_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_backward"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_backward_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_backward_focus")
    );
    private static final Component PAGE_RIGHT_BUTTON_LABEL = Component.translatable("book.page_button.next");
    private static final CustomSpriteButton.ButtonConfig PAGE_RIGHT_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_forward"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_forward_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_forward_focus")
    );
    private static final int CLOSE_BUTTON_SIZE = 9;
    private static final Component CLOSE_BUTTON_LABEL = CommonComponents.GUI_DONE;
    private static final CustomSpriteButton.ButtonConfig CLOSE_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/close"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/close_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/close_focus")
    );

    private static final int CONFIG_BUTTON_SIZE = 24;
    private static final Component CONFIG_BUTTON_LABEL = Component.translatable("ui.eg_bedrock_books.config.title");
    private static final CustomSpriteButton.ButtonConfig CONFIG_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config_button"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config_button_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config_button_focus")
    );

    // pagination
    protected static final int MAX_PAGES = WritableBookContent.MAX_PAGES;

    private int currentLeftPageIndex;
    protected List<PageContent> pages = new ArrayList<>();

    protected CustomSpriteButton turnLeftButton;
    protected CustomSpriteButton turnRightButton;

    protected final boolean canEditAndCreatePages;

    // two visible pages
    protected Component leftPageNumberMessage = CommonComponents.EMPTY;
    protected TextView leftPageTextView;
    @Nullable private EditControls leftPageEditControls = null;

    protected Component rightPageNumberMessage = CommonComponents.EMPTY;
    protected TextView rightPageTextView;
    @Nullable private EditControls rightPageEditControls = null;

    // footer buttons
    protected LinearLayout footerButtonLayout;
    protected CustomSpriteButton configButton;

    public AbstractBedrockBookScreen(Component message, boolean editable) {
        super(message);

        this.currentLeftPageIndex = 0;
        this.canEditAndCreatePages = editable;
    }

    @Override
    protected void init() {
        final int editBoxYPos = (this.height / 2) - PAGE_EDIT_BOX_HEIGHT + 45;
        final int turnPageButtonYPos = (this.height / 2) + 47;
        final int editControlsYPos = (this.height / 2) + 44;

        CustomSpriteButton closeButton = new CustomSpriteButton(
            (this.width / 2) + (CENTER_PADDING / 2) + 120,
            (this.height / 2) - PAGE_EDIT_BOX_HEIGHT + 33,
            CLOSE_BUTTON_SIZE,
            CLOSE_BUTTON_SIZE,
            button -> this.onClose(),
            CLOSE_BUTTON_LABEL,
            CLOSE_BUTTON_CONFIG
        );
        this.addRenderableWidget(closeButton);

        // left page
        TextViewAndWidget<PageContent, TextView> leftPageWidget = createTextWidgetAndView((this.width / 2) - (CENTER_PADDING / 2) - PAGE_EDIT_BOX_WIDTH, editBoxYPos, PageSide.LEFT);
        this.leftPageTextView = leftPageWidget.view();
        if(leftPageWidget.widget() != null) {
            this.addRenderableWidget(leftPageWidget.widget());
            this.setInitialFocus(leftPageWidget.widget());
        }

        if(this.canEditAndCreatePages) {
            this.leftPageEditControls = new EditControls(
                (this.width / 2) - (CENTER_PADDING / 2) - (PAGE_EDIT_BOX_WIDTH / 2) - 42,
                editControlsYPos,
                new EditControls.Actions(
                    () -> this.handlePageMove(PageMoveDirection.LEFT, this.currentLeftPageIndex),
                    () -> this.handleAddPage(this.currentLeftPageIndex),
                    () -> this.handlePageDelete(this.currentLeftPageIndex),
                    () -> this.handlePageMove(PageMoveDirection.RIGHT, this.currentLeftPageIndex)
                )
            );
            this.leftPageEditControls.visitWidgets(this::addRenderableWidget);
            this.addRenderableOnly(this.leftPageEditControls);
        }

        addWidgetsBetweenPages();

        // right page
        TextViewAndWidget<PageContent, TextView> rightPageWidget = createTextWidgetAndView((this.width / 2) + (CENTER_PADDING / 2), editBoxYPos, PageSide.RIGHT);
        this.rightPageTextView = rightPageWidget.view();
        if(rightPageWidget.widget() != null) {
            this.addRenderableWidget(rightPageWidget.widget());
        }

        if(this.canEditAndCreatePages) {
            this.rightPageEditControls = new EditControls(
                (this.width / 2) - (CENTER_PADDING / 2) + 42,
                editControlsYPos,
                new EditControls.Actions(
                    () -> this.handlePageMove(PageMoveDirection.LEFT, this.currentLeftPageIndex + 1),
                    () -> this.handleAddPage(this.currentLeftPageIndex + 1),
                    () -> this.handlePageDelete(this.currentLeftPageIndex + 1),
                    () -> this.handlePageMove(PageMoveDirection.RIGHT, this.currentLeftPageIndex + 1)
                )
            );
            this.rightPageEditControls.visitWidgets(this::addRenderableWidget);
            this.addRenderableOnly(this.rightPageEditControls);
        }

        // navigation buttons
        this.turnLeftButton = new CustomSpriteButton(
            (this.width / 2) - 146,
            turnPageButtonYPos,
            TURN_PAGE_BUTTON_SIZE,
            TURN_PAGE_BUTTON_SIZE,
            (button) -> this.turnBackPage(),
            PAGE_LEFT_BUTTON_LABEL,
            PAGE_LEFT_BUTTON_CONFIG
        );
        this.addRenderableWidget(turnLeftButton);

        this.turnRightButton = new CustomSpriteButton(
            (this.width / 2) + 123,
            turnPageButtonYPos,
            TURN_PAGE_BUTTON_SIZE,
            TURN_PAGE_BUTTON_SIZE,
            (button) -> this.turnForwardPage(),
            PAGE_RIGHT_BUTTON_LABEL,
            PAGE_RIGHT_BUTTON_CONFIG
        );
        this.addRenderableWidget(turnRightButton);

        // footer buttons
        this.footerButtonLayout = LinearLayout.horizontal().spacing(FOOTER_BUTTON_SPACING);
        makeFooterButtons();
        this.footerButtonLayout.arrangeElements();
        this.footerButtonLayout.visitWidgets(this::addRenderableWidget);

        // general setup
        updateVisibleContents();

        this.addConfigButton();
    }

    protected abstract TextViewAndWidget<PageContent, TextView> createTextWidgetAndView(int x, int y, PageSide side);

    protected void addWidgetsBetweenPages() {
    }

    protected void makeFooterButtons() {
    }

    protected void addConfigButton() {
        this.configButton = new CustomSpriteButton(
            4,
            this.height - CONFIG_BUTTON_SIZE - 4,
            CONFIG_BUTTON_SIZE,
            CONFIG_BUTTON_SIZE,
            (button) -> ConfigScreenBehaviour.openConfigScreen(this),
            CONFIG_BUTTON_LABEL,
            CONFIG_BUTTON_CONFIG
        );
        this.addRenderableWidget(configButton);
    }

    protected void updateVisibleContents() {
        this.turnLeftButton.visible = true;
        this.turnRightButton.visible = true;

        if(this.currentLeftPageIndex <= 1) {
            this.turnLeftButton.visible = false;
        }
        if((this.currentLeftPageIndex + 2 >= this.getCurrentAmountOfPages() && !this.canEditAndCreatePages) || this.currentLeftPageIndex + 2 >= MAX_PAGES) {
            this.turnRightButton.visible = false;
        }

        this.leftPageNumberMessage = getPageIndicatorMessage(this.currentLeftPageIndex);
        this.leftPageTextView.setValue(getOrCreatePageIfPossible(this.currentLeftPageIndex), true);

        int rightPageIndex = this.currentLeftPageIndex + 1;
        if(this.getCurrentAmountOfPages() % 2 == 1 && this.currentLeftPageIndex >= this.getCurrentAmountOfPages() - 1) {
            this.rightPageNumberMessage = Component.empty();
            this.rightPageTextView.setVisibility(false);
            if(this.rightPageEditControls != null) {
                this.rightPageEditControls.setVisibility(false);
            }
        } else {
            this.rightPageNumberMessage = getPageIndicatorMessage(this.currentLeftPageIndex + 1);
            this.rightPageTextView.setVisibility(true);
            this.rightPageTextView.setValue(getPageOrEmpty(rightPageIndex), true);
            if(this.rightPageEditControls != null) {
                this.rightPageEditControls.setVisibility(true);
            }
        }

        if(this.canEditAndCreatePages) {
            if(this.leftPageEditControls != null) {
                this.leftPageEditControls.setMoveBackButtonVisible(this.currentLeftPageIndex > 0);
                this.leftPageEditControls.setMoveForwardButtonVisible(this.currentLeftPageIndex < this.getCurrentAmountOfPages() - 1);
            }

            if(this.rightPageEditControls != null) {
                this.rightPageEditControls.setMoveBackButtonVisible(rightPageIndex > 0);
                this.rightPageEditControls.setMoveForwardButtonVisible(rightPageIndex < this.getCurrentAmountOfPages() - 1);
            }
        }
    }

    protected void ensureEvenPageIndex(int newPageIndex) {
        newPageIndex = Math.max(0, newPageIndex);
        if(newPageIndex % 2 == 1) {
            this.currentLeftPageIndex = newPageIndex - 1;
            return;
        }
        this.currentLeftPageIndex = newPageIndex;
    }

    protected int getCurrentLeftPageIndex() {
        return this.currentLeftPageIndex;
    }

    // page edit controls
    protected void resetEditControls() {
        if(this.canEditAndCreatePages) {
            if(this.leftPageEditControls != null) {
                this.leftPageEditControls.toggleControls(false);
            }
            if(this.rightPageEditControls != null) {
                this.rightPageEditControls.toggleControls(false);
            }
        }
    }

    protected void handlePageMove(PageMoveDirection direction, int index) {
        if(direction == PageMoveDirection.LEFT && index > 0 && index < this.pages.size()) {
            PageContent currentPage = this.pages.get(index);
            PageContent previousPage = this.pages.get(index - 1);
            this.pages.set(index, previousPage);
            this.pages.set(index - 1, currentPage);
        } else if(index < this.pages.size() - 1) {
            PageContent currentPage = this.pages.get(index);
            PageContent nextPage = this.pages.get(index + 1);
            this.pages.set(index, nextPage);
            this.pages.set(index + 1, currentPage);
        }
        updateVisibleContents();
    }

    protected abstract PageContent getEmptyPageContent();

    protected void handleAddPage(int index) {
        this.addPage(this.getEmptyPageContent(), index);
        updateVisibleContents();
    }

    protected void handlePageDelete(int index) {
        if(index >= this.pages.size()) return;
        this.pages.remove(index);
        updateVisibleContents();
    }

    // page adding / editing
    protected void addPage(PageContent contents) {
        if(!this.canEditAndCreatePages) return;
        addPage(contents, this.pages.size());
    }

    protected void addPage(PageContent contents, int index) {
        if(!this.canEditAndCreatePages) return;
        if(this.pages.size() >= MAX_PAGES) return;
        this.pages.add(index, contents);
    }

    protected PageContent getOrCreatePageIfPossible(int index) {
        if(index > this.pages.size() - 1) {
            if(!this.canEditAndCreatePages) return this.getEmptyPageContent();
            addPage(this.getEmptyPageContent());
            updateVisibleContents();
        }
        return this.pages.get(index);
    }

    protected PageContent getPageOrEmpty(int index) {
        if(index > this.pages.size() - 1) {
            return this.getEmptyPageContent();
        }
        return this.pages.get(index);
    }

    protected void setPageContent(PageContent contents, int index) {
        if(index > this.pages.size() - 1) {
            this.addPage(contents);
            return;
        }
        this.pages.set(index, contents);
    }

    // page viewing
    protected void turnForwardPage() {
        if(this.currentLeftPageIndex + 2 >= MAX_PAGES) return;
        if(this.currentLeftPageIndex + 3 >= this.getCurrentAmountOfPages() && this.canEditAndCreatePages) {
            // at the end of the book and can create pages
            if(this.getCurrentAmountOfPages() % 2 == 0) {
                // even amount of pages, add two new
                addPage(this.getEmptyPageContent());
                addPage(this.getEmptyPageContent());
            }
            else if(this.currentLeftPageIndex + 1 == this.getCurrentAmountOfPages()) {
                // started on odd amount of pages, add 1 for prev double page, 2 new
                addPage(this.getEmptyPageContent());
                addPage(this.getEmptyPageContent());
                addPage(this.getEmptyPageContent());
            }
            else if(this.getCurrentAmountOfPages() % 2 == 1) {
                // turned onto a double page with 1 page, add 1 to even it out
                addPage(this.getEmptyPageContent());
            }
        }
        ensureEvenPageIndex(this.currentLeftPageIndex + 2);
        resetEditControls();
        updateVisibleContents();
    }

    protected void turnBackPage() {
        ensureEvenPageIndex(Math.max(this.currentLeftPageIndex - 2, 0));
        resetEditControls();
        updateVisibleContents();
    }

    protected int getCurrentAmountOfPages() {
        return this.pages.size();
    }

    protected Component getPageIndicatorMessage(int index) {
        int offsetIndex = index + 1;
        if(offsetIndex > this.getCurrentAmountOfPages()) return CommonComponents.EMPTY;
        return Component.translatable(BOOK_PAGE_INDICATOR, offsetIndex, this.getCurrentAmountOfPages());
    }

    // general visuals and accessibility
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert this.minecraft != null;
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP && this.turnLeftButton.visible) {
            this.turnLeftButton.onPress();
            this.turnLeftButton.playDownSound(this.minecraft.getSoundManager());
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN && this.turnRightButton.visible) {
            this.turnRightButton.onPress();
            this.turnLeftButton.playDownSound(this.minecraft.getSoundManager());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public @NotNull Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.getPageIndicatorMessage(this.currentLeftPageIndex));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        final int pageNumberYPos = (this.height / 2) - PAGE_EDIT_BOX_HEIGHT + 35;

        final int leftPageNumberWidth = this.font.width(this.leftPageNumberMessage);
        guiGraphics.drawString(
            this.font,
            this.leftPageNumberMessage,
            (this.width / 2) - (CENTER_PADDING / 2) - (PAGE_EDIT_BOX_WIDTH / 2) - (leftPageNumberWidth / 2),
            pageNumberYPos,
            PAGE_INDICATOR_COLOUR,
            false
        );

        final int rightPageNumberWidth = this.font.width(this.rightPageNumberMessage);
        guiGraphics.drawString(
            this.font,
            this.rightPageNumberMessage,
            (this.width / 2) + (CENTER_PADDING / 2) + (PAGE_EDIT_BOX_WIDTH / 2) - (rightPageNumberWidth / 2),
            pageNumberYPos,
            PAGE_INDICATOR_COLOUR,
            false
        );

        if(InputUtil.shouldShowDebugVariables()) {
            guiGraphics.drawString(font, "leftPageIndex: " + this.getCurrentLeftPageIndex(), 0, 56, -1);
        }
    }

    protected abstract ResourceLocation getBackgroundTexture();

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderMinecraftBackgrounds(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            getBackgroundTexture(),
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

    protected void renderMinecraftBackgrounds(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft != null && this.minecraft.level == null) {
            this.renderPanorama(guiGraphics, partialTick);
            this.renderBlurredBackground(guiGraphics);
            this.renderMenuBackground(guiGraphics);
        } else {
            this.renderTransparentBackground(guiGraphics);
        }
    }

    public enum PageMoveDirection {
        LEFT,
        RIGHT;
    }
    public enum PageSide {
        LEFT,
        RIGHT;
    }
    public record TextViewAndWidget<Value, TextView extends TextAreaView<Value>>(TextView view, @Nullable AbstractWidget widget) {
    }
}
