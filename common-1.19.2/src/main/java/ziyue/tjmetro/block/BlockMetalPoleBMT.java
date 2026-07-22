package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;
import ziyue.tjmetro.block.base.BlockCustomColorBase;

public class BlockMetalPoleBMT extends BlockCustomColorBase
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public BlockMetalPoleBMT() {
        this(Properties.of(Material.STONE).noOcclusion());
    }

    public BlockMetalPoleBMT(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // 根据点击面决定放置方向：点击顶/底面→竖直，点击侧面→水平
        return defaultBlockState()
                .setValue(AXIS, ctx.getClickedFace().getAxis())
                .setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        final boolean isPole = newState.is(BlockList.METAL_POLE_BMT.get());
        return switch (direction) {
            case NORTH -> state.setValue(NORTH, isPole);
            case EAST -> state.setValue(EAST, isPole);
            case SOUTH -> state.setValue(SOUTH, isPole);
            case WEST -> state.setValue(WEST, isPole);
            case UP -> state.setValue(UP, isPole);
            case DOWN -> state.setValue(DOWN, isPole);
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        final Direction.Axis axis = state.getValue(AXIS);
        VoxelShape shape = switch (axis) {
            case Y -> Block.box(6, 0, 6, 10, 16, 10);
            case X -> Block.box(0, 0, 6, 16, 4, 10);
            case Z -> Block.box(6, 0, 0, 10, 4, 16);
        };
        if (state.getValue(NORTH)) shape = Shapes.or(shape, Block.box(6, 0, 0, 10, 4, 6));
        if (state.getValue(EAST)) shape = Shapes.or(shape, Block.box(10, 0, 6, 16, 4, 10));
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, Block.box(6, 0, 10, 10, 4, 16));
        if (state.getValue(WEST)) shape = Shapes.or(shape, Block.box(0, 0, 6, 6, 4, 10));
        if (state.getValue(UP)) shape = Shapes.or(shape, Block.box(6, 10, 6, 10, 16, 10));
        if (state.getValue(DOWN)) shape = Shapes.or(shape, Block.box(6, 0, 6, 10, 6, 10));
        return shape;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public static class BlockEntity extends BlockCustomColorBase.CustomColorBlockEntity
    {
        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(BlockEntityTypes.METAL_POLE_BMT.get(), blockPos, blockState);
            this.color = 0xfff100;
        }
    }
}
