package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinJinjing;

/**
 * Tianjin Jinjing-line PSD door.
 *
 * @author ZiYueCommentary
 * @see BlockPSDDoorTianjin
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDDoorTianjinJinjing extends BlockPSDDoorTianjin implements BlockFlagPSDTianjinJinjing
{
    @Override
    public Item asItem() {
        return ItemList.PSD_DOOR_TIANJIN_JINJING.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPSDDoorTianjinJinjing(pos, state);
    }

    public static class TileEntityPSDDoorTianjinJinjing extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase
    {
        public TileEntityPSDDoorTianjinJinjing(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PSD_DOOR_TIANJIN_JINJING_TILE_ENTITY.get(), pos, state);
        }
    }
}
