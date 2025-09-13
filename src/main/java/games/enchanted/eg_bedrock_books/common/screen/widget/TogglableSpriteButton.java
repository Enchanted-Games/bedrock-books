package games.enchanted.eg_bedrock_books.common.screen.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TogglableSpriteButton extends CustomSpriteButton {
    protected final ButtonConfig firstConfig;
    protected final ButtonConfig secondConfig;
    protected boolean toggle;
    protected PressHandler pressHandler;

    public TogglableSpriteButton(int x, int y, int width, int height, PressHandler pressHandler, Component message, ButtonConfig buttonConfig, ButtonConfig toggleConfig) {
        super(x, y, width, height, button -> {}, message, buttonConfig);
        this.firstConfig = buttonConfig;
        this.secondConfig = toggleConfig;
        this.toggle = false;
        this.pressHandler = pressHandler;
    }

    @Override
    public void onPress() {
        this.pressHandler.press(this, this.toggle);
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        this.setButtonConfig(toggle ? this.secondConfig : this.firstConfig);
    }

    public interface PressHandler {
        void press(Button button, boolean toggle);
    }
}
