package ziyue.tjmetro.block;

import mtr.Blocks;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

/**
 * An even (double-width) time display, requires better model.
 * Placed as a two-block pair: the second block is placed at the clockwise side, facing the opposite direction.
 *
 * @author ZiYueCommentary
 * @see BlockTimeDisplay
 * @see ziyue.tjmetro.render.RenderTimeDisplayEven
 * @since 1.1.8 - Ported from 4.0
 */
public class BlockTimeDisplayEven extends BlockTimeDisplay
{
    public BlockTimeDisplayEven() {
        this(BlockBehaviour.Properties.ofFullCopy(Blocks.CLOCK.get()).lightLevel(state -> 5));
    }

    public BlockTimeDisplayEven(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockState state = defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, ctx.getHorizontalDirection());
        if (IBlock.isReplaceable(ctx, state.getValue(FACING).getClockWise(), 2)) {
            ctx.getLevel().setBlock(ctx.getClickedPos().relative(state.getValue(FACING).getClockWise()), state.setValue(FACING, state.getValue(FACING).getOpposite()), 2);
            return state;
        }
        return null;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        IBlockExtends.breakBlock(world, pos.relative(state.getValue(FACING).getClockWise()), this);
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return IBlock.getVoxelShapeByDirection(5, 8.5, 6, 16, 16, 10, blockState.getValue(FACING));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntityTimeDisplayEven(blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see BlockTimeDisplay.TileEntityTimeDisplay
     * @see ziyue.tjmetro.render.RenderTimeDisplayEven
     * @since 1.1.8 - Ported from 4.0
     */
    public static class TileEntityTimeDisplayEven extends BlockTimeDisplay.TileEntityTimeDisplay
    {
        public TileEntityTimeDisplayEven(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.TIME_DISPLAY_EVEN_TILE_ENTITY.get(), pos, state, 0.21F, 0.63F);
        }
    }
}
