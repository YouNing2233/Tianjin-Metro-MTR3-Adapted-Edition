package ziyue.tjmetro.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.TianjinMetro;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class BlockPlayerDetector extends Block implements SimpleWaterloggedBlock, EntityBlockMapper
{
    public BlockPlayerDetector() {
        this(BlockBehaviour.Properties.copy(Blocks.STONE_PRESSURE_PLATE));
    }

    public BlockPlayerDetector(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return defaultBlockState()
                .setValue(POWERED, false)
                .setValue(WATERLOGGED, false)
                .setValue(FACING, blockPlaceContext.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, WATERLOGGED, FACING);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) && blockState.getValue(FACING) == direction ? 15 : 0;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (IBlockExtends.isHorizontalDirection(blockState.getValue(FACING)))
            return IBlock.getVoxelShapeByDirection(1.7, 2, 14.8, 14.3, 4, 17, blockState.getValue(FACING));
        else if (blockState.getValue(FACING) == Direction.UP)
            return Block.box(6, 0, 6, 10, 1, 10);
        else
            return Block.box(6, 15, 6, 10, 16, 10);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPlayerDetector(pos, state);
    }

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getType() {
        return BlockEntityTypes.PLAYER_DETECTOR_TILE_ENTITY.get();
    }

    @Override
    public <T extends BlockEntityMapper> void tick(Level level, BlockPos pos, T blockEntity) {
        if (level.isClientSide) return;
        final BlockState state = level.getBlockState(pos);
        final int detectRange = level.getGameRules().getInt(TianjinMetro.PLAYER_DETECT_RANGE);
        final net.minecraft.world.entity.player.Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, detectRange, false);
        final boolean powered = player != null;
        if (powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), 3);
            level.updateNeighborsAt(pos, state.getBlock());
            level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), state.getBlock());
        }
    }

    public static class TileEntityPlayerDetector extends BlockEntityMapper
    {
        public TileEntityPlayerDetector(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PLAYER_DETECTOR_TILE_ENTITY.get(), pos, state);
        }
    }
}
