package games.enchanted.eg_bedrock_books.common.screen.widget;

import net.minecraft.network.chat.Component;

public class TogglableSpriteButton extends CustomSpriteButton {
    protected final ButtonConfig firstConfig;
    protected final ButtonConfig secondConfig;
    protected boolean toggle;

    public TogglableSpriteButton(int x, int y, int width, int height, OnPress onPress, Component message, ButtonConfig buttonConfig, ButtonConfig toggleConfig) {
        super(x, y, width, height, onPress, message, buttonConfig);
        this.firstConfig = buttonConfig;
        this.secondConfig = toggleConfig;
        this.toggle = false;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        this.setButtonConfig(toggle ? this.secondConfig : this.firstConfig);
    }

    public boolean getToggle() {
        return this.toggle;
    }
}
