package ziyue.tjmetro;

import mtr.RegistryObject;
import mtr.item.ItemWithCreativeTabBase;
import net.minecraft.world.item.Item;
import ziyue.tjmetro.item.ItemPSDAPGTianjinBase;

/**
 * @since beta-1
 */

public interface ItemList
{
    RegistryObject<Item> WRENCH = new RegistryObject<>(() -> new ItemWithCreativeTabBase(TianjinMetro.CREATIVE_MODE_TAB, properties -> properties.stacksTo(1)));

    /* 1.1.6 - Ported from 4.0 - APG (half-height platform doors) */
    RegistryObject<Item> APG_DOOR_TIANJIN = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_TIANJIN));
    RegistryObject<Item> APG_DOOR_TIANJIN_TRT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_TIANJIN_TRT));
    RegistryObject<Item> APG_DOOR_TIANJIN_BMT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_TIANJIN_BMT));
    RegistryObject<Item> APG_DOOR_TIANJIN_JINJING = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_TIANJIN_JINJING));
    RegistryObject<Item> APG_DOOR_SINGLE_LEFT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_SINGLE_LEFT));
    RegistryObject<Item> APG_DOOR_SINGLE_RIGHT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_SINGLE_RIGHT));
    RegistryObject<Item> APG_GLASS_TIANJIN = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_TIANJIN));
    RegistryObject<Item> APG_GLASS_TIANJIN_TRT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_TIANJIN_TRT));
    RegistryObject<Item> APG_GLASS_TIANJIN_BMT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_TIANJIN_BMT));
    RegistryObject<Item> APG_GLASS_TIANJIN_JINJING = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_TIANJIN_JINJING));
    RegistryObject<Item> APG_GLASS_END_TIANJIN = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_END_TIANJIN));
    RegistryObject<Item> APG_GLASS_END_TIANJIN_TRT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_END_TIANJIN_TRT));
    RegistryObject<Item> APG_GLASS_END_TIANJIN_BMT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_END_TIANJIN_BMT));
    RegistryObject<Item> APG_GLASS_END_TIANJIN_JINJING = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_END_TIANJIN_JINJING));

    /* 1.1.6 - Ported from 4.0 - PSD (full-height platform doors) */
    RegistryObject<Item> PSD_DOOR_TIANJIN = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_TIANJIN));
    RegistryObject<Item> PSD_DOOR_TIANJIN_BMT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_TIANJIN_BMT));
    RegistryObject<Item> PSD_DOOR_TIANJIN_JINJING = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_TIANJIN_JINJING));
    RegistryObject<Item> PSD_DOOR_SINGLE_LEFT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_SINGLE_LEFT));
    RegistryObject<Item> PSD_DOOR_SINGLE_RIGHT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_SINGLE_RIGHT));
    RegistryObject<Item> PSD_GLASS_TIANJIN = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_TIANJIN));
    RegistryObject<Item> PSD_GLASS_SCREEN_TIANJIN = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_SCREEN_TIANJIN));
    RegistryObject<Item> PSD_GLASS_TIANJIN_BMT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_TIANJIN_BMT));
    RegistryObject<Item> PSD_GLASS_TIANJIN_JINJING = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_TIANJIN_JINJING));
    RegistryObject<Item> PSD_GLASS_END_TIANJIN = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_END_TIANJIN));
    RegistryObject<Item> PSD_GLASS_END_TIANJIN_BMT = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_END_TIANJIN_BMT));
    RegistryObject<Item> PSD_GLASS_END_TIANJIN_JINJING = new RegistryObject<>(() -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_END_TIANJIN_JINJING));
}
