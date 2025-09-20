//? if neoforge {
/*package games.enchanted.eg_bedrock_books.neoforge;

import games.enchanted.eg_bedrock_books.common.ModEntry;
import games.enchanted.eg_bedrock_books.common.screen.config.ConfigScreenBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/^*
 * This is the entry point for your mod's forge side.
 ^/
@Mod(value = "eg_bedrock_books", dist = Dist.CLIENT)
public class NeoForgeEntry {
    public NeoForgeEntry() {
        ModEntry.init();

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> ConfigScreenBehaviour.makeScreenForModMenu(parent));
    }
}
*///?}