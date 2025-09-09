//? if fabric {
package games.enchanted.eg_bedrock_books.fabric;

import games.enchanted.eg_bedrock_books.common.ModEntry;
import net.fabricmc.api.ModInitializer;

public class FabricCommonEntry implements ModInitializer {
    @Override
    public void onInitialize() {
        ModEntry.init();
    }
}
//?}