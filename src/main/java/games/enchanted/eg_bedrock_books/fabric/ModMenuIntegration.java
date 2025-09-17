//? if fabric {
package games.enchanted.eg_bedrock_books.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import games.enchanted.eg_bedrock_books.common.screen.config.ConfigScreenBehaviour;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreenBehaviour::makeScreenForModMenu;
    }
}
//?}