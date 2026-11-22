package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.mappings.Text;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagDoorWide;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinJinjing;

/**
 * Wide Tianjin PSD door made of three leaves.
 *
 * <p>Three leaves form a doorway. With the wrench the whole group switches which
 * side opens: two leaves slide into the left glass pocket, or two leaves slide
 * into the right glass pocket. The remaining leaf stays closed.</p>
 *
 * @author ZiYueCommentary
 * @since 1.1.7
 */
public class BlockPSDDoorTianjinWide extends BlockPSDAPGDoorBase implements BlockFlagDoorWide, BlockFlagPSDTianjinJinjing
{
    /**
     * false = 右侧两扇打开（左侧一扇不动）, true = 左侧两扇打开（右侧一扇不动）
     */
    public static final BooleanProperty OPEN_LEFT = BooleanProperty.create("open_left");

    /** 0 = 左两扇开；1 = 右两扇开；2 = 三扇全开（左二右一） */
    public static final int OPEN_MODE_LEFT = 0;
    public static final int OPEN_MODE_RIGHT = 1;
    public static final int OPEN_MODE_ALL = 2;

    /** 三扇中的位置（MTR 的 SIDE 只有 LEFT/RIGHT，这里单独用一个属性表达中间扇） */
    public static final EnumProperty<WideSide> WIDE_SIDE = EnumProperty.create("wide_side", WideSide.class);

    public enum WideSide implements StringRepresentable
    {
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right");

        private final String name;

        WideSide(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
    public BlockPSDDoorTianjinWide() {
        registerDefaultState(defaultBlockState().setValue(OPEN_LEFT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN_LEFT, WIDE_SIDE);
    }

    @Override
    public Item asItem() {
        return ItemList.PSD_DOOR_TIANJIN_WIDE.get();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPSDDoorTianjinWide(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockState state = super.getStateForPlacement(ctx);
        return state != null && state.getBlock() == this ? state.setValue(OPEN_LEFT, false) : state;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (IBlock.getStatePropertySafe(state, IBlock.SIDE) == IBlock.EnumSide.SINGLE) {
            final Direction sideDirection = IBlock.getSideDirection(state);

            // 本块变成三扇中的左扇
            final BlockState leftState = state.setValue(IBlock.SIDE, IBlock.EnumSide.LEFT).setValue(WIDE_SIDE, WideSide.LEFT);
            world.setBlock(pos, leftState, 3);
            super.setPlacedBy(world, pos, leftState, placer, stack);

            // 中间扇与右扇（含上半块）
            placeLeaf(world, pos.relative(sideDirection), state, IBlock.EnumSide.MIDDLE);
            placeLeaf(world, pos.relative(sideDirection, 2), state, IBlock.EnumSide.RIGHT);
        } else {
            super.setPlacedBy(world, pos, state, placer, stack);
        }
    }

    private static void placeLeaf(Level world, BlockPos pos, BlockState base, IBlock.EnumSide side) {
        final BlockState bottomState = base.setValue(IBlock.SIDE, side == IBlock.EnumSide.LEFT ? IBlock.EnumSide.LEFT : IBlock.EnumSide.RIGHT).setValue(WIDE_SIDE, side == IBlock.EnumSide.LEFT ? WideSide.LEFT : side == IBlock.EnumSide.MIDDLE ? WideSide.MIDDLE : WideSide.RIGHT).setValue(IBlock.HALF, DoubleBlockHalf.LOWER);
        world.setBlock(pos, bottomState, 3);
        world.setBlock(pos.above(), bottomState.setValue(IBlock.HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        // 扳手：整组（上下两半 × 三扇）一起切换开门方向，并给操作者一行提示
        if (player.getItemInHand(interactionHand).is(ItemList.WRENCH.get())) {
            if (!world.isClientSide) {
                final Direction sideDirection = IBlock.getSideDirection(blockState);

                // 点到上半块时换算到下半块，保证上下两半一起切
                BlockPos currentPos = IBlock.getStatePropertySafe(blockState, IBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;

                // 向左找到组头
                for (int i = 0; i < 2; i++) {
                    final BlockPos neighbourPos = currentPos.relative(sideDirection.getOpposite());
                    if (world.getBlockState(neighbourPos).getBlock() == this) {
                        currentPos = neighbourPos;
                    } else {
                        break;
                    }
                }

                // 循环切换：开左 -> 开右 -> 三扇全开
                final int openMode = (getOpenMode(world, currentPos) + 1) % 3;
                for (int i = 0; i < 3; i++) {
                    setOpenMode(world, currentPos, openMode);
                    setOpenMode(world, currentPos.above(), openMode);
                    currentPos = currentPos.relative(sideDirection);
                }

                player.displayClientMessage(Text.translatable(openMode == OPEN_MODE_LEFT
                        ? "message.tjmetro.psd_door_wide.open_left"
                        : openMode == OPEN_MODE_RIGHT
                        ? "message.tjmetro.psd_door_wide.open_right"
                        : "message.tjmetro.psd_door_wide.open_all"), true);
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(blockState, world, pos, player, interactionHand, blockHitResult);
    }

    /** 从方块实体读开门模式（方块状态会被 MTR 的状态刷新重置，不能用） */
    public static int getOpenMode(Level world, BlockPos pos) {
        final BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockPSDDoorTianjinWide
                && world.getBlockEntity(pos) instanceof TileEntityPSDDoorTianjinWide te) {
            return te.openMode;
        }
        return OPEN_MODE_LEFT;
    }

    /** 兼容旧调用：true 表示“左两扇开”这一档 */
    public static boolean isOpenLeft(Level world, BlockPos pos) {
        return getOpenMode(world, pos) == OPEN_MODE_LEFT;
    }

    private static void setOpenMode(Level world, BlockPos pos, int openMode) {
        if (world.getBlockState(pos).getBlock() instanceof BlockPSDDoorTianjinWide
                && world.getBlockEntity(pos) instanceof TileEntityPSDDoorTianjinWide te) {
            te.openMode = openMode;
            world.setBlock(pos, world.getBlockState(pos).setValue(OPEN_LEFT, openMode == OPEN_MODE_LEFT), 2);
            te.setChanged();
            te.syncData();
        }
    }

    public static class TileEntityPSDDoorTianjinWide extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase
    {
        /** 0 = 左两扇开；1 = 右两扇开；2 = 三扇全开 */
        public int openMode = OPEN_MODE_LEFT;

        public TileEntityPSDDoorTianjinWide(BlockPos pos, BlockState state) {
            this(BlockEntityTypes.PSD_DOOR_TIANJIN_WIDE_TILE_ENTITY.get(), pos, state);
        }

        protected TileEntityPSDDoorTianjinWide(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
            this.openMode = IBlock.getStatePropertySafe(state, OPEN_LEFT) ? OPEN_MODE_LEFT : OPEN_MODE_RIGHT;
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            openMode = compoundTag.contains("open_mode")
                    ? compoundTag.getInt("open_mode")
                    : (compoundTag.getBoolean("open_left") ? OPEN_MODE_LEFT : OPEN_MODE_RIGHT);
            super.readCompoundTag(compoundTag);
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            compoundTag.putInt("open_mode", openMode);
            super.writeCompoundTag(compoundTag);
        }
    }
}
