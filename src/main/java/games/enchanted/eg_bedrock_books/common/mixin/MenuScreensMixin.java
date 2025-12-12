package games.enchanted.eg_bedrock_books.common.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import games.enchanted.eg_bedrock_books.common.screen.BedrockLecternScreen;
import games.enchanted.eg_bedrock_books.common.util.InputUtil;
import games.enchanted.eg_bedrock_books.common.util.ScreenUtil;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MenuScreens.class)
public class MenuScreensMixin {
    @WrapMethod(
        method = "register"
    )
    private static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void eg_bedrock_books$modifyLecternScreen(MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, S> screenConstructor, Operation<Void> original) {
        if(!menuType.equals(MenuType.LECTERN)) {
            original.call(menuType, screenConstructor);
            return;
        }
        original.call(menuType, (MenuScreens.ScreenConstructor<M, S>) (menu, inventory, component) -> {
            if(ScreenUtil.shouldOpenVanillaLecternScreen()) {
                return screenConstructor.create(menu, inventory, component);
            }
            return (S) new BedrockLecternScreen((LecternMenu) menu);
        });
    }
}
