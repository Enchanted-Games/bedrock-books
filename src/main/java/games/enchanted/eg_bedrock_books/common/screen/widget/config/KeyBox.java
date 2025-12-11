package games.enchanted.eg_bedrock_books.common.screen.widget.config;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
import games.enchanted.eg_bedrock_books.common.util.InputUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

//? if minecraft: >= 1.21.9 {
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
//?}

public class KeyBox extends AbstractButton {
    public static final int TEXT_COLOUR = 0xff987457;
    public static final int HC_TEXT_COLOUR = 0xffffffff;
    public static final int WIDTH = 52;
    public static final int HEIGHT = 16;
    protected static final int INLINE_PADDING = 4;

    public static final CustomSpriteButton.ButtonConfig DEFAULT_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/key_input"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/key_input_hover"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/key_input_focus")
    );

    private final KeyPress onKeyPress;
    private final CustomSpriteButton.ButtonConfig buttonConfig;

    private InputConstants.Key selectedKey;
    private boolean acceptingKey = false;

    public KeyBox(int x, int y, InputConstants.Key initialKey, KeyPress onKeyPress, Component message, CustomSpriteButton.ButtonConfig buttonConfig) {
        super(x, y, WIDTH, HEIGHT, message);
        this.onKeyPress = onKeyPress;
        this.buttonConfig = buttonConfig;

        this.selectedKey = initialKey;
    }

    public KeyBox(int x, int y, InputConstants.Key initialKey, KeyPress onKeyPress, Component message) {
        this(x, y, initialKey, onKeyPress, message, DEFAULT_BUTTON_CONFIG);
    }

    @Override
    public boolean keyPressed(
        //? if minecraft: >= 1.21.9 {
        KeyEvent keyEvent
        //?} else {
        /*int keyCode, int scanCode, int modifiers
        *///?}
    ) {
        if(this.acceptingKey) {
            this.acceptingKey = false;
            //? if minecraft: >= 1.21.9 {
            int keyCode = keyEvent.key();
            int scanCode = keyEvent.scancode();
            //?}
            if(keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_TAB) {
                //? if minecraft: >= 1.21.9 {
                return super.keyPressed(keyEvent);
                //?} else {
                /*return super.keyPressed(keyCode, scanCode, modifiers);
                *///?}
            }
            this.selectedKey = InputUtil.getKey(keyCode, scanCode);
            this.onKeyPress.keyPress(this.selectedKey);
            return true;
        }
        //? if minecraft: >= 1.21.9 {
        return super.keyPressed(keyEvent);
        //?} else {
        /*return super.keyPressed(keyCode, scanCode, modifiers);
        *///?}
    }

    @Override
    public void onPress(
        //? if minecraft: >= 1.21.9 {
        InputWithModifiers inputWithModifiers
        //?}
    ) {
        this.acceptingKey = !this.acceptingKey;
    }

    @Override
    protected void
    //? if minecraft: <= 1.21.10 {
    /*renderWidget
     *///?} else {
    renderContents
    //?}
    (GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            this.isHovered() ? buttonConfig.hoverSprite() : this.isFocused() ? buttonConfig.focusedSprite() : buttonConfig.sprite(),
            this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight()
        );

        drawKeyLabel(
            guiGraphics,
            this.selectedKey.getDisplayName(),
            this.getX() + INLINE_PADDING,
            this.getY(),
            this.getX() + this.getWidth() - INLINE_PADDING,
            this.getY() + this.getHeight()
        );

        Font font = Minecraft.getInstance().font;
        if(this.acceptingKey) {
            int leftWidth = font.width(">");
            guiGraphics.drawString(font, ">", this.getX() - leftWidth - leftWidth / 2, this.getY() + font.lineHeight / 2, this.getTextColour(), false);
            int rightWidth = font.width("<");
            guiGraphics.drawString(font, "<", this.getX() + this.getWidth() + rightWidth / 2, this.getY() + font.lineHeight / 2, this.getTextColour(), false);
        }

        //? if minecraft: >= 1.21.9 {
        if (this.isHovered()) {
            guiGraphics.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
        }
        //?}
    }

    protected void drawKeyLabel(GuiGraphics guiGraphics, Component keyName, int minX, int minY, int maxX, int maxY) {
        Font font = Minecraft.getInstance().font;
        //? if minecraft: <= 1.21.10 {
        /*AbstractWidget.renderScrollingString(guiGraphics, font, keyName.copy().withStyle(Style.EMPTY.withShadowColor(0)), minX, minY, maxX, maxY + 1, this.getTextColour());
        *///?} else {
        this.renderScrollingStringOverContents(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE), keyName, 1);
        //?}

        if(InputUtil.shouldShowDebugTextBound()) {
            guiGraphics.fill(minX, minY, maxX, maxY, 0xbb00ff00);
        }
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return Component.translatable("narrator.controls.bound", this.getMessage(), this.selectedKey.getDisplayName());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    public boolean isListeningForInput() {
        return this.acceptingKey;
    }

    protected int getTextColour() {
        return ModConstants.isHighContrastPackActive() ? HC_TEXT_COLOUR : TEXT_COLOUR;
    }

    public interface KeyPress {
        void keyPress(InputConstants.Key newKey);
    }
}
