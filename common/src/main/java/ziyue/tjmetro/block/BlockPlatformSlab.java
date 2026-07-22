package ziyue.tjmetro.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

/**
 * 站台台阶（天津）。
 * 1:1 还原自 4.0 版本 MTR 库中的 BlockPlatformSlab。
 * 支持 type（bottom/top/double）、facing、side（1-4）、waterlogged 属性。
 *
 * @author ZiYueCommentary
 * @since 1.1.4
 */
public class BlockPlatformSlab extends SlabBlock implements SimpleWaterloggedBlock
{
    public static final IntegerProperty SIDE = IntegerProperty.create("side", 0, 4);

    public BlockPlatformSlab() {
        this(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2));
    }

    public BlockPlatformSlab(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockState state = defaultBlockState();
        final BlockPos pos = ctx.getClickedPos();
        final boolean facing = ctx.getHorizontalDirection().getAxis() == Direction.Axis.X;
        final SlabType type = ctx.getClickedFace() == Direction.DOWN && ctx.getLevel().getBlockState(pos.below()).getBlock() == this
                ? SlabType.TOP
                : (ctx.getClickedFace() == Direction.UP && ctx.getLevel().getBlockState(pos.above()).getBlock() == this
                ? SlabType.BOTTOM
                : SlabType.BOTTOM);
        return state.setValue(BlockStateProperties.SLAB_TYPE, type)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, facing ? Direction.EAST : Direction.NORTH)
                .setValue(SIDE, 0)
                .setValue(BlockStateProperties.WATERLOGGED, ctx.getLevel().getFluidState(pos).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.SLAB_TYPE, BlockStateProperties.HORIZONTAL_FACING, SIDE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }
}
