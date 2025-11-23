package games.enchanted.eg_bedrock_books.common.mixin.resource;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(Options.class)
public class HighContrastOptionsMixin {
    @Definition(id = "createBoolean", method = "Lnet/minecraft/client/OptionInstance;createBoolean(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;ZLjava/util/function/Consumer;)Lnet/minecraft/client/OptionInstance;")
    @Expression("createBoolean('options.accessibility.high_contrast', ?, ?, ?)")
    @WrapOperation(
        at = @At("MIXINEXTRAS:EXPRESSION"),
        method = "<init>"
    )
    private OptionInstance<Boolean> eg_bedrock_books$composeHighContrastPackChange(String caption, OptionInstance.TooltipSupplier<Boolean> tooltip, boolean initialValue, Consumer<Boolean> onValueUpdate, Operation<OptionInstance<Boolean>> original) {
        return original.call(
            caption,
            tooltip,
            initialValue,
            (Consumer<Boolean>) value -> {
                PackRepository repo = Minecraft.getInstance().getResourcePackRepository();
                boolean isSelected = repo.getSelectedIds().contains(ModConstants.HIGH_CONTRAST_PACK_ID);
                if (!isSelected && value) {
                    repo.addPack(ModConstants.HIGH_CONTRAST_PACK_ID);
                } else if (isSelected && !value) {
                    repo.removePack(ModConstants.HIGH_CONTRAST_PACK_ID);
                }
                onValueUpdate.accept(value);
            }
        );
    }
}
