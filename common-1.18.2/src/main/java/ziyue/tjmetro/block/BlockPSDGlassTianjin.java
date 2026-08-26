package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGGlassBase;
import net.minecraft.world.item.Item;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjin;

/**
 * Tianjin PSD glass panel (default / TRT style).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockPSDGlass
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDGlassTianjin extends BlockPSDAPGGlassBase implements BlockFlagPSDTianjin
{
    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_TIANJIN.get();
    }
}
