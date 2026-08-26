package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjin;

/**
 * Tianjin PSD door (default / TRT style).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockPSDDoor
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDDoorTianjin extends BlockPSDAPGDoorBase implements BlockFlagPSDTianjin
{
    @Override
    public Item asItem() {
        return ItemList.PSD_DOOR_TIANJIN.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPSDDoorTianjin(pos, state);
    }

    public static class TileEntityPSDDoorTianjin extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase
    {
        public TileEntityPSDDoorTianjin(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PSD_DOOR_TIANJIN_TILE_ENTITY.get(), pos, state);
        }
    }
}
