package ziyue.tjmetro.block;

import mtr.block.BlockPSDTop;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjinTRT;

/**
 * Tianjin TRT (Line 1) APG glass panel (half-height).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockAPGGlass
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @see ziyue.tjmetro.render.RenderAPGGlassTianjinTRT
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGGlassTianjinTRT extends BlockAPGGlassTianjin implements BlockFlagAPGTianjinTRT
{
    @Override
    public Item asItem() {
        return ItemList.APG_GLASS_TIANJIN_TRT.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAPGGlassTianjinTRT(pos, state);
    }

    public static class TileEntityAPGGlassTianjinTRT extends BlockPSDTop.TileEntityRouteBase
    {
        public TileEntityAPGGlassTianjinTRT(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.APG_GLASS_TIANJIN_TRT_TILE_ENTITY.get(), pos, state);
        }
    }
}
