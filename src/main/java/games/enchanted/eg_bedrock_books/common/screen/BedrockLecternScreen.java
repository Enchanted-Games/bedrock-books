package games.enchanted.eg_bedrock_books.common.screen;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    public BedrockLecternScreen(LecternMenu menu) {
        this.menu = menu;
    }

    @Override
    protected void init() {
        super.init();

        this.menu.addSlotListener(containerListener);
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
    protected void turnForwardPage() {
        super.turnForwardPage();
        setContainerPageIndex(this.getCurrentLeftPageIndex());
    }

    @Override
    protected void turnBackPage() {
        super.turnBackPage();
        setContainerPageIndex(this.getCurrentLeftPageIndex());
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
