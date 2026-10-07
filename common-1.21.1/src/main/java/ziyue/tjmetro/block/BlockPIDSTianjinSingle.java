package ziyue.tjmetro.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import ziyue.tjmetro.BlockEntityTypes;

import org.jetbrains.annotations.Nullable;

/**
 * Single-row (single-face) PIDS — Tianjin Metro.
 * The screen occupies two blocks: the data block ({@code should_render=true}) stores the
 * configuration and renders the front face, while the block placed to its right
 * ({@code should_render=false}) is only a dummy half that renders nothing.
 *
 * <p>Port of {@code BlockPIDSTianjinSingle} from the upstream Mapping API implementation.
 * Adapted to the non-Mapping MTR 1.20.1 API used by this Architectury project.</p>
 *
 * @author ZiYueCommentary (original Mapping-API implementation)
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see BlockPIDSTianjin
 */
public class BlockPIDSTianjinSingle extends BlockPIDSTianjin {

    public static final BooleanProperty SHOULD_RENDER = BooleanProperty.create("should_render");

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        // Never break the screen on neighbour updates (the dummy half is managed by setPlacedBy/playerWillDestroy).
        return state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getHorizontalDirection();
        return IBlock.isReplaceable(ctx, direction.getClockWise(), 2)
                ? defaultBlockState().setValue(FACING, direction).setValue(SHOULD_RENDER, true)
                : null;
    }

    @Override
    public boolean canStoreData(BlockGetter world, BlockPos pos) {
        return IBlock.getStatePropertySafe(world, pos, SHOULD_RENDER);
    }

    @Override
    public BlockPos getBlockPosWithData(BlockGetter world, BlockPos pos) {
        if (canStoreData(world, pos)) {
            return pos;
        } else {
            final Direction direction = IBlock.getStatePropertySafe(world, pos, FACING);
            return pos.relative(IBlock.getStatePropertySafe(world, pos, SHOULD_RENDER) ? direction.getClockWise() : direction.getCounterClockWise());
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide) {
            final Direction direction = IBlock.getStatePropertySafe(state, FACING);
            world.setBlock(pos.relative(direction.getClockWise()), defaultBlockState().setValue(FACING, direction).setValue(SHOULD_RENDER, false), 3);
            world.updateNeighborsAt(pos, Blocks.AIR);
            state.updateNeighbourShapes(world, pos, 3);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction direction = IBlock.getStatePropertySafe(state, FACING);
        // Break the counterpart half; the base class must not be called here because it would
        // remove the block in front of the screen (pos.relative(FACING)) instead of the dummy half.
        IBlock.onBreakCreative(world, player, pos.relative(IBlock.getStatePropertySafe(state, SHOULD_RENDER) ? direction.getClockWise() : direction.getCounterClockWise()));
        world.destroyBlock(pos, false, player);
        // 1.21 起 playerWillDestroy 需要返回 BlockState。此处刻意不调用 super（理由见上），按原语义返回原状态。
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHOULD_RENDER);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjinSingle(pos, state);
    }

    /**
     * Single-row PIDS block entity. Extends the main PIDS tile entity (so the shared config
     * screen and packets keep working) but is registered with its own block entity type.
     */
    public static class TileEntityBlockPIDSTianjinSingle extends TileEntityBlockPIDSTianjin {

        public TileEntityBlockPIDSTianjinSingle(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PIDS_TIANJIN_SINGLE_TILE_ENTITY.get(), pos, state);
        }
    }
}
