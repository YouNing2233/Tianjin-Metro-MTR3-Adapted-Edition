package ziyue.tjmetro.block;

import mtr.block.BlockPSDTop;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjinJinjing;

/**
 * Tianjin Jinjing-line APG glass panel (half-height).
 *
 * @author ZiYueCommentary
 * @see BlockAPGGlassTianjin
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @see ziyue.tjmetro.render.RenderAPGGlassTianjinJinjing
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGGlassTianjinJinjing extends BlockAPGGlassTianjin implements BlockFlagAPGTianjinJinjing
{
    @Override
    public Item asItem() {
        return ItemList.APG_GLASS_TIANJIN_JINJING.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAPGGlassTianjinJinjing(pos, state);
    }

    public static class TileEntityAPGGlassTianjinJinjing extends BlockPSDTop.TileEntityRouteBase
    {
        public TileEntityAPGGlassTianjinJinjing(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.APG_GLASS_TIANJIN_JINJING_TILE_ENTITY.get(), pos, state);
        }
    }
}
