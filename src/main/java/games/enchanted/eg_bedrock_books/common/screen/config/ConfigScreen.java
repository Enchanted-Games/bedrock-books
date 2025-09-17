package games.enchanted.eg_bedrock_books.common.screen.config;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.AbstractBedrockBookScreen;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.KeyBox;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.DummyTextAreaView;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.TextAreaView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigScreen extends AbstractBedrockBookScreen<String, TextAreaView<String>> {
    protected static final Component CONFIG_TITLE = Component.translatable("ui.eg_bedrock_books.config.title");
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/config_background.png");

    protected static final int FOOTER_BUTTON_WIDTH = 120;

    protected final @Nullable Screen returnScreen;
    protected final boolean alwaysBlurBackground;

    protected ConfigScreen(@Nullable Screen returnScreen, boolean alwaysBlurBackground) {
        super(CONFIG_TITLE, false);
        this.returnScreen = returnScreen;
        this.alwaysBlurBackground = alwaysBlurBackground;

        this.pages = List.of(getEmptyPageContent(), getEmptyPageContent(), getEmptyPageContent());
    }

    @Override
    protected void makeFooterButtons() {
        this.footerButtonLayout.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> {
            this.onClose();
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.addChild(Button.builder(SAVE_BUTTON_COMPONENT, button -> {
            this.saveConfig();
            this.onClose();
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.setPosition((this.width / 2) - (FOOTER_BUTTON_WIDTH * 2 + FOOTER_BUTTON_SPACING) / 2, (this.height / 2) + 90);
    }

    protected void saveConfig() {
    }

    @Override
    public void onClose() {
        if(this.minecraft != null && this.returnScreen != null) {
            this.minecraft.setScreen(returnScreen);
        }
    }

    @Override
    protected void addConfigButton() {
    }

    @Override
    protected TextViewAndWidget<String, TextAreaView<String>> createTextWidgetAndView(int x, int y, PageSide side) {
        return new TextViewAndWidget<>(new DummyTextAreaView(), null);
    }

    @Override
    protected String getEmptyPageContent() {
        return "";
    }

    @Override
    public @NotNull Component getNarrationMessage() {
        return CONFIG_TITLE;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if(this.getFocused() instanceof KeyBox keyBox && keyBox.isListeningForInput()) {
            return false;
        }
        return super.shouldCloseOnEsc();
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

    @Override
    protected void renderMinecraftBackgrounds(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(!this.alwaysBlurBackground) {
            super.renderMinecraftBackgrounds(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        if (this.minecraft != null && this.minecraft.level == null) {
            this.renderPanorama(guiGraphics, partialTick);
        }
        this.renderBlurredBackground(guiGraphics);
        this.renderMenuBackground(guiGraphics);
    }

    public static Screen makeScreenForModMenu(@Nullable Screen returnScreen) {
        return new ConfigScreenBehaviour(returnScreen, true);
    }

    public static Screen makeScreen(@Nullable Screen returnScreen) {
        return new ConfigScreenBehaviour(returnScreen);
    }

    public static void openConfigScreen(@Nullable Screen returnScreen) {
        Minecraft.getInstance().setScreen(makeScreen(returnScreen));
    }
}
