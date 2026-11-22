package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;

import org.jetbrains.annotations.Nullable;

/**
 * PIDS (Passenger Information Display System) - Tianjin Metro, wall-mounted.
 *
 * <p>Same content, same layout, same configuration screen as {@link BlockPIDSTianjinSingle}: the
 * canvas keeps its native size, so the picture spans two blocks horizontally. What differs is only
 * the way it is mounted - the panel stands upright against a wall instead of leaning back, and it
 * may only be placed on a side face of a solid block.</p>
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see BlockPIDSTianjinSingle
 */
public class BlockPIDSTianjinWall extends BlockPIDSTianjinSingle {

    public BlockPIDSTianjinWall() {
        super();
    }

    /**
     * Only allow placement against a wall. {@code getClickedPos()} already is the position the
     * block will occupy, so the wall is the neighbour opposite to the clicked face.
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final Direction clickedFace = context.getClickedFace();
        if (!clickedFace.getAxis().isHorizontal()) {
            return null;
        }
        // FACING points at the wall, exactly like the free-standing panels: the picture is painted
        // on the opposite side of the block, i.e. the side the player is looking at.
        final Direction facing = clickedFace.getOpposite();
        if (context.getLevel().getBlockState(context.getClickedPos().relative(facing)).isAir()) {
            return null;
        }
        if (!context.getLevel().getBlockState(context.getClickedPos().relative(facing.getClockWise())).isAir()) {
            return null;
        }
        return defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing).setValue(SHOULD_RENDER, true);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        final Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        return !world.getBlockState(pos.relative(facing)).isAir();
    }

    /**
     * Only the picture hangs there: keep a razor-thin plate so the panel does not block the
     * platform like a whole block of air would.
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
            case NORTH:
                // FACING points at the wall, so the panel hangs on the north edge of the block
                return Block.box(0, 0, 0, 16, 16, 0.25);
            case SOUTH:
                return Block.box(0, 0, 15.75, 16, 16, 16);
            case EAST:
                return Block.box(15.75, 0, 0, 16, 16, 16);
            case WEST:
            default:
                return Block.box(0, 0, 0, 0.25, 16, 16);
        }
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjinWall(pos, state);
    }

    /**
     * Wall-mounted block entity: identical data to the single-row display, own registry entry.
     */
    public static class TileEntityBlockPIDSTianjinWall extends TileEntityBlockPIDSTianjinSingle {

        public TileEntityBlockPIDSTianjinWall(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PIDS_TIANJIN_WALL_TILE_ENTITY.get(), pos, state);
        }

        protected TileEntityBlockPIDSTianjinWall(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }
}
