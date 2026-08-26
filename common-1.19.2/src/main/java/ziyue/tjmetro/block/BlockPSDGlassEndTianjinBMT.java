package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGGlassEndBase;
import net.minecraft.world.item.Item;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinBMT;

/**
 * Tianjin BMT-line PSD glass end panel.
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockPSDGlassEnd
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDGlassEndTianjinBMT extends BlockPSDAPGGlassEndBase implements BlockFlagPSDTianjinBMT
{
    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_END_TIANJIN_BMT.get();
    }
}
