package ziyue.tjmetro.block;

import net.minecraft.world.item.Item;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinJinjing;

/**
 * Tianjin Jinjing-line PSD glass end panel.
 *
 * @author ZiYueCommentary
 * @see BlockPSDGlassEndTianjin
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDGlassEndTianjinJinjing extends BlockPSDGlassEndTianjin implements BlockFlagPSDTianjinJinjing
{
    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_END_TIANJIN_JINJING.get();
    }
}
