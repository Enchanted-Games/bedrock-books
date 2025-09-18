package games.enchanted.eg_bedrock_books.common.screen.config;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import games.enchanted.eg_bedrock_books.common.config.option.ConfigOption;
import games.enchanted.eg_bedrock_books.common.screen.widget.CustomSpriteButton;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.CheckBox;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.KeyBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigScreenVisual extends ConfigScreenBehaviour {
    protected static final int MAX_LAYOUT_WIDTH = 126;
    protected static final int MAX_LAYOUT_HEIGHT = 140;
    protected static final int CENTER_PADDING = 18;
    protected static final int COLUMN_GAP = 4;
    protected static final int ROW_GAP = 5;

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

    protected GridLayout generalGridLayout;
    protected GridLayout debugGridLayout;

    @Override
    protected void addWidgetsBetweenPages() {
        super.addWidgetsBetweenPages();

        this.generalGridLayout = createPageLayout(PageSide.LEFT);
        this.debugGridLayout = createPageLayout(PageSide.LEFT);

        final Component closeOnCommandRun = translatableComponentForPage("ui.eg_bedrock_books.config.option.close_when_running_command");
        addOptionToLayout(
            new CheckBox(
                0,
                0,
                ConfigOptions.CLOSE_BOOK_WHEN_RUNNING_COMMAND.getValue(),
                ConfigOptions.CLOSE_BOOK_WHEN_RUNNING_COMMAND::setPendingValue,
                closeOnCommandRun,
                CHECKBOX_CONFIG,
                CHECKBOX_UNCHECKED_CONFIG
            ),
            closeOnCommandRun,
            this.generalGridLayout,
            0,
            1
        );

        final Component vanillaScreenKeybindEnabledLabel = translatableComponentForPage("ui.eg_bedrock_books.config.option.open_vanilla_screen_key");
        addOptionToLayout(
            new CheckBox(
                0,
                0,
                ConfigOptions.VANILLA_BOOK_KEY_ENABLED.getValue(),
                ConfigOptions.VANILLA_BOOK_KEY_ENABLED::setPendingValue,
                vanillaScreenKeybindEnabledLabel,
                CHECKBOX_CONFIG,
                CHECKBOX_UNCHECKED_CONFIG
            ),
            vanillaScreenKeybindEnabledLabel,
            this.generalGridLayout,
            1,
            1
        );

        final Component vanillaScreenLabel = translatableComponentForPage("ui.eg_bedrock_books.config.key.open_vanilla_screen_key");
        addOptionToLayout(
            new KeyBox(
                0,
                0,
                ConfigOptions.VANILLA_BOOK_KEY.getValue(),
                ConfigOptions.VANILLA_BOOK_KEY::setPendingValue,
                vanillaScreenLabel
            ),
            vanillaScreenLabel,
            this.generalGridLayout,
            2,
            1
        );

        for (int i = 0; i < DEBUG_OPTIONS.size(); i++) {
            ConfigOption<Boolean> option = DEBUG_OPTIONS.get(i);
            final Component optionLabel = literalComponentForPage(option.getJsonKey());
            addOptionToLayout(
                new CheckBox(
                    0,
                    0,
                    option.getValue(),
                    option::setPendingValue,
                    optionLabel,
                    CHECKBOX_CONFIG,
                    CHECKBOX_UNCHECKED_CONFIG
                ),
                optionLabel,
                this.debugGridLayout,
                i,
                1
            );
        }

        this.generalGridLayout.arrangeElements();
        this.debugGridLayout.arrangeElements();
    }

    protected Component translatableComponentForPage(String translationKey) {
        return Component.translatable(translationKey).withStyle(Style.EMPTY.withColor(PAGE_TEXT_COLOUR).withShadowColor(0));
    }

    protected Component literalComponentForPage(String literal) {
        return Component.literal(literal).withStyle(Style.EMPTY.withColor(PAGE_TEXT_COLOUR).withShadowColor(0));
    }

    protected GridLayout createPageLayout(PageSide side) {
        int x = side == PageSide.LEFT ? (this.width / 2) - (CENTER_PADDING / 2) - MAX_LAYOUT_WIDTH : (this.width / 2) + (CENTER_PADDING / 2);
        int y = (this.height / 2) - MAX_LAYOUT_HEIGHT + 55;

        GridLayout layout = new GridLayout(x, y);
        layout.columnSpacing(COLUMN_GAP);
        layout.defaultCellSetting().alignVerticallyTop();

        return layout;
    }

    protected void addOptionToLayout(AbstractWidget widget, Component label, GridLayout layout, int rowIndex, int occupiedRows) {
        MultiLineTextWidget textWidget = new MultiLineTextWidget(label, Minecraft.getInstance().font);
        textWidget.setMaxWidth(Math.abs(widget.getWidth() - MAX_LAYOUT_WIDTH) - COLUMN_GAP);

        FrameLayout frameLayout = new FrameLayout(
            MAX_LAYOUT_WIDTH,
            Math.max(textWidget.getHeight(), widget.getHeight()) + ROW_GAP
        );
        frameLayout.setMinWidth(MAX_LAYOUT_WIDTH);
        frameLayout.defaultChildLayoutSetting().alignVerticallyMiddle();
        frameLayout.setPosition(layout.getX(), layout.getY());

        frameLayout.addChild(textWidget, LayoutSettings::alignHorizontallyLeft);
        frameLayout.addChild(widget, LayoutSettings::alignHorizontallyRight);

        layout.addChild(
            frameLayout,
            rowIndex,
            0,
            occupiedRows,
            1,
            LayoutSettings::alignHorizontallyLeft
        );

        this.addRenderableWidget(textWidget);
        this.addRenderableWidget(widget);
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
            this.generalGridLayout.visitWidgets(widget -> widget.visible = true);
            this.debugGridLayout.visitWidgets(widget -> widget.visible = false);
        } else {
            this.generalGridLayout.visitWidgets(widget -> widget.visible = false);
            this.debugGridLayout.visitWidgets(widget -> widget.visible = true);
        }
    }
}
