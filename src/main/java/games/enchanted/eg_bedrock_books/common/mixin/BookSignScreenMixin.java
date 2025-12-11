package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.duck.BookSignScreenAdditions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookSignScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = BookSignScreen.class, priority = 995)
public class BookSignScreenMixin extends Screen implements BookSignScreenAdditions {
    @Unique
    private static final int eg_bedrock_books$BACKGROUND_WIDTH = 256;
    @Unique
    private static final int eg_bedrock_books$BACKGROUND_HEIGHT = 256;
    @Unique
    private static final Identifier eg_bedrock_books$BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/book/sign_background.png");

    @Unique
    private static final int eg_bedrock_books$MAIN_TEXT_COLOUR = 0xfffaf3e6;
    @Unique
    private static final int eg_bedrock_books$HC_MAIN_TEXT_COLOUR = 0xffffffff;
    @Unique
    private static final int eg_bedrock_books$SECONDARY_TEXT_COLOUR = 0xfff0dbaf;
    @Unique
    private static final int eg_bedrock_books$HC_SECONDARY_TEXT_COLOUR = CommonColors.SOFT_YELLOW;

    @Unique
    private Screen eg_bedrock_books$returnScreen = null;

    protected BookSignScreenMixin(Component title) {
        super(title);
    }

    @Override
    public void eg_bedrock_books$setReturnScreen(Screen screen) {
        this.eg_bedrock_books$returnScreen = screen;
    }

    @Unique
    private int eg_bedrock_books$getTextColour() {
        return ModConstants.isHighContrastPackActive() ? eg_bedrock_books$HC_MAIN_TEXT_COLOUR : eg_bedrock_books$MAIN_TEXT_COLOUR;
    }
    @Unique
    private int eg_bedrock_books$getSecondaryTextColour() {
        return ModConstants.isHighContrastPackActive() ? eg_bedrock_books$HC_SECONDARY_TEXT_COLOUR : eg_bedrock_books$SECONDARY_TEXT_COLOUR;
    }

    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"),
        method = "method_71541"
    )
    private void eg_bedrock_books$modifyReturnScreenIfPresent(Minecraft instance, Screen old, Operation<Void> original) {
        if(this.eg_bedrock_books$returnScreen != null) {
            original.call(instance, this.eg_bedrock_books$returnScreen);
            return;
        }
        original.call(instance, old);
    }

    // buttons
    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;"),
        method = "init"
    )
    private Button.Builder eg_bedrock_books$modifyButtons(Button.Builder instance, int x, int y, int width, int height, Operation<Button.Builder> original) {
        return original.call(instance, x, (this.height / 2) + 90, width, height);
    }

    // background
    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"),
        method = "renderBackground"
    )
    private void eg_bedrock_books$modifyBackgroundImage(GuiGraphics instance, RenderPipeline pipeline, Identifier atlas, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original) {
        original.call(
            instance,
            RenderPipelines.GUI_TEXTURED,
            eg_bedrock_books$BACKGROUND_TEXTURE,
            (this.width / 2) - (eg_bedrock_books$BACKGROUND_WIDTH / 2),
            (this.height / 2) - (eg_bedrock_books$BACKGROUND_HEIGHT / 2),
            0f,
            0f,
            eg_bedrock_books$BACKGROUND_WIDTH,
            eg_bedrock_books$BACKGROUND_HEIGHT,
            eg_bedrock_books$BACKGROUND_WIDTH,
            eg_bedrock_books$BACKGROUND_HEIGHT
        );
    }

    // title label
    @WrapOperation(
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/BookSignScreen;EDIT_TITLE_LABEL:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETSTATIC)),
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V", ordinal = 0),
        method = "render"
    )
    private void eg_bedrock_books$modifyEditTitleLabel(GuiGraphics instance, Font font, Component text, int x, int y, int color, boolean drawShadow, Operation<Void> original) {
        int textWidth = font.width(text);
        original.call(
            instance,
            font,
            text,
            (this.width / 2) - textWidth / 2,
            (this.height / 2) - 86,
            eg_bedrock_books$getTextColour(),
            drawShadow
        );
    }


    // title box
    @WrapOperation(
        at = @At(value = "NEW", target = "(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/network/chat/Component;)Lnet/minecraft/client/gui/components/EditBox;"),
        method = "init"
    )
    private EditBox eg_bedrock_books$modifyInputPosition(Font font, int x, int y, int width, int height, Component message, Operation<EditBox> original) {
        return original.call(font, x, (this.height / 2) - 70, width, height, message);
    }

    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;setTextColor(I)V"),
        method = "init"
    )
    private void eg_bedrock_books$modifyInputColour(EditBox instance, int color, Operation<Void> original) {
        original.call(instance, eg_bedrock_books$getTextColour());
    }

    // owner label
    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;"),
        method = "<init>"
    )
    private MutableComponent eg_bedrock_books$modifyOwnerLabelColour(MutableComponent instance, ChatFormatting format, Operation<MutableComponent> original) {
        return original.call(instance, format).withStyle(Style.EMPTY.withColor(eg_bedrock_books$getSecondaryTextColour()));
    }

    @WrapOperation(
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/BookSignScreen;ownerText:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETFIELD)),
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V", ordinal = 0),
        method = "render"
    )
    private void eg_bedrock_books$modifyOwnerLabel(GuiGraphics instance, Font font, Component text, int x, int y, int color, boolean drawShadow, Operation<Void> original) {
        int textWidth = font.width(text);
        original.call(
            instance,
            font,
            text,
            (this.width / 2) - textWidth / 2,
            (this.height / 2) - 56,
            -1,
            drawShadow
        );
    }

    // note text
    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawWordWrap(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/FormattedText;IIIIZ)V"),
        method = "render"
    )
    private void eg_bedrock_books$modifyNoteText(GuiGraphics instance, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean drawShadow, Operation<Void> original) {
        original.call(
            instance,
            font,
            text,
            (this.width / 2) - lineWidth / 2,
            (this.height / 2) - 16,
            lineWidth,
            eg_bedrock_books$getTextColour(),
            drawShadow
        );
    }
}
