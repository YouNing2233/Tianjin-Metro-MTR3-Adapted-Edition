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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import ziyue.tjmetro.BlockEntityTypes;

import org.jetbrains.annotations.Nullable;

/**
 * Single-sided PIDS (Passenger Information Display System) — Tianjin Metro.
 * Only renders one face; the other half is a non-functional dummy block placed
 * automatically to fill the 1x2 footprint.
 *
 * @author ZiYueCommentary (original Mapping-API implementation)
 * @author __Lingxii__ (Architectury multi-platform port)
 * @since 1.0.0-rc-6
 */
public class BlockPIDSTianjinSingle extends BlockPIDSTianjin {

    public static final BooleanProperty SHOULD_RENDER = BooleanProperty.create("should_render");

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getHorizontalDirection();
        final BlockPos neighborPos = ctx.getClickedPos().relative(direction.getClockWise());
        final BlockState neighborState = ctx.getLevel().getBlockState(neighborPos);
        if (neighborState.canBeReplaced(ctx)) {
            return defaultBlockState().setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING, direction).setValue(SHOULD_RENDER, true);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide) {
            final Direction direction = IBlock.getStatePropertySafe(state, net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
            world.setBlock(pos.relative(direction.getClockWise()), defaultBlockState().setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING, direction).setValue(SHOULD_RENDER, false), 3);
            world.updateNeighborsAt(pos, net.minecraft.world.level.block.Blocks.AIR);
            state.updateNeighbourShapes((LevelAccessor) world, pos, 3);
        }
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
            final Direction direction = IBlock.getStatePropertySafe(world, pos, net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
            return pos.relative(IBlock.getStatePropertySafe(world, pos, SHOULD_RENDER) ? direction.getClockWise() : direction.getCounterClockWise());
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction direction = IBlock.getStatePropertySafe(state, net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
        final BlockPos neighborPos = pos.relative(IBlock.getStatePropertySafe(state, SHOULD_RENDER) ? direction.getClockWise() : direction.getCounterClockWise());
        if (!world.isClientSide) {
            world.destroyBlock(neighborPos, !player.isCreative());
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHOULD_RENDER);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjin(pos, state, true);
    }
}
