package ziyue.tjmetro.block;

import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

/**
 * Two blocks tall rubbish bin.
 *
 * <p>The block is a proper double block: placing it puts a {@link Half#UPPER} half one
 * block above the {@link Half#LOWER} half, the two halves render different models
 * ({@code trash_can_lower} / {@code trash_can_upper}) and breaking either half removes the other.</p>
 *
 * @author ZiYueCommentary
 */
public class BlockTrashCan extends Block
{
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    public BlockTrashCan() {
        this(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion());
    }

    public BlockTrashCan(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Level world = ctx.getLevel();
        final BlockPos pos = ctx.getClickedPos();
        // The bin occupies two blocks, so both positions have to be free before it can be placed.
        if (world.isEmptyBlock(pos.above())) {
            return defaultBlockState()
                    .setValue(FACING, ctx.getHorizontalDirection())
                    .setValue(HALF, Half.BOTTOM);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide || state.getValue(HALF) == Half.TOP) {
            return;
        }
        final BlockPos abovePos = pos.above();
        if (world.isEmptyBlock(abovePos)) {
            world.setBlock(abovePos, state.setValue(HALF, Half.TOP), 3);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide && player.getMainHandItem().isEmpty()) {
            // Rotate both halves together so the bin never ends up visually twisted.
            final Direction facing = state.getValue(FACING);
            final Direction newFacing = facing.getClockWise();
            final BlockPos otherPos = state.getValue(HALF) == Half.TOP ? pos.below() : pos.above();
            world.setBlockAndUpdate(pos, state.setValue(FACING, newFacing));
            final BlockState otherState = world.getBlockState(otherPos);
            if (otherState.getBlock() == this) {
                world.setBlockAndUpdate(otherPos, otherState.setValue(FACING, newFacing));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack itemStack) {
        if (!world.isClientSide) {
            // Only one half comes from the player, so the other half is removed without drops.
            final BlockPos otherPos = state.getValue(HALF) == Half.TOP ? pos.below() : pos.above();
            if (world.getBlockState(otherPos).getBlock() == this) {
                world.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        super.playerDestroy(world, player, pos, state, blockEntity, itemStack);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        // The upper half is only the lid (model height 3px), the lower half is the full body of the bin.
        if (blockState.getValue(HALF) == Half.TOP) {
            return IBlock.getVoxelShapeByDirection(2, 0, 2, 14, 3, 14, facing);
        }
        return IBlock.getVoxelShapeByDirection(2, 0, 2, 14, 16, 14, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }
}
