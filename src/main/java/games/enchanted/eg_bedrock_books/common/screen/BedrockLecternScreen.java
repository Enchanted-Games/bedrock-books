package games.enchanted.eg_bedrock_books.common.screen;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
import games.enchanted.eg_bedrock_books.common.screen.widget.TogglableSpriteButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class BedrockLecternScreen extends BedrockBookViewScreen implements MenuAccess<LecternMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/lectern_background.png");

    protected static final int FOOTER_BUTTON_WIDTH = 120;
    protected static final int TEXT_OFFSET_LEFT = 7;
    protected static final int TEXT_OFFSET_RIGHT = 7;

    protected static final Component TAKE_BOOK_COMPONENT = Component.translatable("lectern.take_book");

    protected static final int RIBBON_WIDTH = 20;
    protected static final int RIBBON_HEIGHT = 116;

    Component LEFT_RIBBON_LABEL = Component.translatable("ui.eg_bedrock_books.lectern.bookmark_left_label");
    protected static final CustomSpriteButton.ButtonConfig LEFT_RIBBON_SELECTED_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/left_page_selected_ribbon"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/left_page_selected_ribbon_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/left_page_selected_ribbon_focus")
    );
    protected static final CustomSpriteButton.ButtonConfig LEFT_RIBBON_UNSELECTED_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/left_page_unselected_ribbon"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/left_page_unselected_ribbon_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/left_page_unselected_ribbon_focus")
    );

    Component RIGHT_RIBBON_LABEL = Component.translatable("ui.eg_bedrock_books.lectern.bookmark_right_label");
    protected static final CustomSpriteButton.ButtonConfig RIGHT_RIBBON_SELECTED_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/right_page_selected_ribbon"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/right_page_selected_ribbon_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/right_page_selected_ribbon_focus")
    );
    protected static final CustomSpriteButton.ButtonConfig RIGHT_RIBBON_UNSELECTED_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/right_page_unselected_ribbon"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/right_page_unselected_ribbon_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/lectern/right_page_unselected_ribbon_focus")
    );

    private final LecternMenu menu;
    private final ContainerListener containerListener = new ContainerListener() {
        @Override
        public void slotChanged(AbstractContainerMenu menu, int slotIndex, ItemStack stack) {
            BedrockLecternScreen.this.bookDataChanged();
        }

        @Override
        public void dataChanged(AbstractContainerMenu menu, int slotIndex, int value) {
            if (slotIndex == 0) {
                BedrockLecternScreen.this.pageIndexChanged();
            }
        }
    };
    protected TogglableSpriteButton leftPageRibbon;
    protected TogglableSpriteButton rightPageRibbon;

    public BedrockLecternScreen(LecternMenu menu) {
        this.menu = menu;
    }

    @Override
    protected void init() {
        final int ribbonYOffset = 111;

        this.leftPageRibbon = new TogglableSpriteButton(
            this.width / 2 - RIBBON_WIDTH,
            (this.height / 2) - ribbonYOffset,
            RIBBON_WIDTH,
            RIBBON_HEIGHT,
            button -> {
                this.setPageIndex(this.getCurrentLeftPageIndex());
                this.updateVisibleContents();
            },
            LEFT_RIBBON_LABEL,
            LEFT_RIBBON_UNSELECTED_CONFIG,
            LEFT_RIBBON_SELECTED_CONFIG
        );
        this.leftPageRibbon.setToggle(true);

        this.rightPageRibbon = new TogglableSpriteButton(
            this.width / 2,
            (this.height / 2) - ribbonYOffset,
            RIBBON_WIDTH,
            RIBBON_HEIGHT,
            button -> {
                this.setPageIndex(this.getCurrentLeftPageIndex() + 1);
                this.updateVisibleContents();
            },
            RIGHT_RIBBON_LABEL,
            RIGHT_RIBBON_UNSELECTED_CONFIG,
            RIGHT_RIBBON_SELECTED_CONFIG
        );

        super.init();

        this.menu.addSlotListener(containerListener);
    }

    @Override
    protected void addWidgetsBetweenPages() {
        super.addWidgetsBetweenPages();
        addRenderableWidget(this.leftPageRibbon);
        addRenderableWidget(this.rightPageRibbon);
    }

    @Override
    protected void makeFooterButtons() {
        this.footerButtonLayout.addChild(Button.builder(CommonComponents.GUI_DONE, button -> {
            this.onClose();
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.addChild(Button.builder(TAKE_BOOK_COMPONENT, button -> {
            this.sendContainerButtonClick(LecternMenu.BUTTON_TAKE_BOOK);
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.setPosition((this.width / 2) - (FOOTER_BUTTON_WIDTH * 2 + FOOTER_BUTTON_SPACING) / 2, (this.height / 2) + 90);
    }

    @Override
    protected int getHorizontalLeftPageTextOffset() {
        return TEXT_OFFSET_LEFT;
    }

    @Override
    protected int getHorizontalRightPageTextOffset() {
        return TEXT_OFFSET_RIGHT;
    }

    @Override
    protected void updateVisibleContents() {
        super.updateVisibleContents();

        if(isContainerOnLeftPage()) {
            this.leftPageRibbon.setToggle(true);
            this.rightPageRibbon.setToggle(false);
            // more pages after current selected container page
            this.turnRightButton.visible = this.getContainerPageIndex() < this.getCurrentAmountOfPages() - 1;
        } else {
            this.leftPageRibbon.setToggle(false);
            this.rightPageRibbon.setToggle(true);
            this.turnLeftButton.visible = true;
        }

        boolean morePagesAfterLeft = this.getCurrentLeftPageIndex() < this.getCurrentAmountOfPages() - 1;
        this.rightPageRibbon.visible = morePagesAfterLeft;

        this.leftPageRibbon.visible = !this.pages.isEmpty();
    }

    @Override
    protected void turnForwardPage() {
        if(isContainerOnLeftPage()) {
            if(this.getContainerPageIndex() > this.getCurrentAmountOfPages() - 1) return;
            // container is on 'left page', dont increment screen page index
            setContainerPageIndex(getCurrentLeftPageIndex() + 1);
            updateVisibleContents();
        } else {
            // container is on 'right page', increment to next double page
            super.turnForwardPage();
            setContainerPageIndex(getCurrentLeftPageIndex());
        }
    }

    @Override
    protected void turnBackPage() {
        if(isContainerOnLeftPage()) {
            // container is on 'left page', increment back to next double page
            setContainerPageIndex(getCurrentLeftPageIndex() - 1);
            super.turnBackPage();
        } else {
            if(this.getContainerPageIndex() <= 0) return;
            // container is on 'right page', turn back by 1
            setContainerPageIndex(getCurrentLeftPageIndex());
            updateVisibleContents();
        }
    }

    protected boolean isContainerOnLeftPage() {
        return this.getCurrentLeftPageIndex() == this.getContainerPageIndex();
    }

    @Override
    protected void setPageIndex(int index) {
        super.setPageIndex(index);
        setContainerPageIndex(index);
    }

    protected void setContainerPageIndex(int index) {
        if (index != this.menu.getPage()) {
            this.sendContainerButtonClick(LecternMenu.BUTTON_PAGE_JUMP_RANGE_START + index);
        }
    }

    protected int getContainerPageIndex() {
        return this.menu.getPage();
    }

    protected void bookDataChanged() {
        this.setBookAccess(Objects.requireNonNullElse(BookViewScreen.BookAccess.fromItem(this.menu.getBook()), BookViewScreen.EMPTY_ACCESS));
    }

    protected void pageIndexChanged() {
        this.setPageIndex(this.menu.getPage());
    }

    protected void sendContainerButtonClick(int buttonId) {
        Objects.requireNonNull(this.minecraft, "this.minecraft is null");
        Objects.requireNonNull(this.minecraft.gameMode, "this.minecraft.gameMode is null");
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    @Override
    public void onClose() {
        super.onClose();
        closeServerContainer();
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(containerListener);
    }

    @Override
    protected void closeServerContainer() {
        Objects.requireNonNull(this.minecraft, "this.minecraft is null");
        Objects.requireNonNull(this.minecraft.player, "this.minecraft.player is null");
        this.minecraft.player.closeContainer();
    }

    @Override
    public @NotNull LecternMenu getMenu() {
        return this.menu;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            guiGraphics.drawString(font, "clientLeftPageIndex: " + this.getCurrentLeftPageIndex(), 0, 56, -1);
            guiGraphics.drawString(font, "containerIndex: " + this.menu.getPage(), 0, 64, -1);
        }
    }
}
