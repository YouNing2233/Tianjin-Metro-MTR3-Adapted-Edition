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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
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
 * Station Name Plate, ported from the MTR4 Tianjin Metro addon
 * (<i>BlockStationNamePlate</i>, 1.0.0-beta-1).
 * <p>
 * A two-sided hanging sign (one "end" block, two middle blocks and a flipped
 * "end" block on the other side) that displays the station name of a selected
 * platform together with a directional arrow.
 *
 * @author ZiYueCommentary
 * @see BlockRailwaySignBase
 * @see mtr.block.BlockRouteSignBase.TileEntityRouteSignBase
 * @see ziyue.tjmetro.render.RenderStationNamePlate
 * @since 1.1.7
 */
public class BlockStationNamePlate extends BlockRailwaySignBase
{
    public static final IntegerProperty ARROW_DIRECTION = IntegerProperty.create("arrow_direction", 0, 2);

    public BlockStationNamePlate() {
        this(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2), 8, false);
    }

    public BlockStationNamePlate(BlockBehaviour.Properties properties, int length, boolean isOdd) {
        super(properties, length, isOdd);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            final Direction hitSide = hit.getDirection();
            if (hitSide == facing || hitSide == facing.getOpposite()) {
                final BlockPos checkPos = findEndWithDirection(world, pos, hitSide.getOpposite(), false);
                if (checkPos != null) {
                    if (item == Items.BRUSH.get()) {
                        world.setBlockAndUpdate(checkPos, world.getBlockState(checkPos).cycle(ARROW_DIRECTION));
                    } else {
                        PacketGuiServer.openRailwaySignScreenS2C((ServerPlayer) player, checkPos);
                    }
                }
            }
        }, null, Items.BRUSH.get(), ItemList.WRENCH.get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        return IRailwaySign.updateShape(state, direction, newState, BlockList.STATION_NAME_PLATE_MIDDLE.get());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        IRailwaySign.setPlacedBy(world, pos, state, BlockList.STATION_NAME_PLATE_MIDDLE.get(), getMiddleLength());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        if (state.is(BlockList.STATION_NAME_PLATE_MIDDLE.get())) {
            return IBlock.getVoxelShapeByDirection(0, 3, 7, 16, 12, 9, facing);
        } else {
            final VoxelShape main = IBlock.getVoxelShapeByDirection(0, 3, 7, 16, 12, 9, facing);
            final VoxelShape pole = IBlock.getVoxelShapeByDirection(-1, 3, 7, 0.25, 16, 9, facing);
            return Shapes.or(main, pole);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, net.minecraft.world.item.Item.TooltipContext blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
        IGuiExtends.addHoldShiftTooltip(tooltip, Text.translatable("tooltip.tjmetro.station_name_plate"), true);
    }

    @Override
    protected int getMiddleLength() {
        return 2;
    }

    @Override
    public String getDescriptionId() {
        return "block.tjmetro.station_name_plate";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ARROW_DIRECTION, WATERLOGGED);
    }

    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.STATION_NAME_PLATE_MIDDLE.get());
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        if (this == BlockList.STATION_NAME_PLATE_MIDDLE.get()) {
            return null;
        } else {
            return new TileEntityStationNamePlate(pos, state);
        }
    }

    /**
     * @see ziyue.tjmetro.render.RenderStationNamePlate
     */
    public static class TileEntityStationNamePlate extends BlockRouteSignBase.TileEntityRouteSignBase
    {
        public TileEntityStationNamePlate(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.STATION_NAME_PLATE_TILE_ENTITY.get(), pos, state);
        }
    }
}
