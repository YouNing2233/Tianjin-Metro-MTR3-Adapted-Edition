package ziyue.tjmetro.block;

import mtr.Items;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;
import ziyue.tjmetro.block.base.BlockRailwaySignBase;
import ziyue.tjmetro.block.base.IRailwaySign;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class BlockStationNavigator extends BlockRailwaySignBase
{
    public static final BooleanProperty ARROW_LEFT = BooleanProperty.create("arrow_left");

    public final int length;

    public BlockStationNavigator(int length) {
        this(length, Properties.of().noOcclusion().lightLevel(state -> 15));
    }

    public BlockStationNavigator(int length, Properties blockSettings) {
        super(blockSettings, length, true);
        this.length = length;
    }

    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.STATION_NAVIGATOR_MIDDLE.get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        return IRailwaySign.updateShape(state, direction, newState, BlockList.STATION_NAVIGATOR_MIDDLE.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction facing = ctx.getHorizontalDirection();
        if (IBlock.isReplaceable(ctx, facing.getClockWise(), 2) && IBlock.isReplaceable(ctx, facing.getCounterClockWise(), 2)) {
            final Level world = ctx.getLevel();
            final BlockPos pos = ctx.getClickedPos();
            world.setBlock(pos.relative(facing.getCounterClockWise()), defaultBlockState().setValue(FACING, facing).setValue(ARROW_LEFT, false), 3);
            world.setBlock(pos.relative(facing.getClockWise()), defaultBlockState().setValue(FACING, facing.getOpposite()).setValue(ARROW_LEFT, false), 3);
            world.updateNeighborsAt(pos, Blocks.AIR);
            defaultBlockState().updateNeighbourShapes(world, pos, 3);
            return BlockList.STATION_NAVIGATOR_MIDDLE.get().defaultBlockState().setValue(FACING, facing);
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            final BlockPos checkPos = findEndWithDirection(world, pos, hit.getDirection().getOpposite(), false);
            if (checkPos == null) return;
            if (item == ziyue.tjmetro.ItemList.WRENCH.get()) {
                PacketGuiServer.openRailwaySignScreenS2C((net.minecraft.server.level.ServerPlayer) player, checkPos);
            } else {
                world.setBlock(checkPos, world.getBlockState(checkPos).cycle(ARROW_LEFT), 3);
            }
        }, null, ziyue.tjmetro.ItemList.WRENCH.get(), Items.BRUSH.get());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
    }

    @Override
    protected int getMiddleLength() {
        return 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Text.translatable("tooltip.mtr.railway_sign_length", length).withStyle(ChatFormatting.GRAY));
        tooltip.add(Text.translatable("tooltip.tjmetro.station_navigator").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        if (blockState.is(BlockList.STATION_NAVIGATOR_MIDDLE.get())) {
            return IBlock.getVoxelShapeByDirection(0, 0, 7, 16, 9, 9, facing);
        } else {
            return IBlock.getVoxelShapeByDirection(0, 0, 7, 16, 9, 9, facing);
        }
    }

    @Override
    public String getDescriptionId() {
        return "block.tjmetro.station_navigator";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ARROW_LEFT, WATERLOGGED);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        if (length != 0)
            return new BlockEntity(length, pos, state);
        else
            return null;
    }

    public static class BlockEntity extends BlockRailwaySignBase.TileEntityRailwaySign
    {
        public final int length;

        public BlockEntity(int length, BlockPos pos, BlockState state) {
            super(getType(length), length, pos, state);
            this.length = length;
        }

        public static net.minecraft.world.level.block.entity.BlockEntityType<?> getType(int length) {
            return switch (length) {
                case 3 -> BlockEntityTypes.STATION_NAVIGATOR_3.get();
                case 5 -> BlockEntityTypes.STATION_NAVIGATOR_5.get();
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
