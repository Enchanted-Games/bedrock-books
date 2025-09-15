//? if fabric {
package games.enchanted.eg_bedrock_books.fabric;

import games.enchanted.eg_bedrock_books.common.ModEntry;
import net.fabricmc.api.ClientModInitializer;

public class FabricClientEntry implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntry.init();
    }
}
//?}
