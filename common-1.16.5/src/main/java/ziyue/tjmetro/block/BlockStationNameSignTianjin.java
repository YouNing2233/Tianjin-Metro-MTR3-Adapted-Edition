package ziyue.tjmetro.block;

import mtr.block.BlockRouteSignBase;
import mtr.block.BlockStationNameBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.block.base.BlockStationNameSignBase;
import ziyue.tjmetro.packet.PacketGuiServer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

/**
 * Tianjin Station Name Sign, ported from MTR4 {@code ziyue.tjmetro.mod.block.BlockStationNameSignTianjin}.
 * A double-block (LEFT/RIGHT) wall sign; only the LEFT half renders the platform-based dynamic texture.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.render.RenderStationNameSignTianjin
 * @see BlockStationNameSignBase
 * @since 1.1.8
 */

public class BlockStationNameSignTianjin extends BlockStationNameSignBase implements IBlock
{
    public BlockStationNameSignTianjin() {
        this(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2).lightLevel(state -> 10).noOcclusion());
    }

    public BlockStationNameSignTianjin(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return IBlockExtends.checkHoldingBrushOrWrench(world, player, () -> {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof TileEntityStationNameSignTianjin) {
                TileEntityStationNameSignTianjin entity1 = (TileEntityStationNameSignTianjin) entity;
                entity1.syncData();
                PacketGuiServer.openRailwaySignScreenS2C((ServerPlayer) player, pos);
            }
        });
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getHorizontalDirection();
        final BlockState state = defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, false);
        if (IBlock.isReplaceable(ctx, direction.getClockWise(), 2)) {
            ctx.getLevel().setBlock(ctx.getClickedPos().relative(direction.getClockWise()), state.setValue(SIDE, IBlock.EnumSide.RIGHT), 3);
            return state.setValue(SIDE, IBlock.EnumSide.LEFT);
        }
        return null;
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final IBlock.EnumSide side = IBlock.getStatePropertySafe(state, SIDE);
        final BlockPos otherPos = pos.relative(side == IBlock.EnumSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        if (world.getBlockState(otherPos).getBlock() == this) {
            IBlock.onBreakCreative(world, player, otherPos);
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        final Direction direction = IBlock.getStatePropertySafe(state, FACING);
        if (IBlock.getStatePropertySafe(state, SIDE) == IBlock.EnumSide.LEFT) {
            return IBlock.getVoxelShapeByDirection(4, 0, 0, 16, 16, 1, direction);
        } else {
            return IBlock.getVoxelShapeByDirection(0, 0, 0, 12, 16, 1, direction);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR, FACING, WATERLOGGED, SIDE);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityStationNameSignTianjin(pos, state);
    }

    /**
     * Stores the selected {@code platformId}; both halves share the same platform id.
     *
     * @author ZiYueCommentary
     * @see mtr.block.BlockRouteSignBase.TileEntityRouteSignBase
     * @since 1.1.8
     */
    public static class TileEntityStationNameSignTianjin extends BlockRouteSignBase.TileEntityRouteSignBase
    {
        public TileEntityStationNameSignTianjin(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.STATION_NAME_SIGN_TIANJIN.get(), pos, state);
        }

        /**
         * MTR4-compatible entry point: {@code setData(long platformId)} updates both halves.
         */
        public void setData(long platformId) {
            setPlatformId(platformId);
        }

        @Override
        public void setPlatformId(long platformId) {
            super.setPlatformId(platformId);
            if (level != null && !level.isClientSide && getPlatformId() == platformId) {
                final BlockEntity partner = level.getBlockEntity(getPartnerPos());
                if (partner instanceof TileEntityStationNameSignTianjin && ((TileEntityStationNameSignTianjin) partner).getPlatformId() != platformId) {
                    ((TileEntityStationNameSignTianjin) partner).setPlatformId(platformId);
                }
            }
        }

        private BlockPos getPartnerPos() {
            final BlockState state = getBlockState();
            final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);
            final IBlock.EnumSide side = IBlock.getStatePropertySafe(state, IBlock.SIDE);
            return getBlockPos().relative(side == IBlock.EnumSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        }
    }
}
