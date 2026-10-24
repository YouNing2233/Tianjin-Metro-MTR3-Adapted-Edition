package ziyue.tjmetro.block;

import mtr.Items;
import mtr.block.BlockRouteSignBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.base.BlockRailwaySignBase;
import ziyue.tjmetro.block.base.IRailwaySign;
import ziyue.tjmetro.data.IGuiExtends;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

/**
 * Binhai New Area (BMT, Tianjin Metro Line 9) route map, ported from the MTR4 Tianjin Metro addon
 * (<i>BlockRouteMapBMT</i>, 1.0.0).
 * <p>
 * A two-block-tall hanging sign (one "end" block, one middle block and a flipped "end" block on the
 * other side, each with an upper and a lower half) that displays the route map of a selected
 * platform. Use the wrench to pick the platform to display (Railway Sign Screen) and the brush to
 * flip which side the arrow points to.
 *
 * @author ZiYueCommentary
 * @see BlockRailwaySignBase
 * @see mtr.block.BlockRouteSignBase.TileEntityRouteSignBase
 * @see ziyue.tjmetro.render.RenderRouteMapBMT
 * @since 1.1.8
 */
public class BlockRouteMapBMT extends BlockRailwaySignBase
{
    public BlockRouteMapBMT() {
        this(BlockBehaviour.Properties.of(Material.STONE).noOcclusion().lightLevel(state -> 10));
    }

    public BlockRouteMapBMT(BlockBehaviour.Properties properties) {
        super(properties, 6, false);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            // The block entity only exists on the upper half, so normalize to it before searching.
            final BlockPos basePos = IBlock.getStatePropertySafe(world, pos, HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos;
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            final Direction hitSide = hit.getDirection();
            if (hitSide == facing || hitSide == facing.getOpposite()) {
                final BlockPos checkPos = findEndWithDirection(world, basePos, hitSide.getOpposite(), false);
                if (checkPos != null) {
                    if (item == Items.BRUSH.get()) {
                        world.setBlockAndUpdate(checkPos, world.getBlockState(checkPos).cycle(SIDE));
                    } else {
                        PacketGuiServer.openRailwaySignScreenS2C((ServerPlayer) player, checkPos);
                    }
                }
            }
        }, null, Items.BRUSH.get(), ItemList.WRENCH.get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        // Vertical neighbor: the upper/lower halves must always be paired.
        final boolean isTop = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER;
        if ((isTop && direction == Direction.DOWN || !isTop && direction == Direction.UP) && !newState.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        // Horizontal neighbor: middle blocks and end blocks must always be paired.
        return IRailwaySign.updateShape(state, direction, newState, BlockList.ROUTE_MAP_BMT_MIDDLE.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction facing = ctx.getHorizontalDirection();
        if (!IBlock.isReplaceable(ctx, facing.getClockWise(), getMiddleLength() + 2)) return null;
        final Level world = ctx.getLevel();
        final BlockPos pos = ctx.getClickedPos();
        for (int i = 0; i < getMiddleLength() + 2; i++) {
            if (!world.getBlockState(pos.below().relative(facing.getClockWise(), i)).canBeReplaced(ctx)) {
                return null;
            }
        }
        return defaultBlockState().setValue(FACING, facing).setValue(HALF, DoubleBlockHalf.UPPER).setValue(SIDE, IBlock.EnumSide.LEFT);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final BlockPos basePos = IBlock.getStatePropertySafe(world, pos, HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos;
        final BlockPos checkPos = findEndWithDirection(world, basePos, facing, true);
        if (checkPos != null) {
            IBlock.onBreakCreative(world, player, checkPos);
            IBlock.onBreakCreative(world, player, checkPos.below());
        }
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER) {
            IBlock.onBreakCreative(world, player, pos.below());
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide) return;

        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        world.setBlock(pos.below(), defaultBlockState().setValue(FACING, facing).setValue(HALF, DoubleBlockHalf.LOWER).setValue(SIDE, IBlock.EnumSide.LEFT), 3);
        final BlockState middleState = BlockList.ROUTE_MAP_BMT_MIDDLE.get().defaultBlockState().setValue(FACING, facing);
        for (int i = 1; i <= getMiddleLength(); i++) {
            final BlockPos middlePos = pos.relative(facing.getClockWise(), i);
            world.setBlock(middlePos, middleState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
            world.setBlock(middlePos.below(), middleState.setValue(HALF, DoubleBlockHalf.LOWER), 3);
        }
        final BlockPos endPos = pos.relative(facing.getClockWise(), getMiddleLength() + 1);
        world.setBlock(endPos, defaultBlockState().setValue(FACING, facing.getOpposite()).setValue(HALF, DoubleBlockHalf.UPPER).setValue(SIDE, IBlock.EnumSide.LEFT), 3);
        world.setBlock(endPos.below(), defaultBlockState().setValue(FACING, facing.getOpposite()).setValue(HALF, DoubleBlockHalf.LOWER).setValue(SIDE, IBlock.EnumSide.LEFT), 3);
        world.updateNeighborsAt(pos, Blocks.AIR);
        state.updateNeighbourShapes(world, pos, 3);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final boolean isTop = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER;
        if (state.is(BlockList.ROUTE_MAP_BMT_MIDDLE.get())) {
            return IBlock.getVoxelShapeByDirection(0, 0, 7, 16, isTop ? 12 : 16, 9, facing);
        } else {
            if (isTop) {
                final VoxelShape pole = IBlock.getVoxelShapeByDirection(3, 0, 7, 4.25, 16, 9, facing);
                final VoxelShape plate = IBlock.getVoxelShapeByDirection(4.25, 0, 7, 16, 12, 9, facing);
                return Shapes.or(pole, plate);
            } else {
                return IBlock.getVoxelShapeByDirection(3, 0, 7, 16, 16, 9, facing);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
        IGuiExtends.addHoldShiftTooltip(tooltip, Text.translatable("tooltip.tjmetro.station_name_plate"), true);
    }

    @Override
    protected int getMiddleLength() {
        return 1;
    }

    @Override
    public String getDescriptionId() {
        return "block.tjmetro.route_map_bmt";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, SIDE, WATERLOGGED);
    }

    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.ROUTE_MAP_BMT_MIDDLE.get());
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        if (this == BlockList.ROUTE_MAP_BMT_MIDDLE.get() || IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.LOWER) {
            return null;
        } else {
            return new TileEntityRouteMapBMT(pos, state);
        }
    }

    /**
     * Stores the selected platform id. The renderer only draws the route map on the block that has
     * been configured (its {@code platform_id} is non-zero), so the two end blocks never overlap.
     *
     * @see ziyue.tjmetro.render.RenderRouteMapBMT
     */
    public static class TileEntityRouteMapBMT extends BlockRouteSignBase.TileEntityRouteSignBase
    {
        public TileEntityRouteMapBMT(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.ROUTE_MAP_BMT.get(), pos, state);
        }

        // Lets subclasses (the wall-mounted variant) reuse this type while registering their own BlockEntityType.
        public TileEntityRouteMapBMT(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }
}
