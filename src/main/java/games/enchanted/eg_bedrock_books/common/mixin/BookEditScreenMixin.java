package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.screen.BedrockBookEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookSignScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin extends Screen {
    @Shadow @Final private Player owner;
    @Shadow @Final private ItemStack book;
    @Shadow @Final private InteractionHand hand;

    protected BookEditScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private WritableBookContent bookContent;

    @Inject(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;<init>(Lnet/minecraft/network/chat/Component;)V", shift = At.Shift.AFTER),
        method = "<init>"
    )
    private void eg_bedrock_books$storeWritableBookContent(Player owner, ItemStack book, InteractionHand hand, WritableBookContent content, CallbackInfo ci) {
        this.bookContent = content;
    }

    @Inject(
        at = @At("HEAD"),
        method = "init"
    )
    private void eg_bedrock_books$replaceBookEditScreen(CallbackInfo ci) {
        assert this.minecraft != null;
        if(!(this.minecraft.screen instanceof BedrockBookEditScreen)) {
            this.minecraft.setScreen(new BedrockBookEditScreen(this.owner, this.book, this.hand, this.bookContent));
        }
    }

//    @Shadow @Final private List<String> pages;
//    @Shadow private int currentPage;
//    @Shadow private MultiLineEditBox page;
//    @Shadow protected abstract void updatePageContent();
//    @Shadow private Component numberOfPages;
//    @Shadow private PageButton backButton;
//    @Shadow private PageButton forwardButton;
//    @Shadow protected abstract Component getPageNumberMessage();
//    @Shadow protected abstract void pageBack();
//    @Shadow protected abstract void pageForward();
//    @Shadow @Final private BookSignScreen signScreen;
//    @Shadow protected abstract void saveChanges();
//    @Shadow protected abstract void updateButtonVisibility();
//
//    protected BookEditScreenMixin(Component title) {
//        super(title);
//    }
//
//    @Unique
//    private MultiLineEditBox eg_bedrock_books$secondPage;
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public void init() {
//        int i = (this.width - 192) / 2;
//        int j = 2;
//        int k = 8;
//        this.page = MultiLineEditBox.builder().setShowDecorations(false).setTextColor(-16777216).setCursorColor(-16777216).setShowBackground(true).setTextShadow(false).setX(this.width / 2).setY(28).build(this.font, 122, 134, CommonComponents.EMPTY);
//        this.page.setCharacterLimit(1024);
//        Objects.requireNonNull(this.font);
//        page.setLineLimit(126 / 9);
//        this.page.setValueListener((string) -> this.pages.set(this.currentPage, string));
//        this.addRenderableWidget(this.page);
//
//        this.eg_bedrock_books$secondPage = MultiLineEditBox.builder().setShowDecorations(false).setTextColor(-16777216).setCursorColor(-16777216).setShowBackground(true).setTextShadow(true).setX((this.width / 2) - 128).setY(28).build(this.font, 122, 134, CommonComponents.EMPTY);
//        this.eg_bedrock_books$secondPage.setCharacterLimit(1024);
//        Objects.requireNonNull(this.font);
//        eg_bedrock_books$secondPage.setLineLimit(126 / 9);
//        this.eg_bedrock_books$secondPage.setValueListener((string) -> this.pages.set(this.currentPage, string));
//        this.addRenderableWidget(this.eg_bedrock_books$secondPage);
//
//        this.updatePageContent();
//        this.numberOfPages = this.getPageNumberMessage();
//        this.backButton = this.addRenderableWidget(new PageButton(i + 43, 159, false, (button) -> this.pageBack(), true));
//        this.forwardButton = this.addRenderableWidget(new PageButton(i + 116, 159, true, (button) -> this.pageForward(), true));
//        this.addRenderableWidget(Button.builder(Component.translatable("book.signButton"), (button) -> this.minecraft.setScreen(this.signScreen)).bounds(this.width / 2 - 100, 196, 98, 20).build());
//        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
//            this.minecraft.setScreen(null);
//            this.saveChanges();
//        }).bounds(this.width / 2 + 2, 196, 98, 20).build());
//        this.updateButtonVisibility();
//    }
//
//    @WrapOperation(
//        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"),
//        method = "renderBackground"
//    )
//    private void eg_bedrock_books$modifyBackgroundImage(GuiGraphics instance, RenderPipeline pipeline, ResourceLocation atlas, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original) {
//        original.call(instance, pipeline, ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/book_background.png"), (this.width / 2) - 256, (this.height / 2) - 128, 0f, 0f, 512, 256, 512, 256);
//    }
}
