package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjin;

/**
 * Wide Tianjin APG door (half-height) made of three leaves.
 *
 * <p>Same behaviour as {@link BlockPSDDoorTianjinWide}: three leaves form a doorway and the
 * wrench switches which side opens. Only the height differs (half-height APG instead of PSD).</p>
 *
 * @author ZiYueCommentary
 * @since 1.1.8
 */
public class BlockAPGDoorTianjinWide extends BlockPSDDoorTianjinWide implements BlockFlagAPGTianjin
{
    @Override
    protected boolean isAPG() {
        return true;
    }

    @Override
    public Item asItem() {
        return ItemList.APG_DOOR_TIANJIN_WIDE.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAPGDoorTianjinWide(pos, state);
    }

    public static class TileEntityAPGDoorTianjinWide extends BlockPSDDoorTianjinWide.TileEntityPSDDoorTianjinWide
    {
        public TileEntityAPGDoorTianjinWide(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.APG_DOOR_TIANJIN_WIDE_TILE_ENTITY.get(), pos, state);
        }
    }
}
