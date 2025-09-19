package games.enchanted.eg_bedrock_books.common.screen.widget.config;

import games.enchanted.eg_bedrock_books.common.duck.AbstractSliderButtonAdditions;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IntegerSlider extends AbstractSliderButton implements AbstractSliderButtonAdditions {
    public static final int TEXT_COLOUR = 0xff614128;

    private final OnValueChange valueChange;
    private final int min;
    private final int max;

    private final ResourceLocation backgroundSprite;
    private final CustomSpriteButton.ButtonConfig handleConfig;

    public IntegerSlider(int x, int y, int width, int height, Component message, int initialValue, OnValueChange valueChange, int min, int max, ResourceLocation backgroundSprite, CustomSpriteButton.ButtonConfig handleConfig) {
        super(x, y, width, height, message, (double) initialValue / (max - min));
        this.valueChange = valueChange;
        if(min >= max) {
            throw new IllegalArgumentException("Min cannot be greater than or equal to max");
        }
        this.min = min;
        this.max = max;

        this.backgroundSprite = backgroundSprite;
        this.handleConfig = handleConfig;
    }

    @Override
    public ResourceLocation eg_bedrock_books$getSprite() {
        return this.backgroundSprite;
    }

    @Override
    public ResourceLocation eg_bedrock_books$getHandleSprite() {
        return this.isHovered() ? this.handleConfig.hoverSprite() : this.isFocused() ? this.handleConfig.focusedSprite() : this.handleConfig.sprite();
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return CommonComponents.joinForNarration(super.createNarrationMessage(), Component.translatable("ui.eg_bedrock_books.widget.slider.narration", getIntegerValue()));
    }

    @Override
    protected void updateMessage() {
    }

    @Override
    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int width, int color) {
        int minX = this.getX() + width;
        int maxX = this.getX() + this.getWidth() - width;
        renderScrollingString(
            guiGraphics,
            font,
            Component.literal("" + getIntegerValue()).withStyle(Style.EMPTY
                .withShadowColor(0)
                .withColor(TEXT_COLOUR)
            ),
            minX,
            this.getY(),
            maxX,
            this.getY() + this.getHeight(),
            -1
        );
    }

    protected int getIntegerValue() {
        return (int) Math.round(this.value * (this.max - this.min));
    }

    @Override
    protected void applyValue() {
        this.valueChange.valueChanged(getIntegerValue());
    }

    public interface OnValueChange {
        void valueChanged(int value);
    }
}
