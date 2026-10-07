package ziyue.tjmetro.block;

import mtr.Items;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.base.BlockCustomColorBase;
import ziyue.tjmetro.data.IGuiExtends;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @since 1.0.0-beta-4
 */

public class BlockStationNameProjector extends BlockCustomColorBase
{
    public static final IntegerProperty SCALE = IntegerProperty.create("scale", 1, 7);

    public BlockStationNameProjector() {
        this(Properties.of(Material.STONE).strength(2).lightLevel(state -> 15).noOcclusion().noCollission());
    }

    public BlockStationNameProjector(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(SCALE, 2));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            if (item == Items.BRUSH.get()) {
                world.setBlockAndUpdate(pos, state.cycle(SCALE));
            } else {
                final net.minecraft.world.level.block.entity.BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof CustomColorBlockEntity) {
                    CustomColorBlockEntity entity1 = (CustomColorBlockEntity) entity;
                    entity1.syncData();
                    PacketGuiServer.openCustomColorScreenS2C((net.minecraft.server.level.ServerPlayer) player, pos);
                }
            }
        }, null, Items.BRUSH.get(), ItemList.WRENCH.get());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(SCALE, 2);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(new TranslatableComponent("tooltip.tjmetro.custom_color").withStyle(net.minecraft.ChatFormatting.GRAY));
        IGuiExtends.addHoldShiftTooltip(tooltip, (net.minecraft.network.chat.MutableComponent) new TranslatableComponent("tooltip.tjmetro.station_name_projector").withStyle(net.minecraft.ChatFormatting.GRAY), false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 16, 1, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SCALE);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.render.RenderStationNameProjector
     * @since 1.0.0-beta-4
     */
    public static class BlockEntity extends BlockCustomColorBase.CustomColorBlockEntity
    {
        public BlockEntity(BlockPos blockPos, BlockState state) {
            super(BlockEntityTypes.STATION_NAME_PROJECTOR.get(), blockPos, state);
        }

        public int getDefaultColor(BlockPos pos) {
            return 0xFFFF0000;
        }
    }
}
