package games.enchanted.eg_bedrock_books.common.screen.config;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import games.enchanted.eg_bedrock_books.common.config.option.ConfigOption;
import games.enchanted.eg_bedrock_books.common.screen.AbstractBedrockBookScreen;
import games.enchanted.eg_bedrock_books.common.screen.BedrockBookViewScreen;
import games.enchanted.eg_bedrock_books.common.screen.BedrockLecternScreen;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
import games.enchanted.eg_bedrock_books.common.screen.widget.ScreenCloseOverride;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.CheckBox;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.IntegerSlider;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.KeyBox;
import games.enchanted.eg_bedrock_books.common.screen.widget.scroll.ConfigList;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.DummyTextAreaView;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.TextAreaView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigScreen extends AbstractBedrockBookScreen<String, TextAreaView<String>> {
    protected static final int MAX_LAYOUT_WIDTH = 120;
    protected static final int MAX_LAYOUT_HEIGHT = 140;
    protected static final int CENTER_PADDING = 24;
    protected static final int COLUMN_GAP = 8;

    protected static final int PAGE_TEXT_COLOUR = 0xff987457;
    protected static final int HC_PAGE_TEXT_COLOUR = 0xffffffff;

    protected static final Component CONFIG_TITLE = Component.translatable("ui.eg_bedrock_books.config.title");
    protected static final Component RESET_BUTTON_COMPONENT = Component.translatable("ui.eg_bedrock_books.config.reset");
    protected static final Component RESET_TITLE_COMPONENT = Component.translatable("ui.eg_bedrock_books.config.reset.title").withStyle(Style.EMPTY.withBold(true));
    protected static final Component RESET_MESSAGE_COMPONENT = Component.translatable("ui.eg_bedrock_books.config.reset.warning");
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/config_background.png");

    public static final CustomSpriteButton.ButtonConfig CHECKBOX_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_unchecked"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_unchecked_hover"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_unchecked_focus")
    );
    public static final CustomSpriteButton.ButtonConfig CHECKBOX_UNCHECKED_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_checked"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_checked_hover"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/checkbox_checked_focus")
    );

    public static Identifier SLIDER_BACKGROUND_SPRITE = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/slider_background");
    public static final CustomSpriteButton.ButtonConfig SLIDER_HANDLE_CONFIG = new CustomSpriteButton.ButtonConfig(
        () -> SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/slider_handle"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/slider_handle_hover"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "config/slider_handle_focus")
    );

    protected static final List<ConfigOption<Boolean>> SCREEN_PREFERENCES = List.of(
        ConfigOptions.PREFER_VANILLA_EDIT_SCREEN,
        ConfigOptions.PREFER_VANILLA_WRITTEN_SCREEN,
        ConfigOptions.PREFER_VANILLA_LECTERN_SCREEN,
        ConfigOptions.PREFER_VANILLA_SIGN_SCREEN
    );

    protected static final List<ConfigOption<Boolean>> DEBUG_OPTIONS = List.of(
        ConfigOptions.DEBUG_WIDGET_BOUNDS,
        ConfigOptions.DEBUG_TEXT_BOUNDS,
        ConfigOptions.DEBUG_CONTAINER_DATA,
        ConfigOptions.DEBUG_VARIABLES
    );

    protected final @Nullable Screen returnScreen;
    protected final boolean alwaysBlurBackground;
    protected boolean saveWhenOnCloseCalled = false;

    protected ConfigScreen(@Nullable Screen returnScreen, boolean alwaysBlurBackground) {
        super(CONFIG_TITLE, false);
        this.returnScreen = returnScreen;
        this.alwaysBlurBackground = alwaysBlurBackground;

        this.pages = List.of(getEmptyPageContent(), getEmptyPageContent(), getEmptyPageContent(), getEmptyPageContent());
    }

    protected ConfigScreen(@Nullable Screen returnScreen) {
        this(returnScreen, false);
    }

    protected ConfigList generalOptionList;
    protected ConfigList visualOptionList;
    protected ConfigList screenPreferencesOptionList;
    protected ConfigList debugOptionList;

    protected List<ConfigList> getConfigLists() {
        return List.of(this.generalOptionList, this.visualOptionList, this.screenPreferencesOptionList, this.debugOptionList);
    }

    @Override
    protected void addWidgetsBetweenPages() {
        super.addWidgetsBetweenPages();

        this.generalOptionList = createPageLayout(PageSide.LEFT);
        this.visualOptionList = createPageLayout(PageSide.RIGHT);
        this.screenPreferencesOptionList = createPageLayout(PageSide.LEFT);
        this.debugOptionList = createPageLayout(PageSide.RIGHT);

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
        addStackedOption(
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
        addStackedOption(
            this.generalOptionList,
            turnBackwardPageWidget,
            turnBackwardPageLabel
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
            MAX_LAYOUT_WIDTH - (ConfigList.SCROLLBAR_WIDTH / 2),
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

        final Component improveTextContrastInHCLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option.improve_text_contrast_in_hc");
        final Tooltip improveTextContrastInHCLabelTooltip = Tooltip.create(Component.translatable("ui.eg_bedrock_books.config.option.improve_text_contrast_in_hc.tooltip"));
        final CheckBox improveTextContrastInHCWidget = new CheckBox(
            0,
            0,
            ConfigOptions.IMPROVE_TEXT_CONTRAST_IN_HC.getPendingOrCurrentValue(),
            ConfigOptions.IMPROVE_TEXT_CONTRAST_IN_HC::setPendingValue,
            improveTextContrastInHCLabel,
            CHECKBOX_CONFIG,
            CHECKBOX_UNCHECKED_CONFIG
        );
        improveTextContrastInHCWidget.setTooltip(improveTextContrastInHCLabelTooltip);
        addHorizontalOption(
            this.visualOptionList,
            improveTextContrastInHCWidget,
            improveTextContrastInHCLabel
        );

        final Component autoEnableHCPackLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option.auto_enable_bedrock_books_hc_pack");
        final Tooltip autoEnableHCPackTooltip = Tooltip.create(Component.translatable("ui.eg_bedrock_books.config.option.auto_enable_bedrock_books_hc_pack.tooltip"));
        final CheckBox autoEnableHCPackWidget = new CheckBox(
            0,
            0,
            ConfigOptions.AUTO_ENABLE_BEDROCK_BOOKS_HC_PACK.getPendingOrCurrentValue(),
            ConfigOptions.AUTO_ENABLE_BEDROCK_BOOKS_HC_PACK::setPendingValue,
            autoEnableHCPackLabel,
            CHECKBOX_CONFIG,
            CHECKBOX_UNCHECKED_CONFIG
        );
        autoEnableHCPackWidget.setTooltip(autoEnableHCPackTooltip);
        addHorizontalOption(
            this.visualOptionList,
            autoEnableHCPackWidget,
            autoEnableHCPackLabel
        );


        // screen preferences
        final Component screenToggleKeyLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option.screen_toggle_key");
        final Component screenToggleKeyTooltip = Component.translatable("ui.eg_bedrock_books.config.option.screen_toggle_key.tooltip");
        final KeyBox screenToggleKeyInput = new KeyBox(
            0,
            0,
            ConfigOptions.INVERSE_SCREEN_PREFERENCE_KEY.getPendingOrCurrentValue(),
            ConfigOptions.INVERSE_SCREEN_PREFERENCE_KEY::setPendingValue,
            screenToggleKeyLabel
        );
        screenToggleKeyInput.setTooltip(Tooltip.create(screenToggleKeyTooltip));
        addStackedOption(
            this.screenPreferencesOptionList,
            screenToggleKeyInput,
            screenToggleKeyLabel,
            screenToggleKeyTooltip
        );

        for (ConfigOption<Boolean> option : SCREEN_PREFERENCES) {
            final Component optionLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option." + option.getJsonKey());
            addHorizontalOption(
                this.screenPreferencesOptionList,
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


        // debug
        for (ConfigOption<Boolean> option : DEBUG_OPTIONS) {
            final Component optionLabel = literalComponentForPage(option.getJsonKey());
            addHorizontalOption(
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
        this.addRenderableWidget(this.screenPreferencesOptionList);
        this.addRenderableWidget(this.debugOptionList);
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

    protected Component translatableComponentForPage(String translationKey) {
        return Component.translatable(translationKey).withStyle(Style.EMPTY.withColor(getPageTextColour()).withShadowColor(0));
    }

    protected Component literalComponentForPage(String literal) {
        return Component.literal(literal).withStyle(Style.EMPTY.withColor(getPageTextColour()).withShadowColor(0));
    }

    protected ConfigList createPageLayout(PageSide side) {
        int x = side == PageSide.LEFT ? (this.width / 2) - (CENTER_PADDING / 2) - MAX_LAYOUT_WIDTH : (this.width / 2) + (CENTER_PADDING / 2) + 4;
        int y = (this.height / 2) - MAX_LAYOUT_HEIGHT + 55;
        return new ConfigList(x - ConfigList.SCROLLBAR_WIDTH / 4, y, MAX_LAYOUT_WIDTH + ConfigList.SCROLLBAR_WIDTH / 2, MAX_LAYOUT_HEIGHT - 5);
    }

    protected void addHorizontalOption(ConfigList configList, AbstractWidget widget, Component label) {
        addHorizontalOption(configList, widget, label, CommonComponents.EMPTY);
    }
    protected void addHorizontalOption(ConfigList configList, AbstractWidget widget, Component label, Component tooltip) {
        MultiLineTextWidget labelWidget = new MultiLineTextWidget(label, Minecraft.getInstance().font);
        labelWidget.setMaxWidth(Math.abs(widget.getWidth() - MAX_LAYOUT_WIDTH) - COLUMN_GAP);
        labelWidget.setTooltip(Tooltip.create(tooltip));
        configList.addHorizontal(widget, labelWidget);
    }

    protected void addStackedOption(ConfigList configList, AbstractWidget widget, Component label) {
        addStackedOption(configList, widget, label, CommonComponents.EMPTY);
    }
    protected void addStackedOption(ConfigList configList, AbstractWidget widget, Component label, Component tooltip) {
        MultiLineTextWidget labelWidget = new MultiLineTextWidget(label, Minecraft.getInstance().font);
        labelWidget.setMaxWidth(MAX_LAYOUT_WIDTH - COLUMN_GAP);
        labelWidget.setTooltip(Tooltip.create(tooltip));
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
            case 2 -> Component.translatable("ui.eg_bedrock_books.config.page.screen_preferences");
            case 3 -> Component.translatable("ui.eg_bedrock_books.config.page.debug");
            default -> super.getPageIndicatorMessage(index);
        };
    }

    @Override
    protected void updateVisibleContents() {
        super.updateVisibleContents();
        if(this.getCurrentLeftPageIndex() == 0) {
            this.generalOptionList.visible = true;
            this.visualOptionList.visible = true;
            this.screenPreferencesOptionList.visible = false;
            this.debugOptionList.visible = false;
        } else {
            this.generalOptionList.visible = false;
            this.visualOptionList.visible = false;
            this.screenPreferencesOptionList.visible = true;
            this.debugOptionList.visible = true;
        }
    }

    protected int getPageTextColour() {
        return ModConstants.isHighContrastPackActive() ? HC_PAGE_TEXT_COLOUR : PAGE_TEXT_COLOUR;
    }


    public boolean isChildPreventingInputs() {
        var magicLambdaThing = new Object() {
            boolean preventingInput = false;
        };
        this.getConfigLists().forEach(configList -> configList.visitChildren(widget -> {
            if(widget instanceof ScreenCloseOverride screenCloseOverride && screenCloseOverride.preventScreenClose()) {
                magicLambdaThing.preventingInput = true;
            }
        }));
        return magicLambdaThing.preventingInput;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !this.isChildPreventingInputs();
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
            Minecraft.getInstance().setScreen(new ConfigScreen(this.returnScreen, this.alwaysBlurBackground));
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
    protected Identifier getBackgroundTexture() {
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
        return new ConfigScreen(returnScreen, true);
    }

    public static Screen makeScreen(@Nullable Screen returnScreen) {
        return new ConfigScreen(returnScreen);
    }

    public static void openConfigScreen(@Nullable Screen returnScreen) {
        Minecraft.getInstance().setScreen(makeScreen(returnScreen));
    }

    @Override
    protected boolean canTurnForwardPage() {
        if(this.isChildPreventingInputs()) return false;
        return super.canTurnForwardPage();
    }

    @Override
    protected boolean canTurnBackPage() {
        if(this.isChildPreventingInputs()) return false;
        return super.canTurnBackPage();
    }
}
