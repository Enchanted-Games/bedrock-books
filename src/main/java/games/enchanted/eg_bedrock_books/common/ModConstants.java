package games.enchanted.eg_bedrock_books.common;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.NotNull;

public class ModConstants {
    public static final String MOD_NAME = "Bedrock Books";
    public static final String MOD_ID = "eg_bedrock_books";

    public static final String HIGH_CONTRAST_PACK_ID = MOD_ID + ":" + "high_contrast";

    //? if fabric {
    public static final String TARGET_PLATFORM = "fabric";
    //?}
    //? if neoforge {
    /*public static final String TARGET_PLATFORM = "neoforge";
    *///?}

    public static boolean isHighContrastPackActive() {
        return Minecraft.getInstance().getResourcePackRepository().getSelectedIds().contains(HIGH_CONTRAST_PACK_ID);
    }

    public static final PackSource BEDROCK_BOOKS_SOURCE = new PackSource() {
        @Override
        public @NotNull Component decorate(Component name) {
            return name.copy().append(Component.translatable("pack.source.eg_bedrock_books", " (BB)"));
        }

        @Override
        public boolean shouldAddAutomatically() {
            return false;
        }
    };
}
