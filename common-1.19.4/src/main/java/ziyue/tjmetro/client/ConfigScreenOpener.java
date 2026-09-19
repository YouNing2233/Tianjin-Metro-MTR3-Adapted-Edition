package ziyue.tjmetro.client;

import net.minecraft.client.gui.screens.Screen;
import ziyue.tjmetro.Registry;

/**
 * Opens the mod config screen, falling back to a notice when Cloth Config API is missing.
 * Ported from MTR4 edition (MissingClothConfigScreen / ModMenuConfig).
 *
 * @since 1.1.2.1
 */

public class ConfigScreenOpener
{
    public static Screen getConfigScreen(Screen parent) {
        if (Registry.isClothConfigLoaded()) {
            return ClothConfigScreenHolder.create(parent);
        }
        return new MissingClothScreen(parent);
    }

    /**
     * Kept in a separate class so that Cloth Config classes are only loaded when it is installed.
     */
    private static class ClothConfigScreenHolder
    {
        private static Screen create(Screen parent) {
            return ConfigScreen.getConfigScreen(parent);
        }
    }
}