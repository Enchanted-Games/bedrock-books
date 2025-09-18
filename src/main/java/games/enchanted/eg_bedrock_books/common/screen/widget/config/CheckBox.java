package games.enchanted.eg_bedrock_books.common.screen.widget.config;

import games.enchanted.eg_bedrock_books.common.screen.widget.TogglableSpriteButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class CheckBox extends TogglableSpriteButton {
    protected static final int SIZE = 16;

    protected final ValueChange valueChange;

    public CheckBox(int x, int y, boolean initialValue, ValueChange valueChange, Component message, ButtonConfig checkedConfig, ButtonConfig uncheckedConfig) {
        super(x, y, SIZE, SIZE, button -> {}, message, checkedConfig, uncheckedConfig);
        this.valueChange = valueChange;
        this.setToggle(initialValue);
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return CommonComponents.joinForNarration(
            this.getMessage(),
            Component.translatable("ui.eg_bedrock_books.widget.checkbox." + (this.getToggle() ? "checked" : "unchecked") + ".narration")
        );
    }

    @Override
    public void onPress() {
        super.onPress();
        this.setToggle(!this.getToggle());
        this.valueChange.onPress(this.getToggle());
    }

    @Environment(EnvType.CLIENT)
    public interface ValueChange {
        void onPress(boolean value);
    }
}
