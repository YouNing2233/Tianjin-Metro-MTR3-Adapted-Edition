package ziyue.tjmetro.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityClientSerializableMapper;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

/**
 * A wall sign used at the security check area.
 *
 * @author ZiYueCommentary
 * @see TileEntitySecurityCheckSign
 * @since 1.1.8
 */

public class BlockSecurityCheckSign extends HorizontalDirectionalBlock implements EntityBlockMapper, SimpleWaterloggedBlock
{
    public BlockSecurityCheckSign() {
        this(Properties.copy(BlockList.LOGO.get()));
    }

    public BlockSecurityCheckSign(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection()).setValue(IBlockExtends.THIRD, IBlockExtends.BlockThirdProperty.RIGHT).setValue(WATERLOGGED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, IBlockExtends.THIRD, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return IBlock.getVoxelShapeByDirection(1.5f, 0f, 0f, 14.5f, 16f, 0.1f, IBlock.getStatePropertySafe(blockState, FACING));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return IBlockExtends.checkHoldingBrushOrWrench(world, player, () -> world.setBlockAndUpdate(pos, blockState.cycle(IBlockExtends.THIRD)));
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntitySecurityCheckSign(pos, state);
    }

    public static class TileEntitySecurityCheckSign extends BlockEntityClientSerializableMapper
    {
        public final float yOffset;
        public final float zOffset;

        public TileEntitySecurityCheckSign(BlockPos pos, BlockState state) {
            this(BlockEntityTypes.SECURITY_CHECK_SIGN_TILE_ENTITY.get(), pos, state, 0, 0.009375f);
        }

        public TileEntitySecurityCheckSign(BlockEntityType<?> entityType, BlockPos pos, BlockState state, float yOffset, float zOffset) {
            super(entityType, pos, state);
            this.yOffset = yOffset;
            this.zOffset = zOffset;
        }

        public boolean shouldRender() {
            return true;
        }
    }
}
