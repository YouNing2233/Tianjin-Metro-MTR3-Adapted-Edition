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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
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
 * Wall-mounted version of the Binhai New Area (BMT, Tianjin Metro Line 9) route map,
 * <i>路线图（9号线，墙上）</i>.
 * <p>
 * Same overall geometry as {@link BlockRouteMapBMT} (three blocks wide, two blocks tall, plate
 * spanning the lower twelve pixels of the upper half and the full lower half) with three changes:
 * <ul>
 *     <li>the plate is one pixel thick and sits flush against the supporting wall instead of
 *     floating in the middle of the block;</li>
 *     <li>the hanging rod of the end block is removed;</li>
 *     <li>the block can only exist while the block it faces is sturdy, so it is a true wall sign.</li>
 * </ul>
 * Because the hanging sign is read from the player's side, {@code FACING} points <i>towards</i> the
 * wall, matching {@code mtr.block.BlockStationNameWallBase}, while the texture faces
 * {@code FACING.getOpposite()}.
 *
 * @author ZiYueCommentary
 * @see BlockRouteMapBMT
 * @see BlockRailwaySignBase
 * @see ziyue.tjmetro.render.RenderRouteMapBMTWall
 * @since 1.1.9
 */
public class BlockRouteMapBMTWall extends BlockRailwaySignBase
{
    public BlockRouteMapBMTWall() {
        this(BlockBehaviour.Properties.of().noOcclusion().noCollission().lightLevel(state -> 10));
    }

    public BlockRouteMapBMTWall(BlockBehaviour.Properties properties) {
        super(properties, 6, false);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            // The block entity only exists on the upper half, so normalize to it before searching.
            final BlockPos basePos = IBlock.getStatePropertySafe(world, pos, HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos;
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            final Direction hitSide = hit.getDirection();
            // Only accept clicks on the two horizontal faces of the plate.
            if (hitSide == facing || hitSide == facing.getOpposite()) {
                // Always resolve to the START block, the one whose FACING equals the FACING used at
                // placement time. setPlacedBy gives the two end blocks OPPOSITE FACING values and
                // RenderRouteMapBMTWall reads FACING from whichever end block holds the block
                // entity, so letting the clicked column/face decide which end gets configured makes
                // the rendered route map flip by 180 degrees and sit towards the wrong side.
                // Walking towards FACING.getCounterClockWise() always reaches the start block
                // first, whatever column was clicked, so the orientation is now deterministic.
                final BlockPos checkPos = findEndWithDirection(world, basePos, facing, false);
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
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return world.getBlockState(pos.relative(facing)).isFaceSturdy(world, pos.relative(facing), facing.getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        // Vertical neighbor: the upper/lower halves must always be paired.
        final boolean isTop = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER;
        if ((isTop && direction == Direction.DOWN || !isTop && direction == Direction.UP) && !newState.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        // The wall this sign is mounted on disappeared.
        if (direction == IBlock.getStatePropertySafe(state, FACING) && !state.canSurvive(world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        // A column only exists as part of a row along the wall: if both horizontal neighbours are
        // gone, this column is a leftover. This is checked by connectivity instead of with
        // IRailwaySign.updateShape because that helper keys off the opposite FACING that the
        // hanging sign gives its far end; every column here keeps the same FACING, so on the far
        // end its "next block" direction would point out of the sign and merely placing a block
        // beside the sign would delete it.
        if (direction.getAxis() != Direction.Axis.Y) {
            final boolean hasRowNeighbour =
                    world.getBlockState(pos.relative(facing.getClockWise())).getBlock() instanceof BlockRailwaySignBase
                            || world.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock() instanceof BlockRailwaySignBase;
            if (!hasRowNeighbour) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction side = ctx.getClickedFace();
        if (side == Direction.UP || side == Direction.DOWN) return null;

        final Direction facing = side.getOpposite();
        final Direction extend = facing.getClockWise();
        if (!IBlock.isReplaceable(ctx, extend, getMiddleLength() + 2)) return null;

        final Level world = ctx.getLevel();
        final BlockPos pos = ctx.getClickedPos();
        for (int i = 0; i < getMiddleLength() + 2; i++) {
            final BlockPos columnPos = pos.relative(extend, i);
            // The lower half must be free as well.
            if (!world.getBlockState(columnPos.below()).canBeReplaced(ctx)) return null;
            // Every block of the sign needs a sturdy wall behind it.
            if (!isSturdy(world, columnPos, facing) || !isSturdy(world, columnPos.below(), facing)) return null;
        }

        return defaultBlockState().setValue(FACING, facing).setValue(HALF, DoubleBlockHalf.UPPER).setValue(SIDE, IBlock.EnumSide.LEFT);
    }

    private static boolean isSturdy(LevelReader world, BlockPos pos, Direction facing) {
        final BlockPos wallPos = pos.relative(facing);
        return world.getBlockState(wallPos).isFaceSturdy(world, wallPos, facing.getOpposite());
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final BlockPos basePos = IBlock.getStatePropertySafe(world, pos, HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos;
        // Break the column at the other end of the row (creative only), then this column's other
        // half. The remaining middle columns disappear through updateShape.
        final BlockPos startPos = findEndWithDirection(world, basePos, facing, true);
        final Direction step = facing.getClockWise();
        BlockPos otherPos = startPos;
        while (world.getBlockState(otherPos.relative(step)).getBlock() instanceof BlockRailwaySignBase) {
            otherPos = otherPos.relative(step);
        }
        if (!otherPos.equals(basePos)) {
            IBlock.onBreakCreative(world, player, otherPos);
            IBlock.onBreakCreative(world, player, otherPos.below());
        }
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER) {
            IBlock.onBreakCreative(world, player, pos.below());
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide) return;

        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        world.setBlock(pos.below(), defaultBlockState().setValue(FACING, facing).setValue(HALF, DoubleBlockHalf.LOWER).setValue(SIDE, IBlock.EnumSide.LEFT), 3);
        final BlockState middleState = BlockList.ROUTE_MAP_BMT_WALL_MIDDLE.get().defaultBlockState().setValue(FACING, facing);
        for (int i = 1; i <= getMiddleLength(); i++) {
            final BlockPos middlePos = pos.relative(facing.getClockWise(), i);
            world.setBlock(middlePos, middleState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
            world.setBlock(middlePos.below(), middleState.setValue(HALF, DoubleBlockHalf.LOWER), 3);
        }
        final BlockPos endPos = pos.relative(facing.getClockWise(), getMiddleLength() + 1);
        world.setBlock(endPos, defaultBlockState().setValue(FACING, facing).setValue(HALF, DoubleBlockHalf.UPPER).setValue(SIDE, IBlock.EnumSide.LEFT), 3);
        world.setBlock(endPos.below(), defaultBlockState().setValue(FACING, facing).setValue(HALF, DoubleBlockHalf.LOWER).setValue(SIDE, IBlock.EnumSide.LEFT), 3);
        world.updateNeighborsAt(pos, Blocks.AIR);
        state.updateNeighbourShapes(world, pos, 3);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final boolean isTop = IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER;
        // One pixel thick, flush against the wall, with the same vertical extent as the hanging
        // sign (lower twelve pixels of the upper half, the full lower half). No rod.
        return IBlock.getVoxelShapeByDirection(0, 0, 0, 16, isTop ? 12 : 16, 1, facing);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, net.minecraft.world.item.Item.TooltipContext blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
        IGuiExtends.addHoldShiftTooltip(tooltip, Text.translatable("tooltip.tjmetro.station_name_plate"), true);
    }

    @Override
    protected int getMiddleLength() {
        return 1;
    }

    @Override
    public String getDescriptionId() {
        return "block.tjmetro.route_map_bmt_wall";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, SIDE, WATERLOGGED);
    }

    /**
     * Walks away from the direction the sign was laid out in and returns the outermost block of the
     * row, which is the START block: the one that owns the visible route map.
     * <p>
     * The hanging {@link BlockRouteMapBMT} marks its two ends by giving the far one the opposite
     * {@code FACING}. That cannot be done here: the plate of a wall sign is only one pixel thick and
     * therefore asymmetric, so flipping {@code FACING} moves that column's plate to the other face
     * of the block (it would poke out of the back of the wall) and makes its {@code canSurvive}
     * check the wrong neighbor. Every column of the wall sign therefore keeps the same
     * {@code FACING}, and the ends are told apart purely by position.
     */
    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        final Direction step = direction.getCounterClockWise();
        BlockPos pos = startPos;
        while (world.getBlockState(pos.relative(step)).getBlock() instanceof BlockRailwaySignBase) {
            pos = pos.relative(step);
        }
        return pos;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        if (this == BlockList.ROUTE_MAP_BMT_WALL_MIDDLE.get() || IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.LOWER) {
            return null;
        } else {
            return new TileEntityRouteMapBMTWall(pos, state);
        }
    }

    /**
     * Extends {@link BlockRouteMapBMT.TileEntityRouteMapBMT} on purpose: the railway sign screen
     * matches on that class with {@code instanceof}, so the wall sign reuses the existing platform
     * selection GUI without any change there.
     *
     * @see ziyue.tjmetro.render.RenderRouteMapBMTWall
     */
    public static class TileEntityRouteMapBMTWall extends BlockRouteMapBMT.TileEntityRouteMapBMT
    {
        public TileEntityRouteMapBMTWall(BlockPos pos, BlockState state) {
            this(BlockEntityTypes.ROUTE_MAP_BMT_WALL.get(), pos, state);
        }

        public TileEntityRouteMapBMTWall(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }
}
