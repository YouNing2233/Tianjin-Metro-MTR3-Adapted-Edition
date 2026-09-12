package ziyue.tjmetro.block;

import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import mtr.mappings.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.TianjinMetro;
import ziyue.tjmetro.data.IGuiExtends;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Smoke alarm. Emits a redstone signal while activated; can be reset with a brush or wrench.
 *
 * @since 1.1.x (ported from 4.0)
 */
public class BlockSmokeAlarm extends Block implements EntityBlockMapper
{
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public BlockSmokeAlarm() {
        this(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 6.0F).lightLevel(state -> IBlock.getStatePropertySafe(state, ACTIVATED) ? 5 : 0));
    }

    public BlockSmokeAlarm(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return defaultBlockState().setValue(ACTIVATED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.or(
                Block.box(5.6, 15, 5.6, 10.4, 16, 10.4),
                Block.box(6.08, 14.4, 6.08, 9.92, 15, 9.92),
                Block.box(6.9, 14, 6.9, 7.9, 14.4, 7.9)
        );
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return IBlockExtends.checkHoldingBrushOrWrench(level, player, () -> {
            level.setBlockAndUpdate(blockPos, blockState.setValue(ACTIVATED, false));
            level.updateNeighborsAt(blockPos, blockState.getBlock());
        });
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return IBlock.getStatePropertySafe(blockState, ACTIVATED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return IBlock.getStatePropertySafe(blockState, ACTIVATED) ? 15 : 0;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        IGuiExtends.addHoldShiftTooltip(list, Text.translatable("tooltip.tjmetro.smoke_alarm"), true);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntitySmokeAlarm(blockPos, blockState);
    }

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getType() {
        return BlockEntityTypes.SMOKE_ALARM_TILE_ENTITY.get();
    }

    @Override
    public <T extends BlockEntityMapper> void tick(Level level, BlockPos pos, T blockEntity) {
        if (!level.isClientSide) {
            final BlockState state = level.getBlockState(pos);
            final boolean activated = isFireNearby(level, pos);
            if (activated != state.getValue(ACTIVATED)) {
                level.setBlock(pos, state.setValue(ACTIVATED, activated), 3);
                level.updateNeighborsAt(pos, state.getBlock());
            }
        }
        TileEntitySmokeAlarm.tick(level, pos);
    }

    private static boolean isFireNearby(Level level, BlockPos pos) {
        final int range = level.getGameRules().getInt(TianjinMetro.SMOKE_ALARM_RANGE);
        for (final BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range))) {
            final BlockState checkState = level.getBlockState(checkPos);
            final Block block = checkState.getBlock();
            if (block == Blocks.FIRE || block == Blocks.SOUL_FIRE || block == Blocks.CAMPFIRE || block == Blocks.SOUL_CAMPFIRE || block == Blocks.LAVA || block == Blocks.MAGMA_BLOCK) {
                return true;
            }
        }
        return false;
    }

    public static class TileEntitySmokeAlarm extends BlockEntityMapper
    {
        public static BiConsumer<BlockPos, Boolean> updateSoundSource = null;

        public TileEntitySmokeAlarm(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.SMOKE_ALARM_TILE_ENTITY.get(), pos, state);
        }

        public static void tick(Level level, BlockPos pos) {
            if (level.isClientSide && updateSoundSource != null) {
                updateSoundSource.accept(pos, !IBlock.getStatePropertySafe(level.getBlockState(pos), ACTIVATED));
            }
        }

        @Override
        public void setRemoved() {
            if (level != null && level.isClientSide && updateSoundSource != null) {
                updateSoundSource.accept(worldPosition, true);
            }
            super.setRemoved();
        }
    }
}
