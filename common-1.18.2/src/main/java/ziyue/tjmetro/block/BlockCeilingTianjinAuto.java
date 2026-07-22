package ziyue.tjmetro.block;

import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.core.Direction;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import java.util.Random;
import net.minecraft.world.item.context.BlockPlaceContext;
import java.util.Random;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.level.Level;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import java.util.Random;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;
import net.minecraft.world.level.block.state.StateDefinition;
import java.util.Random;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import java.util.Random;
import net.minecraft.world.level.material.FluidState;
import java.util.Random;
import net.minecraft.world.level.material.Material;
import java.util.Random;
import net.minecraft.world.level.material.Fluids;
import java.util.Random;
import net.minecraft.world.level.material.Material;
import java.util.Random;
import net.minecraft.world.phys.shapes.CollisionContext;
import java.util.Random;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Random;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

/**
 * 天花板（天津，自动亮度），根据位置自动判断是否亮起。
 * 1:1 还原自 4.0 版本的 BlockCeilingTianjinAuto。
 *
 * @author ZiYueCommentary
 * @since 1.1.4
 */
public class BlockCeilingTianjinAuto extends Block implements SimpleWaterloggedBlock
{
    public static final BooleanProperty LIGHT = BooleanProperty.create("light");
    protected static final VoxelShape SHAPE = Block.box(0, 7, 0, 16, 10, 16);

    public BlockCeilingTianjinAuto() {
        super(BlockBehaviour.Properties.of(Material.STONE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(2).noOcclusion());
    }

    public BlockCeilingTianjinAuto(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(LIGHT, hasLight(ctx.getClickedPos())).setValue(WATERLOGGED, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos).setValue(LIGHT, hasLight(pos));
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        final boolean light = hasLight(pos);
        if (state.getValue(LIGHT) != light) {
            world.setBlock(pos, state.setValue(LIGHT, light), 3);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LIGHT);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    protected boolean hasLight(BlockPos pos) {
        return (pos.getZ() % 2 == 0) && (pos.getX() % 2 == 0);
    }
}
