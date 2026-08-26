package ziyue.tjmetro.block;

import mtr.block.BlockAPGGlass;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjin;

/**
 * Tianjin APG glass panel (half-height, default / TRT style).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockAPGGlass
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGGlassTianjin extends BlockAPGGlass implements BlockFlagAPGTianjin
{
    @Override
    protected boolean isAPG() {
        return true;
    }

    @Override
    public Item asItem() {
        return ItemList.APG_GLASS_TIANJIN.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
