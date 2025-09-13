package games.enchanted.eg_bedrock_books.common.screen;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.duck.BookSignScreenAdditions;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.MultilineEditBoxView;
import games.enchanted.eg_bedrock_books.common.screen.widget.text.TextAreaView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.BookSignScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;

import java.util.ListIterator;
import java.util.Optional;

public class BedrockBookEditScreen extends AbstractBedrockBookScreen<String, TextAreaView<String>> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/edit_background.png");;

    // player and item
    protected final Player owner;
    protected final ItemStack bookStack;
    protected final InteractionHand hand;
    protected final BookSignScreen bookSignScreen;

    public BedrockBookEditScreen(Player owner, ItemStack book, InteractionHand hand, WritableBookContent writableBookContent) {
        super(BOOK_EDIT_TITLE, true);

        writableBookContent.getPages(Minecraft.getInstance().isTextFilteringEnabled()).forEach(this.pages::add);
        if(this.pages.isEmpty()) {
            addPage("");
            addPage("");
        } else if (this.pages.size() == 1) {
            addPage("");
        }

        this.owner = owner;
        this.bookStack = book;
        this.hand = hand;

        this.bookSignScreen = new BookSignScreen(null, owner, hand, this.pages);
        ((BookSignScreenAdditions) this.bookSignScreen).eg_bedrock_books$setReturnScreen(this);
    }

    @Override
    protected void makeFooterButtons() {
        this.footerButtonLayout.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> {
            this.onClose();
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.addChild(Button.builder(SAVE_BUTTON_COMPONENT, button -> {
            this.saveAndClose();
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.addChild(Button.builder(SIGN_BUTTON_COMPONENT, button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(this.bookSignScreen);
        }).width(FOOTER_BUTTON_WIDTH).build());
        this.footerButtonLayout.setPosition((this.width / 2) - (FOOTER_BUTTON_WIDTH * 3 + FOOTER_BUTTON_SPACING * 2) / 2, (this.height / 2) + 90);
    }

    @Override
    protected TextViewAndWidget<String, TextAreaView<String>> createTextWidgetAndView(int x, int y, PageSide side) {
        MultiLineEditBox editBox = MultiLineEditBox.builder()
            .setTextColor(TEXT_COLOUR)
            .setCursorColor(CURSOR_COLOUR)
            .setShowDecorations(false)
            .setShowBackground(false)
            .setTextShadow(TEXT_SHADOW)
            .setX(x)
            .setY(y)
            .build(this.font, PAGE_EDIT_BOX_WIDTH, PAGE_EDIT_BOX_HEIGHT, CommonComponents.EMPTY);
        editBox.setCharacterLimit(1024);
        editBox.setLineLimit(126 / this.font.lineHeight);
        editBox.setValueListener(newValue -> setPageContent(newValue, this.getCurrentLeftPageIndex() + (side == PageSide.RIGHT ? 1 : 0)));

        return new TextViewAndWidget<>(new MultilineEditBoxView(editBox), editBox);
    }

    @Override
    protected void saveAndClose() {
        super.saveAndClose();
        savePagesToStack();
    }

    @Override
    String getEmptyPageContent() {
        return "";
    }

    protected void savePagesToStack() {
        this.removeTrailingEmptyPages();
        this.updateLocalStack();
        int slotId = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().getSelectedSlot() : Inventory.SLOT_OFFHAND;
        assert this.minecraft != null;
        ClientPacketListener clientConnection = this.minecraft.getConnection();
        if(clientConnection != null) {
            clientConnection.send(new ServerboundEditBookPacket(slotId, this.pages, Optional.empty()));
        }
    }
    private void removeTrailingEmptyPages() {
        ListIterator<String> pageIterator = this.pages.listIterator(this.pages.size());
        while (pageIterator.hasPrevious() && pageIterator.previous().isEmpty()) {
            pageIterator.remove();
        }
    }
    private void updateLocalStack() {
        this.bookStack.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(this.pages.stream().map(Filterable::passThrough).toList()));
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }
}
