package games.enchanted.eg_bedrock_books.platform;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.fml.loading.FMLPaths;
*///?}

import java.nio.file.Path;

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
}
