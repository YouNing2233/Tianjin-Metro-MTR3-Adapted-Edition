package ziyue.tjmetro.block;

import mtr.block.IBlock;
import mtr.mappings.BlockDirectionalMapper;
import mtr.mappings.BlockEntityClientSerializableMapper;
import mtr.mappings.EntityBlockMapper;
import mtr.mappings.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.block.IBlockExtends;
import ziyue.tjmetro.data.IGuiExtends;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.List;
import java.util.Locale;

/**
 * Single-block railway sign, ported from the MTR4 Tianjin Metro addon
 * (<i>BlockRailwaySignSingle</i>, 1.1.3).
 * <p>
 * One block holding two independent sign slots: slot 0 is shown on the face the player is looking
 * at, slot 1 on the opposite face. Use a brush or the wrench to open the sign selection screen for
 * the face that was clicked.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.render.RenderRailwaySignSingle
 * @see ziyue.tjmetro.screen.RailwaySignSingleScreen
 * @since 1.1.3
 */
public class BlockRailwaySignSingle extends BlockDirectionalMapper implements IBlock, EntityBlockMapper
{
    public BlockRailwaySignSingle() {
        this(Properties.of(Material.STONE).strength(2).lightLevel(state -> 15).noOcclusion());
    }

    public BlockRailwaySignSingle(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hit) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final Direction hitSide = hit.getDirection();
        if (hitSide == facing.getCounterClockWise() || hitSide == facing.getClockWise()) {
            return InteractionResult.PASS;
        }
        return IBlockExtends.checkHoldingBrushOrWrench(world, player,
                () -> PacketGuiServer.openRailwaySignSingleScreenS2C((ServerPlayer) player, pos, hitSide == facing ? 1 : 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return Shapes.or(
                IBlock.getVoxelShapeByDirection(3.5, 3, 6, 12.5, 12, 10, facing),
                IBlock.getVoxelShapeByDirection(7.5, 12, 7.5, 8.5, 16, 8.5, facing));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
        IGuiExtends.addHoldShiftTooltip(tooltip, Text.translatable("tooltip.tjmetro.railway_sign_single"), true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntityClientSerializableMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntity(pos, state);
    }

    /**
     * Holds one sign id per face. Slots are synced to the client so the renderer can draw them.
     */
    public static class BlockEntity extends BlockEntityClientSerializableMapper
    {
        protected final String[] signIds = new String[2];
        protected static final String KEY_SIGN_ID = "sign_id";

        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.RAILWAY_SIGN_SINGLE.get(), pos, state);
        }

        public BlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            for (int i = 0; i < signIds.length; i++) {
                final String signId = compoundTag.getString(KEY_SIGN_ID + i);
                signIds[i] = signId.isEmpty() ? null : signId;
            }
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            for (int i = 0; i < signIds.length; i++) {
                compoundTag.putString(KEY_SIGN_ID + i, signIds[i] == null ? "" : signIds[i]);
            }
        }


        public void setData(int index, String signId) {
            if (index < 0 || index >= signIds.length) return;
            this.signIds[index] = signId;
            setChanged();
            syncData();
        }

        public String getSignId(int index) {
            return signIds[index];
        }
    }
}
