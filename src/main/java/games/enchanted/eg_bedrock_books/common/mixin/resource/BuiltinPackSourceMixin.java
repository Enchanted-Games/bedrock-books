package games.enchanted.eg_bedrock_books.common.mixin.resource;

import games.enchanted.eg_bedrock_books.common.Logging;
import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.platform.PlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(BuiltInPackSource.class)
public class BuiltinPackSourceMixin {
    @Inject(
        at = @At("HEAD"),
        method = "listBundledPacks"
    )
    private void eg_bedrock_books$addBuiltinPacks(Consumer<Pack> packConsumer, CallbackInfo ci) {
        Path jarPath = PlatformHelper.getModJarPath();
        if(jarPath == null) {
            Logging.error("Could not find resourcepacks from mod jar!");
            return;
        }
        Path resourcepacksFolder = jarPath.resolve("resourcepacks");

        try {
            FolderRepositorySource.discoverPacks(
                resourcepacksFolder,
                Minecraft.getInstance().directoryValidator(),
                (packPath, resourceSupplier) -> {
                    String packId = packPath.getFileName().toString();
                    packConsumer.accept(Pack.readMetaAndCreate(
                        new PackLocationInfo("eg_bedrock_books:" + packId, Component.translatable("pack.eg_bedrock_books." + packId + ".title"), ModConstants.BEDROCK_BOOKS_SOURCE, Optional.empty()),
                        resourceSupplier,
                        PackType.CLIENT_RESOURCES,
                        new PackSelectionConfig(false, Pack.Position.TOP, false)
                    ));
                }
            );
        } catch (IOException e) {
            Logging.error("IOException occurred trying to load mod resourcepacks: \n {}", e);
        }
    }
}
