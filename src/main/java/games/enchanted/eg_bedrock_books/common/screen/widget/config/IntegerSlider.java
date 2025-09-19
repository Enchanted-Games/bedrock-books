package games.enchanted.eg_bedrock_books.common.screen.widget.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class IntegerSlider extends AbstractSliderButton {
    private final OnValueChange valueChange;
    private final int min;
    private final int max;

    public IntegerSlider(int x, int y, int width, int height, Component message, int initialValue, OnValueChange valueChange, int min, int max) {
        super(x, y, width, height, message, (double) initialValue / (max - min));
        this.valueChange = valueChange;
        if(min >= max) {
            throw new IllegalArgumentException("Min cannot be greater than or equal to max");
        }
        this.min = min;
        this.max = max;
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
        renderScrollingString(guiGraphics, font, Component.literal("" + getIntegerValue()), minX, this.getY(), maxX, this.getY() + this.getHeight(), color);
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
