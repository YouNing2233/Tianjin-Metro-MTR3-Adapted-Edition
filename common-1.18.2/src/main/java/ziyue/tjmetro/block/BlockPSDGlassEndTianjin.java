package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGGlassEndBase;
import net.minecraft.world.item.Item;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjin;

/**
 * Tianjin PSD glass end panel (default / TRT style).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockPSDGlassEnd
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDGlassEndTianjin extends BlockPSDAPGGlassEndBase implements BlockFlagPSDTianjin
{
    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_END_TIANJIN.get();
    }
}
