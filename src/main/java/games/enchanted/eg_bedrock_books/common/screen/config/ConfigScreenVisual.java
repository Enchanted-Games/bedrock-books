package games.enchanted.eg_bedrock_books.common.screen.config;

import games.enchanted.eg_bedrock_books.common.config.ConfigOptions;
import games.enchanted.eg_bedrock_books.common.screen.widget.config.KeyBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class ConfigScreenVisual extends ConfigScreenBehaviour {
    protected static final int MAX_LAYOUT_WIDTH = 130;
    protected static final int MAX_LAYOUT_HEIGHT = 140;
    protected static final int CENTER_PADDING = 14;
    protected static final int COLUMN_GAP = 4;
    protected static final int ROW_GAP = 4;

    protected static final int PAGE_TEXT_COLOUR = 0xff987457;

    protected ConfigScreenVisual(@Nullable Screen returnScreen, boolean alwaysBlurBackground) {
        super(returnScreen, alwaysBlurBackground);
    }

    protected ConfigScreenVisual(@Nullable Screen returnScreen) {
        super(returnScreen, false);
    }

    protected GridLayout generalGridLayout;

    @Override
    protected void addWidgetsBetweenPages() {
        super.addWidgetsBetweenPages();

        this.generalGridLayout = createPageLayout(PageSide.LEFT);

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
            0,
            1
        );
        addOptionToLayout(
            new KeyBox(
                0,
                0,
                ConfigOptions.VANILLA_BOOK_KEY.getValue(),
                ConfigOptions.VANILLA_BOOK_KEY::setPendingValue,
                Component.translatable("ui.eg_bedrock_books.config.key.open_vanilla_screen_key")
            ),
            Component.translatable("ui.eg_bedrock_books.config.key.open_vanilla_screen_key"),
            this.generalGridLayout,
            1,
            1
        );

        this.generalGridLayout.arrangeElements();
    }

    protected Component translatableComponentForPage(String translationKey) {
        return Component.translatable(translationKey).withStyle(Style.EMPTY.withColor(PAGE_TEXT_COLOUR).withShadowColor(0));
    }

    protected GridLayout createPageLayout(PageSide side) {
        GridLayout layout = new GridLayout(
            side == PageSide.LEFT ? (this.width / 2) - (CENTER_PADDING / 2) - MAX_LAYOUT_WIDTH : (this.width / 2) + (CENTER_PADDING / 2),
            (this.height / 2) - MAX_LAYOUT_HEIGHT + 55
        );
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
}
