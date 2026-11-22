package ziyue.tjmetro.block;

import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ziyue.tjmetro.BlockEntityTypes;

/**
 * Flat passenger information screen that hangs in front of a platform screen door top plate.
 * It is 2.9 blocks wide and 0.9 blocks tall, so three blocks are used: the anchor block in the middle
 * (which stores the data and draws the content) plus one hidden block on each side.
 */
public class BlockPIDSTianjinPsdTop extends BlockPIDSTianjin
{
    public static final BooleanProperty SHOULD_RENDER = BooleanProperty.create("should_render");

    public BlockPIDSTianjinPsdTop() {
        super();
        registerDefaultState(defaultBlockState().setValue(SHOULD_RENDER, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHOULD_RENDER);
    }

    public static boolean isPlatformScreenDoorTop(BlockState state) {
        return state.getBlock() instanceof BlockPSDTop;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isVertical()) {
            return null;
        }

        final Direction facing = clickedFace.getOpposite();
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        if (!world.getBlockState(pos.relative(facing)).is(this) && !isPlatformScreenDoorTop(world.getBlockState(pos.relative(facing)))) {
            return null;
        }

        if (!world.getBlockState(pos.relative(facing.getClockWise())).canBeReplaced(context)
                || !world.getBlockState(pos.relative(facing.getCounterClockWise())).canBeReplaced(context)) {
            return null;
        }

        return defaultBlockState().setValue(FACING, facing).setValue(SHOULD_RENDER, true);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide) {
            return;
        }

        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        world.setBlockAndUpdate(pos.relative(facing.getClockWise()), state.setValue(SHOULD_RENDER, false));
        world.setBlockAndUpdate(pos.relative(facing.getCounterClockWise()), state.setValue(SHOULD_RENDER, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final BlockState neighbour = world.getBlockState(pos.relative(facing));
        return neighbour.is(this) || isPlatformScreenDoorTop(neighbour);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        if (direction == facing && !newState.is(this) && !isPlatformScreenDoorTop(newState)) {
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    @Override
    public boolean canStoreData(BlockGetter world, BlockPos pos) {
        return IBlock.getStatePropertySafe(world, pos, SHOULD_RENDER);
    }

    @Override
    public BlockPos getBlockPosWithData(BlockGetter world, BlockPos pos) {
        return pos;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // 只有中间那块（玩家放置的那块）有碰撞箱，两侧的陪衬方块是空气体积。
        return IBlock.getStatePropertySafe(state, SHOULD_RENDER) ? getPlateShape(IBlock.getStatePropertySafe(state, FACING)) : Shapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return IBlock.getStatePropertySafe(state, SHOULD_RENDER) ? getPlateShape(IBlock.getStatePropertySafe(state, FACING)) : Shapes.empty();
    }

    private static VoxelShape getPlateShape(Direction facing) {
        return switch (facing) {
            case SOUTH -> Block.box(0, 1.2, 15.75, 16, 15.6, 16);
            case EAST -> Block.box(15.75, 1.2, 0, 16, 15.6, 16);
            case WEST -> Block.box(0, 1.2, 0, 0.25, 15.6, 16);
            default -> Block.box(0, 1.2, 0, 16, 15.6, 0.25);
        };
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjinPsdTop(pos, state);
    }

    public static class TileEntityBlockPIDSTianjinPsdTop extends TileEntityBlockPIDSTianjin
    {
        public TileEntityBlockPIDSTianjinPsdTop(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PIDS_TIANJIN_PSD_TOP_TILE_ENTITY.get(), pos, state);
        }

        protected TileEntityBlockPIDSTianjinPsdTop(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }
}
