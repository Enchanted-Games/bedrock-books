package games.enchanted.eg_bedrock_books.common.screen.widget.config;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class KeyBox extends AbstractButton {
    public static final CustomSpriteButton.ButtonConfig DEFAULT_BUTTON_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/key_box"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/key_box_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/key_box_focus")
    );
    protected static final int INLINE_PADDING = 3;

    private final KeyPress onKeyPress;
    private final CustomSpriteButton.ButtonConfig buttonConfig;

    private InputConstants.Key selectedKey;
    private boolean acceptingKey = false;

    public KeyBox(int x, int y, InputConstants.Key initialKey, KeyPress onKeyPress, Component message, CustomSpriteButton.ButtonConfig buttonConfig) {
        super(x, y, 54, 16, message);
        this.onKeyPress = onKeyPress;
        this.buttonConfig = buttonConfig;

        this.selectedKey = initialKey;
    }

    public KeyBox(int x, int y, InputConstants.Key initialKey, KeyPress onKeyPress, Component message) {
        this(x, y, initialKey, onKeyPress, message, DEFAULT_BUTTON_CONFIG);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(this.acceptingKey) {
            this.selectedKey = InputConstants.getKey(keyCode, scanCode);
            this.onKeyPress.keyPress(this.selectedKey);
            this.acceptingKey = false;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onPress() {
        this.acceptingKey = !this.acceptingKey;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
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
            guiGraphics.drawString(font, ">", this.getX() - leftWidth - leftWidth / 2, this.getY() + font.lineHeight / 2, -1);
            int rightWidth = font.width("<");
            guiGraphics.drawString(font, "<", this.getX() + this.getWidth() + rightWidth / 2, this.getY() + font.lineHeight / 2, -1);
        }
    }

    protected void drawKeyLabel(GuiGraphics guiGraphics, Component keyName, int minX, int minY, int maxX, int maxY) {
        Font font = Minecraft.getInstance().font;
        AbstractWidget.renderScrollingString(guiGraphics, font, keyName, minX, minY, maxX, maxY, -1);

        if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) {
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

    public interface KeyPress {
        void keyPress(InputConstants.Key newKey);
    }
}
