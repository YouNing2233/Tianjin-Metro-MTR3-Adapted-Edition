package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;

/**
 * PIDS (Passenger Information Display System) — Tianjin Metro, Line 2 layout.
 *
 * <p>Same geometry and item icon as {@link BlockPIDSTianjin}, but a different screen texture
 * and a different on-screen layout: the line name occupies the top of the left column, only the
 * next train is shown, and the clock plus the date get their own panel underneath.</p>
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see BlockPIDSTianjin
 */
public class BlockPIDSTianjin2 extends BlockPIDSTianjin {

    public BlockPIDSTianjin2() {
        super();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjin2(pos, state);
    }

    /**
     * Line 2 layout block entity. Extends the shared PIDS tile entity so the configuration
     * screen, advertisement categories and scrolling text keep working unchanged.
     */
    public static class TileEntityBlockPIDSTianjin2 extends TileEntityBlockPIDSTianjin {


        public TileEntityBlockPIDSTianjin2(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PIDS_TIANJIN_2_TILE_ENTITY.get(), pos, state);
        }
    }
}
