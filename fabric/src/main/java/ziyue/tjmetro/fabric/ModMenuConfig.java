package ziyue.tjmetro.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import ziyue.tjmetro.client.ConfigScreenOpener;

/**
 * Mod Menu integration.
 * Ported from MTR4 edition (ModMenuConfig).
 *
 * @since 1.1.2.1
 */

public class ModMenuConfig implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreenOpener::getConfigScreen;
    }
}