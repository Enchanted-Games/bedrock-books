package games.enchanted.eg_bedrock_books.common.screen.widget;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class EditControls implements Renderable, LayoutElement {
    private static final int EDIT_BUTTON_SIZE = 20;
    private static final int MAX_VISIBLE_BUTTONS = 4;

    private static final Component PENCIL_BUTTON_LABEL = Component.literal("");
    private static final CustomSpriteButton.ButtonConfig PENCIL_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/edit_button"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/edit_button_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/edit_button_focus")
    );
    private static final CustomSpriteButton.ButtonConfig MOVE_BACK_BUTTON = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_left"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_left_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_left_focus")
    );
    private static final CustomSpriteButton.ButtonConfig ADD_PAGE_BUTTON = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/add_page_button"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/add_page_button_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/add_page_button_focus")
    );
    private static final CustomSpriteButton.ButtonConfig DELETE_PAGE_BUTTON = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/delete_page_button"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/delete_page_button_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/delete_page_button_focus")
    );
    private static final CustomSpriteButton.ButtonConfig MOVE_RIGHT_BUTTON = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_right"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_right_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "book/page_right_focus")
    );

    protected int x;
    protected int y;
    protected boolean preventVisibilityUpdates = false;
    private boolean controlsVisible = false;
    protected boolean moveBackButtonVisible = true;
    protected boolean moveForwardButtonVisible = true;

    protected final LinearLayout layout;
    protected final CustomSpriteButton pencilButton;
    protected final Actions buttonActions;
    protected final CustomSpriteButton moveBackButton;
    protected final CustomSpriteButton addButton;
    protected final CustomSpriteButton deleteButton;
    protected final CustomSpriteButton moveForwardButton;

    public EditControls(int x, int y, Actions buttonActions) {
        this.x = x;
        this.y = y;
        this.buttonActions = buttonActions;

        this.layout = new LinearLayout(EDIT_BUTTON_SIZE * MAX_VISIBLE_BUTTONS, EDIT_BUTTON_SIZE, LinearLayout.Orientation.HORIZONTAL);
        this.layout.setPosition(x, y);

        this.pencilButton = new CustomSpriteButton(0, 0, EDIT_BUTTON_SIZE, EDIT_BUTTON_SIZE, button -> this.toggleControls(true), PENCIL_BUTTON_LABEL, PENCIL_BUTTON_CONFIG);

        this.moveBackButton = new CustomSpriteButton(0, 0, EDIT_BUTTON_SIZE, EDIT_BUTTON_SIZE, button -> this.buttonActions.moveBackPressed().run(), PENCIL_BUTTON_LABEL, MOVE_BACK_BUTTON);
        this.moveBackButton.visible = false;
        this.layout.addChild(this.moveBackButton);

        this.addButton = new CustomSpriteButton(0, 0, EDIT_BUTTON_SIZE, EDIT_BUTTON_SIZE, button -> this.buttonActions.addPressed().run(), PENCIL_BUTTON_LABEL, ADD_PAGE_BUTTON);
        this.addButton.visible = false;
        this.layout.addChild(this.addButton);

        this.deleteButton = new CustomSpriteButton(0, 0, EDIT_BUTTON_SIZE, EDIT_BUTTON_SIZE, button -> this.buttonActions.deletePressed().run(), PENCIL_BUTTON_LABEL, DELETE_PAGE_BUTTON);
        this.deleteButton.visible = false;
        this.layout.addChild(this.deleteButton);

        this.moveForwardButton = new CustomSpriteButton(0, 0, EDIT_BUTTON_SIZE, EDIT_BUTTON_SIZE, button -> this.buttonActions.moveForwardPressed().run(), PENCIL_BUTTON_LABEL, MOVE_RIGHT_BUTTON);
        this.moveForwardButton.visible = false;
        this.layout.addChild(this.moveForwardButton);

        this.layout.arrangeElements();
        this.repositionElements();
    }

    public void toggleControls(boolean showControls) {
        if(this.preventVisibilityUpdates) return;

        this.controlsVisible = showControls;

        this.pencilButton.visible = !showControls;
        this.moveBackButton.visible = showControls && this.moveBackButtonVisible;
        this.addButton.visible = showControls;
        this.deleteButton.visible = showControls;
        this.moveForwardButton.visible = showControls && this.moveForwardButtonVisible;

        this.repositionElements();
    }

    public void toggleControls() {
        this.toggleControls(!this.controlsVisible);
    }

    public void updateControlVisibility() {
        this.toggleControls(this.controlsVisible);
    }

    public void setMoveForwardButtonVisible(boolean newValue) {
        this.moveForwardButtonVisible = newValue;
        updateControlVisibility();
        repositionElements();
    }

    public void setMoveBackButtonVisible(boolean newValue) {
        this.moveBackButtonVisible = newValue;
        updateControlVisibility();
        repositionElements();
    }

    protected int getVisibleButtons() {
        return 2 + (this.moveBackButtonVisible ? 1 : 0) + (this.moveForwardButtonVisible ? 1 : 0);
    }

    public void setVisibility(boolean visibility) {
        this.preventVisibilityUpdates = !visibility;
        if(!visibility) {
            visitWidgets(widget -> widget.visible = false);
        } else {
            toggleControls(this.controlsVisible);
        }
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> visitor) {
        visitor.accept(this.pencilButton);
        this.layout.visitChildren(element -> element.visitWidgets(visitor));
    }

    public void repositionElements() {
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        this.layout.arrangeElements();
        int moveLeftOrRight = this.moveForwardButtonVisible ? -1 : 1;
        this.layout.setPosition(this.x + (getVisibleButtons() == 3 ? (EDIT_BUTTON_SIZE / 2) * moveLeftOrRight: 0), this.y);

        FrameLayout.centerInRectangle(this.pencilButton, this.getRectangle());
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return EDIT_BUTTON_SIZE * MAX_VISIBLE_BUTTONS;
    }

    @Override
    public int getHeight() {
        return EDIT_BUTTON_SIZE;
    }

    @Override
    public @NotNull ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.x, this.y, getWidth(), getHeight());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) return;
        guiGraphics.fillGradient(getRectangle().left(), getRectangle().top(), getRectangle().right(), getRectangle().bottom(), 0xaa00ffff, 0xaa00ffff);
        guiGraphics.fillGradient(this.layout.getX(), this.layout.getY(), this.layout.getX() + 12, this.layout.getY() + 12, 0xaa00ff00, 0xaa00ff00);
    }

    public record Actions(Runnable moveBackPressed, Runnable addPressed, Runnable deletePressed, Runnable moveForwardPressed) {
    }
}
