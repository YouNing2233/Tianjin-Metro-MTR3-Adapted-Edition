package ziyue.tjmetro.block;

import mtr.block.BlockAPGGlassEnd;
import net.minecraft.world.item.Item;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjinTRT;

/**
 * Tianjin TRT (Line 1) APG glass end panel (half-height).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockAPGGlassEnd
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGGlassEndTianjinTRT extends BlockAPGGlassEnd implements BlockFlagAPGTianjinTRT
{
    @Override
    protected boolean isAPG() {
        return true;
    }

    @Override
    public Item asItem() {
        return ItemList.APG_GLASS_END_TIANJIN_TRT.get();
    }
}
