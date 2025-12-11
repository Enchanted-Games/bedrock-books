package games.enchanted.eg_bedrock_books.platform;

import org.jetbrains.annotations.Nullable;
//? if fabric {
import games.enchanted.eg_bedrock_books.common.ModConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
//?} else {
/*import games.enchanted.eg_bedrock_books.neoforge.NeoForgeEntry;
import net.neoforged.fml.loading.FMLPaths;
*///?}

import java.nio.file.Path;
import java.util.Optional;

public class PlatformHelper {
    /**
     * Returns the path where configuration files are stored within the .minecraft directory
     */
    public static Path getConfigPath() {
        //? if fabric {
        return FabricLoader.getInstance().getConfigDir();
        //?} else {
        /*return FMLPaths.CONFIGDIR.get();
         *///?}
    }

    @SuppressWarnings("OptionalIsPresent")
    public static @Nullable Path getModJarPath() {
        //? if fabric {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID);
        if(container.isEmpty()) return null;
        return container.get().findPath("").orElse(null);
        //?} else {
        /*return NeoForgeEntry.CONTAINER.getModInfo().getOwningFile().getFile().getFilePath();
        *///?}
    }
}
