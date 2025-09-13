package games.enchanted.eg_bedrock_books.common.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CustomSpriteButton extends Button {
    protected ButtonConfig buttonConfig;

    public CustomSpriteButton(int x, int y, int width, int height, OnPress onPress, Component message, ButtonConfig buttonConfig) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.buttonConfig = buttonConfig;
    }

    public void setButtonConfig(ButtonConfig config) {
        this.buttonConfig = config;
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            this.isHovered() ? buttonConfig.hoverSprite() : this.isFocused() ? buttonConfig.focusedSprite() : buttonConfig.sprite(),
            this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight()
        );
    }

    public void playDownSound(SoundManager handler) {
        if (buttonConfig.soundInstance != null) {
            handler.play(buttonConfig.soundInstance().get());
        }
    }

    public record ButtonConfig(@Nullable Supplier<SoundInstance> soundInstance, ResourceLocation sprite, ResourceLocation hoverSprite, ResourceLocation focusedSprite) {
    }
}
