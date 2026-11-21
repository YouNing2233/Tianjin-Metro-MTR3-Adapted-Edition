package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;

/**
 * Single-faced PIDS — Tianjin Metro, Line 2 layout.
 *
 * <p>Same two-block placement logic as {@link BlockPIDSTianjinSingle} (the data block renders,
 * the dummy half does not), but rendering the Line 2 screen layout of {@link BlockPIDSTianjin2}.</p>
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see BlockPIDSTianjinSingle
 * @see BlockPIDSTianjin2
 */
public class BlockPIDSTianjin2Single extends BlockPIDSTianjinSingle {

    public BlockPIDSTianjin2Single() {
        super();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjin2Single(pos, state);
    }

    public static class TileEntityBlockPIDSTianjin2Single extends TileEntityBlockPIDSTianjinSingle {

        public TileEntityBlockPIDSTianjin2Single(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PIDS_TIANJIN_2_SINGLE_TILE_ENTITY.get(), pos, state);
        }
    }
}
