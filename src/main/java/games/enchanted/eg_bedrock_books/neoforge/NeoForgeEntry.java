//? if neoforge {
/*package games.enchanted.eg_bedrock_books.neoforge;

import games.enchanted.eg_bedrock_books.common.ModConstants;
import games.enchanted.eg_bedrock_books.common.ModEntry;
import games.enchanted.eg_bedrock_books.common.screen.config.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/^*
 * This is the entry point for your mod's forge side.
 ^/
@Mod(value = ModConstants.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeEntry {
    public static ModContainer CONTAINER = null;

    public NeoForgeEntry(ModContainer modContainer) {
        ModEntry.init();
        CONTAINER = modContainer;

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> ConfigScreen.makeScreenForModMenu(parent));
    }
}
*///?}