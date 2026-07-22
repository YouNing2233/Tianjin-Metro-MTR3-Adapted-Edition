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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class BlockTrashCan extends Block
{
    public BlockTrashCan() {
        this(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion());
    }

    public BlockTrashCan(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getHorizontalDirection();
        final Level world = ctx.getLevel();
        final BlockPos pos = ctx.getClickedPos();
        // 仅检查上方是否为空，不在此时放置上方方块
        // 上方方块的放置交给 setPlacedBy，避免重复触发放置逻辑
        if (world.isEmptyBlock(pos.above())) {
            return defaultBlockState().setValue(FACING, direction);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide) {
            final BlockPos abovePos = pos.above();
            final BlockPos belowPos = pos.below();
            // 仅当下方不是垃圾桶时才放置上半部分（防止上半部分触发setPlacedBy导致无限堆叠）
            if (world.isEmptyBlock(abovePos) && !(world.getBlockState(belowPos).getBlock() instanceof BlockTrashCan)) {
                world.setBlock(abovePos, defaultBlockState().setValue(FACING, state.getValue(FACING)), 3);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            if (player.getMainHandItem().isEmpty()) {
                world.setBlockAndUpdate(pos, state.cycle(FACING));
                world.setBlockAndUpdate(pos.above(), world.getBlockState(pos.above()).cycle(FACING));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity, net.minecraft.world.item.ItemStack itemStack) {
        // Break the other half of the trash can
        final BlockState aboveState = world.getBlockState(pos.above());
        final BlockState belowState = world.getBlockState(pos.below());
        if (aboveState.getBlock() instanceof BlockTrashCan) {
            IBlockExtends.breakBlock(world, pos.above());
        }
        if (belowState.getBlock() instanceof BlockTrashCan) {
            IBlockExtends.breakBlock(world, pos.below());
        }
        super.playerDestroy(world, player, pos, state, blockEntity, itemStack);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        return IBlock.getVoxelShapeByDirection(1, 0, 1, 15, 16, 15, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
