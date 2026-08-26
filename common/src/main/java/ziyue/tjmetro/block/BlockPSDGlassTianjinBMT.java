package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGGlassBase;
import net.minecraft.world.item.Item;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinBMT;

/**
 * Tianjin BMT-line PSD glass panel.
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockPSDGlass
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDGlassTianjinBMT extends BlockPSDAPGGlassBase implements BlockFlagPSDTianjinBMT
{
    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_TIANJIN_BMT.get();
    }
}
