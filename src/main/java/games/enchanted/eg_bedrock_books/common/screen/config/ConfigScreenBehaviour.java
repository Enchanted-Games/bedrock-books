package games.enchanted.eg_bedrock_books.common.screen.config;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import games.enchanted.eg_bedrock_books.common.screen.AbstractBedrockBookScreen;
import games.enchanted.eg_bedrock_books.common.screen.BedrockBookEditScreen;
import games.enchanted.eg_bedrock_books.common.screen.BedrockBookViewScreen;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.KeyBox;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.DummyTextAreaView;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.TextAreaView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigScreenBehaviour extends AbstractBedrockBookScreen<String, TextAreaView<String>> {
    protected static final Component CONFIG_TITLE = Component.translatable("ui.eg_bedrock_books.config.title");
    protected static final Component RESET_BUTTON_COMPONENT = Component.translatable("ui.eg_bedrock_books.config.reset");
    protected static final Component RESET_TITLE_COMPONENT = Component.translatable("ui.eg_bedrock_books.config.reset.title").withStyle(Style.EMPTY.withBold(true));
    protected static final Component RESET_MESSAGE_COMPONENT = Component.translatable("ui.eg_bedrock_books.config.reset.warning");
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/config_background.png");

    protected final @Nullable Screen returnScreen;
    protected final boolean alwaysBlurBackground;
    protected boolean saveWhenOnCloseCalled = false;

    protected ConfigScreenBehaviour(@Nullable Screen returnScreen, boolean alwaysBlurBackground) {
        super(CONFIG_TITLE, false);
        this.returnScreen = returnScreen;
        this.alwaysBlurBackground = alwaysBlurBackground;

        this.pages = List.of(getEmptyPageContent(), getEmptyPageContent(), getEmptyPageContent());
    }

    @Override
    protected void makeFooterButtons() {
        this.footerButtonLayout.addChild(
            Button.builder(CommonComponents.GUI_CANCEL, button -> this.cancelAndClose())
                .width(FOOTER_BUTTON_WIDTH)
            .build()
        );
        this.footerButtonLayout.addChild(
            Button.builder(RESET_BUTTON_COMPONENT, button -> this.resetWithConfirmation())
                .width(FOOTER_BUTTON_WIDTH)
            .build()
        );
        this.footerButtonLayout.addChild(
            Button.builder(SAVE_BUTTON_COMPONENT, button -> this.saveAndClose())
                .width(FOOTER_BUTTON_WIDTH)
            .build()
        );
        this.footerButtonLayout.setPosition((this.width / 2) - (FOOTER_BUTTON_WIDTH * 3 + FOOTER_BUTTON_SPACING * 2) / 2, (this.height / 2) + 90);
    }

    protected void cancelAndClose() {
        this.saveWhenOnCloseCalled = false;
        this.onClose();
    }

    protected void saveAndClose() {
        this.saveWhenOnCloseCalled = true;
        this.onClose();
    }

    protected void resetWithConfirmation() {
        ConfirmScreen confirmScreen = new ConfirmScreen(confirmed -> {
            Minecraft.getInstance().setScreen(this);
            if(!confirmed) {
                return;
            }
            ConfigOptions.resetAndSaveAllOptions();
            Minecraft.getInstance().setScreen(new ConfigScreenVisual(this.returnScreen, this.alwaysBlurBackground));
        }, RESET_TITLE_COMPONENT, RESET_MESSAGE_COMPONENT);
        Minecraft.getInstance().setScreen(confirmScreen);
    }

    @Override
    public void onClose() {
        if(this.minecraft != null && this.returnScreen != null) {
            this.minecraft.setScreen(returnScreen);
        }
        if(this.saveWhenOnCloseCalled) {
            ConfigOptions.saveIfAnyDirtyOptions();
        } else {
            ConfigOptions.clearAllPendingValues();
        }
        if(this.returnScreen instanceof BedrockBookViewScreen viewScreen) {
            viewScreen.refreshPageContent();
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
        return new ConfigScreenVisual(returnScreen, true);
    }

    public static Screen makeScreen(@Nullable Screen returnScreen) {
        return new ConfigScreenVisual(returnScreen);
    }

    public static void openConfigScreen(@Nullable Screen returnScreen) {
        Minecraft.getInstance().setScreen(makeScreen(returnScreen));
    }
}
