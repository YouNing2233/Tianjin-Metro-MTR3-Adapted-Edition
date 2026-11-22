package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;

/**
 * Line 2 flavour of the header-mounted PIDS: same block, same behaviour, different board drawing.
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see BlockPIDSTianjinPsdTop
 */
public class BlockPIDSTianjin2PsdTop extends BlockPIDSTianjinPsdTop {

    public BlockPIDSTianjin2PsdTop() {
        super();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjin2PsdTop(pos, state);
    }

    public static class TileEntityBlockPIDSTianjin2PsdTop extends TileEntityBlockPIDSTianjinPsdTop {

        public TileEntityBlockPIDSTianjin2PsdTop(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PIDS_TIANJIN_2_PSD_TOP_TILE_ENTITY.get(), pos, state);
        }

        protected TileEntityBlockPIDSTianjin2PsdTop(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }
}
