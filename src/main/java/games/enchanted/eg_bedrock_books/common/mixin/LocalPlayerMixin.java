package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import games.enchanted.eg_bedrock_books.common.screen.BedrockBookEditScreen;
import games.enchanted.eg_bedrock_books.common.util.InputUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"),
        method = "openItemGui"
    )
    private void eg_bedrock_books$modifyBookScreen(Minecraft instance, Screen old, Operation<Void> original, ItemStack stack, InteractionHand hand, @Local WritableBookContent writableBookContent) {
        if(InputUtil.shouldOpenVanillaBook()) {
            original.call(instance, old);
            return;
        }
        original.call(instance, new BedrockBookEditScreen(this, stack, hand, writableBookContent));
    }
}
