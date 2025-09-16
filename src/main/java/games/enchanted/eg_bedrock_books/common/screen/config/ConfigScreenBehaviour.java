package games.enchanted.eg_bedrock_books.common.screen.config;

import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigScreenBehaviour extends ConfigScreen {
    protected ConfigScreenBehaviour(@Nullable Screen returnScreen, boolean alwaysBlurBackground) {
        super(returnScreen, alwaysBlurBackground);
    }

    protected ConfigScreenBehaviour(@Nullable Screen returnScreen) {
        super(returnScreen, false);
    }

    @Override
    protected void addWidgetsBetweenPages() {
        super.addWidgetsBetweenPages();
    }

    @Override
    protected void saveConfig() {
        ConfigOptions.saveIfAnyDirtyOptions();
    }

    @Override
    protected Component getPageIndicatorMessage(int index) {
        return switch (index) {
            case 0 -> Component.translatable("ui.eg_bedrock_books.config.page.general");
            case 1 -> Component.translatable("ui.eg_bedrock_books.config.page.debug");
            default -> super.getPageIndicatorMessage(index);
        };
    }
}
