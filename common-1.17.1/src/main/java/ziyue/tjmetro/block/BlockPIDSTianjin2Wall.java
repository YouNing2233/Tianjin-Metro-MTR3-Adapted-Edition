package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;

/**
 * PIDS - Tianjin Metro, Line 2 layout, wall-mounted.
 *
 * <p>Wall-mounted twin of {@link BlockPIDSTianjin2}: the Line 2 screen layout on the same upright
 * two-block panel as {@link BlockPIDSTianjinWall}.</p>
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see BlockPIDSTianjinWall
 * @see BlockPIDSTianjin2
 */
public class BlockPIDSTianjin2Wall extends BlockPIDSTianjinWall {

    public BlockPIDSTianjin2Wall() {
        super();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjin2Wall(pos, state);
    }

    /**
     * Line 2 wall-mounted block entity.
     */
    public static class TileEntityBlockPIDSTianjin2Wall extends TileEntityBlockPIDSTianjinWall {

        public TileEntityBlockPIDSTianjin2Wall(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PIDS_TIANJIN_2_WALL_TILE_ENTITY.get(), pos, state);
        }
    }
}
