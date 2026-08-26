package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjin;

/**
 * Tianjin APG door (half-height, default / TRT style).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockAPGDoor
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGDoorTianjin extends BlockPSDAPGDoorBase implements BlockFlagAPGTianjin
{
    @Override
    protected boolean isAPG() {
        return true;
    }

    @Override
    public Item asItem() {
        return ItemList.APG_DOOR_TIANJIN.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAPGDoorTianjin(pos, state);
    }

    public static class TileEntityAPGDoorTianjin extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase
    {
        public TileEntityAPGDoorTianjin(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.APG_DOOR_TIANJIN_TILE_ENTITY.get(), pos, state);
        }
    }
}
