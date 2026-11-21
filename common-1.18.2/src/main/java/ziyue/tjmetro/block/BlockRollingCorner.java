package ziyue.tjmetro.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockList;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

/**
 * Corner section of the Tianjin rolling shutter (ported from the author's own
 * {@code mtr-external-lights} model). The wrench (or the MTR brush) flips the block
 * between the upper and the bottom rail model, exactly like {@link BlockRolling}.
 *
 * @author __YouNing__
 */

public class BlockRollingCorner extends Block implements SimpleWaterloggedBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final BooleanProperty CHANGED = BooleanProperty.create("changed");

    public BlockRollingCorner() {
        this(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.IRON_BARS).noOcclusion());
    }

    public BlockRollingCorner(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction facing = ctx.getHorizontalDirection().getOpposite();
        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(CHANGED, false)
                .setValue(BOTTOM, !(ctx.getLevel().getBlockState(ctx.getClickedPos().below()).getBlock() == BlockList.ROLLING_CORNER.get()))
                .setValue(WATERLOGGED, false);
    }

    /**
     * Rotates a box given in the north-facing orientation by the block's facing.
     *
     * <p>The number of quarter turns has to match the {@code y} rotation of the blockstate
     * (north = 0, east = 90, south = 180, west = 270). {@link Direction#get2DDataValue()}
     * orders the horizontal directions as south, west, north, east, so a constant offset of
     * two is needed - without it the collision shape ends up 180 degrees away from the model.</p>
     */
    private static VoxelShape rotate(double x0, double z0, double x1, double z1, Direction facing) {
        double ax = x0, az = z0, bx = x1, bz = z1;
        final int steps = facing == null ? 0 : (facing.get2DDataValue() + 2) % 4;
        for (int i = 0; i < steps; i++) {
            final double nx0 = 16 - bz, nz0 = ax, nx1 = 16 - az, nz1 = bx;
            ax = nx0;
            az = nz0;
            bx = nx1;
            bz = nz1;
        }
        return Block.box(Math.min(ax, bx), 0, Math.min(az, bz), Math.max(ax, bx), 16, Math.max(az, bz));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        final Direction facing = blockState.getValue(FACING);
        return Shapes.or(
                rotate(6, 6, 16, 10, facing),
                rotate(6, 10, 10, 16, facing)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BOTTOM, CHANGED, FACING, WATERLOGGED);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return IBlockExtends.checkHoldingBrushOrWrench(level, player, () -> level.setBlockAndUpdate(blockPos, blockState.cycle(BOTTOM).setValue(CHANGED, true)));
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (!blockState.getValue(CHANGED)) {
            if (level.getBlockState(blockPos.below()).getBlock() == BlockList.ROLLING_CORNER.get())
                level.setBlockAndUpdate(blockPos, blockState.setValue(BOTTOM, false));
            else
                level.setBlockAndUpdate(blockPos, blockState.setValue(BOTTOM, true));
        }
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }
}
