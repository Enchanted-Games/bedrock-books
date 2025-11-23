package games.enchanted.eg_bedrock_books.common.screen.config;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import games.enchanted.eg_bedrock_books.common.config.option.ConfigOption;
import games.enchanted.eg_bedrock_books.common.screen.BedrockLecternScreen;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.CheckBox;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.IntegerSlider;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.KeyBox;
import games.enchanted.eg_bedrock_books.common.screen.widget.scroll.ConfigList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigScreenVisual extends ConfigScreenBehaviour {
    protected static final int MAX_LAYOUT_WIDTH = 120;
    protected static final int MAX_LAYOUT_HEIGHT = 140;
    protected static final int CENTER_PADDING = 24;
    protected static final int COLUMN_GAP = 8;

    protected static final int PAGE_TEXT_COLOUR = 0xff987457;

    public static final CustomSpriteButton.ButtonConfig CHECKBOX_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_unchecked"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_unchecked_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_unchecked_focus")
    );
    public static final CustomSpriteButton.ButtonConfig CHECKBOX_UNCHECKED_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_checked"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_checked_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_checked_focus")
    );

    public static ResourceLocation SLIDER_BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/slider_background");
    public static final CustomSpriteButton.ButtonConfig SLIDER_HANDLE_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/slider_handle"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/slider_handle_hover"),
        ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config/slider_handle_focus")
    );

    protected static final List<ConfigOption<Boolean>> DEBUG_OPTIONS = List.of(
        ConfigOptions.DEBUG_WIDGET_BOUNDS,
        ConfigOptions.DEBUG_TEXT_BOUNDS,
        ConfigOptions.DEBUG_CONTAINER_DATA,
        ConfigOptions.DEBUG_VARIABLES
    );

    protected ConfigScreenVisual(@Nullable Screen returnScreen, boolean alwaysBlurBackground) {
        super(returnScreen, alwaysBlurBackground);
    }

    protected ConfigScreenVisual(@Nullable Screen returnScreen) {
        super(returnScreen, false);
    }

    protected ConfigList generalOptionList;
    protected ConfigList visualOptionList;
    protected ConfigList debugOptionList;

    @Override
    protected void addWidgetsBetweenPages() {
        super.addWidgetsBetweenPages();

        this.generalOptionList = createPageLayout(PageSide.LEFT);
        this.visualOptionList = createPageLayout(PageSide.RIGHT);
        this.debugOptionList = createPageLayout(PageSide.LEFT);

        // general
        final Component closeOnCommandRunLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option.close_when_running_command");
        final CheckBox closeOnCommandRunWidget = new CheckBox(
            0,
            0,
            ConfigOptions.CLOSE_BOOK_WHEN_RUNNING_COMMAND.getPendingOrCurrentValue(),
            ConfigOptions.CLOSE_BOOK_WHEN_RUNNING_COMMAND::setPendingValue,
            closeOnCommandRunLabel,
            CHECKBOX_CONFIG,
            CHECKBOX_UNCHECKED_CONFIG
        );
        closeOnCommandRunWidget.setTooltip(Tooltip.create(Component.translatable("ui.eg_bedrock_books.config.option.close_when_running_command.tooltip")));
        addHorizontalOption(
            this.generalOptionList,
            closeOnCommandRunWidget,
            closeOnCommandRunLabel
        );


        final Component turnForwardPageLabel = translatableComponentForPage("ui.eg_bedrock_books.config.key.turn_forward_page");
        final KeyBox turnForwardPageWidget = new KeyBox(
            0,
            0,
            ConfigOptions.MOVE_FORWARD_PAGE_KEY.getPendingOrCurrentValue(),
            ConfigOptions.MOVE_FORWARD_PAGE_KEY::setPendingValue,
            turnForwardPageLabel
        );
        addHorizontalOption(
            this.generalOptionList,
            turnForwardPageWidget,
            turnForwardPageLabel
        );


        final Component turnBackwardPageLabel = translatableComponentForPage("ui.eg_bedrock_books.config.key.turn_backward_page");
        final KeyBox turnBackwardPageWidget = new KeyBox(
            0,
            0,
            ConfigOptions.MOVE_BACKWARD_PAGE_KEY.getPendingOrCurrentValue(),
            ConfigOptions.MOVE_BACKWARD_PAGE_KEY::setPendingValue,
            turnBackwardPageLabel
        );
        addHorizontalOption(
            this.generalOptionList,
            turnBackwardPageWidget,
            turnBackwardPageLabel
        );


        final Component vanillaScreenKeybindEnabledLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option.open_vanilla_screen_key");
        final CheckBox vanillaScreenKeyEnabledWidget = new CheckBox(
            0,
            0,
            ConfigOptions.VANILLA_BOOK_KEY_ENABLED.getPendingOrCurrentValue(),
            ConfigOptions.VANILLA_BOOK_KEY_ENABLED::setPendingValue,
            vanillaScreenKeybindEnabledLabel,
            CHECKBOX_CONFIG,
            CHECKBOX_UNCHECKED_CONFIG
        );
        final Tooltip vanillaScreenKeyTooltip = Tooltip.create(Component.translatable("ui.eg_bedrock_books.config.key.open_vanilla_screen_key.tooltip"));
        vanillaScreenKeyEnabledWidget.setTooltip(vanillaScreenKeyTooltip);
        addHorizontalOption(
            this.generalOptionList,
            vanillaScreenKeyEnabledWidget,
            vanillaScreenKeybindEnabledLabel
        );


        final Component vanillaScreenKeyLabel = translatableComponentForPage("ui.eg_bedrock_books.config.key.open_vanilla_screen_key");
        final KeyBox vanillaScreenKeyInput = new KeyBox(
            0,
            0,
            ConfigOptions.VANILLA_BOOK_KEY.getPendingOrCurrentValue(),
            ConfigOptions.VANILLA_BOOK_KEY::setPendingValue,
            vanillaScreenKeyLabel
        );
        vanillaScreenKeyInput.setTooltip(vanillaScreenKeyTooltip);
        addHorizontalOption(
            this.generalOptionList,
            vanillaScreenKeyInput,
            vanillaScreenKeyLabel
        );


        // visual
        final Component showXButtonLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option.show_x_button");
        final CheckBox showXButtonWidget = new CheckBox(
            0,
            0,
            ConfigOptions.SHOW_X_BUTTON.getPendingOrCurrentValue(),
            value -> {
                ConfigOptions.SHOW_X_BUTTON.setPendingValue(value);
                this.xButton.visible = value;
            },
            showXButtonLabel,
            CHECKBOX_CONFIG,
            CHECKBOX_UNCHECKED_CONFIG
        );
        addHorizontalOption(
            this.visualOptionList,
            showXButtonWidget,
            showXButtonLabel
        );


        final Component ribbonHeightLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option.ribbon_height");
        final IntegerSlider ribbonHeightWidget = new IntegerSlider(
            0,
            0,
            MAX_LAYOUT_WIDTH,
            16,
            ribbonHeightLabel,
            ConfigOptions.RIBBON_HEIGHT.getPendingOrCurrentValue(),
            ConfigOptions.RIBBON_HEIGHT::setPendingValue,
            0,
            130,
            SLIDER_BACKGROUND_SPRITE,
            SLIDER_HANDLE_CONFIG
        );
        addStackedOption(
            this.visualOptionList,
            ribbonHeightWidget,
            ribbonHeightLabel
        );


        // debug
        for (ConfigOption<Boolean> option : DEBUG_OPTIONS) {
            final Component optionLabel = literalComponentForPage(option.getJsonKey());
            addStackedOption(
                this.debugOptionList,
                new CheckBox(
                    0,
                    0,
                    option.getPendingOrCurrentValue(),
                    option::setPendingValue,
                    optionLabel,
                    CHECKBOX_CONFIG,
                    CHECKBOX_UNCHECKED_CONFIG
                ),
                optionLabel
            );
        }


        this.addRenderableWidget(this.generalOptionList);
        this.addRenderableWidget(this.visualOptionList);
        this.addRenderableWidget(this.debugOptionList);
    }

    protected Component translatableComponentForPage(String translationKey) {
        return Component.translatable(translationKey).withStyle(Style.EMPTY.withColor(PAGE_TEXT_COLOUR).withShadowColor(0));
    }

    protected Component literalComponentForPage(String literal) {
        return Component.literal(literal).withStyle(Style.EMPTY.withColor(PAGE_TEXT_COLOUR).withShadowColor(0));
    }

    protected ConfigList createPageLayout(PageSide side) {
        int x = side == PageSide.LEFT ? (this.width / 2) - (CENTER_PADDING / 2) - MAX_LAYOUT_WIDTH : (this.width / 2) + (CENTER_PADDING / 2) + 4;
        int y = (this.height / 2) - MAX_LAYOUT_HEIGHT + 55;
        return new ConfigList(x - ConfigList.SCROLLBAR_WIDTH / 4, y, MAX_LAYOUT_WIDTH + ConfigList.SCROLLBAR_WIDTH / 2, MAX_LAYOUT_HEIGHT - 5);
    }

    protected void addHorizontalOption(ConfigList configList, AbstractWidget widget, Component label) {
        MultiLineTextWidget labelWidget = new MultiLineTextWidget(label, Minecraft.getInstance().font);
        labelWidget.setMaxWidth(Math.abs(widget.getWidth() - MAX_LAYOUT_WIDTH) - COLUMN_GAP);
        configList.addHorizontal(widget, labelWidget);
    }
    protected void addStackedOption(ConfigList configList, AbstractWidget widget, Component label) {
        MultiLineTextWidget labelWidget = new MultiLineTextWidget(label, Minecraft.getInstance().font);
        labelWidget.setMaxWidth(MAX_LAYOUT_WIDTH - COLUMN_GAP);
        configList.addStacked(widget, labelWidget);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if(this.getCurrentLeftPageIndex() == 0) {
            guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                BedrockLecternScreen.RIGHT_RIBBON_SELECTED_SPRITE,
                this.width / 2,
                (this.height / 2) - BedrockLecternScreen.RIBBON_Y_OFFSET,
                BedrockLecternScreen.RIBBON_WIDTH,
                BedrockLecternScreen.RIBBON_TOP_HEIGHT + ConfigOptions.RIBBON_HEIGHT.getPendingOrCurrentValue() + BedrockLecternScreen.RIBBON_BOTTOM_HEIGHT
            );
        }
    }

    @Override
    protected Component getPageIndicatorMessage(int index) {
        return switch (index) {
            case 0 -> Component.translatable("ui.eg_bedrock_books.config.page.general");
            case 1 -> Component.translatable("ui.eg_bedrock_books.config.page.visual");
            case 2 -> Component.translatable("ui.eg_bedrock_books.config.page.debug");
            default -> super.getPageIndicatorMessage(index);
        };
    }

    @Override
    protected void updateVisibleContents() {
        super.updateVisibleContents();
        if(this.getCurrentLeftPageIndex() == 0) {
            this.generalOptionList.visible = true;
            this.visualOptionList.visible = true;
            this.debugOptionList.visible = false;
        } else {
            this.generalOptionList.visible = false;
            this.visualOptionList.visible = false;
            this.debugOptionList.visible = true;
        }
    }
}
