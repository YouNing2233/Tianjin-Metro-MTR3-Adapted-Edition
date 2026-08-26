package ziyue.tjmetro.block;

import net.minecraft.world.item.Item;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinJinjing;

/**
 * Tianjin Jinjing-line PSD glass panel.
 *
 * @author ZiYueCommentary
 * @see BlockPSDGlassTianjin
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDGlassTianjinJinjing extends BlockPSDGlassTianjin implements BlockFlagPSDTianjinJinjing
{
    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_TIANJIN_JINJING.get();
    }
}
