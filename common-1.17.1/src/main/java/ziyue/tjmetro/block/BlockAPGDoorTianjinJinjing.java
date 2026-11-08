package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjinJinjing;

/**
 * Tianjin Jinjing-line APG door (half-height).
 *
 * @author ZiYueCommentary
 * @see BlockAPGDoorTianjin
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGDoorTianjinJinjing extends BlockAPGDoorTianjin implements BlockFlagAPGTianjinJinjing
{
    @Override
    public Item asItem() {
        return ItemList.APG_DOOR_TIANJIN_JINJING.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAPGDoorTianjinJinjing(pos, state);
    }

    public static class TileEntityAPGDoorTianjinJinjing extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase
    {
        public TileEntityAPGDoorTianjinJinjing(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.APG_DOOR_TIANJIN_JINJING_TILE_ENTITY.get(), pos, state);
        }
    }
}
