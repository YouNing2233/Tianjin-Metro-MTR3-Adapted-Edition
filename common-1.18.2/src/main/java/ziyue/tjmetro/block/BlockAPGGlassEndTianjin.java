package ziyue.tjmetro.block;

import mtr.block.BlockAPGGlassEnd;
import net.minecraft.world.item.Item;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjin;

/**
 * Tianjin APG glass end panel (half-height, default / TRT style).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockAPGGlassEnd
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGGlassEndTianjin extends BlockAPGGlassEnd implements BlockFlagAPGTianjin
{
    @Override
    protected boolean isAPG() {
        return true;
    }

    @Override
    public Item asItem() {
        return ItemList.APG_GLASS_END_TIANJIN.get();
    }
}
