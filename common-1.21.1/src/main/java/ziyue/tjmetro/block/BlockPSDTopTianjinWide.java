package ziyue.tjmetro.block;

import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinJinjing;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

/**
 * Tianjin wide PSD top plate (鐧屾澘) used above the three leaf wide platform screen door.
 * <p>
 * Unlike {@link BlockPSDTopTianjin}, the texture is drawn by a single renderer call which
 * spans the whole group of plates, so three plates together show one continuous board
 * instead of three independent boards.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.render.RenderPSDTopTianjinWide
 * @since 1.1.6
 */
public class BlockPSDTopTianjinWide extends BlockPSDTopTianjin implements BlockFlagPSDTianjinJinjing
{
    public BlockPSDTopTianjinWide() {
        super(true);
    }

    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_TIANJIN_JINJING.get();
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingItem(world, player, item -> {
            if (item == ItemList.WRENCH.get()) {
                // 扳手：整组吊板在「站名 / 下一站」之间切换
                final EnumPSDType next = IBlock.getStatePropertySafe(state, STYLE) == EnumPSDType.NEXT_STATION ? EnumPSDType.STATION_NAME : EnumPSDType.NEXT_STATION;
                cycleGroupStyle(world, pos, next);
            } else if (item == mtr.Items.BRUSH.get()) {
                // 刷子：整组吊板一起切换箭头方向（与 MTR 原版一致）
                cycleGroup(world, pos, groupState -> groupState.cycle(ARROW_DIRECTION));
            }
        }, null, ItemList.WRENCH.get(), mtr.Items.BRUSH.get());
    }

    private static void cycleGroupStyle(Level world, BlockPos pos, EnumPSDType style) {
        cycleGroup(world, pos, state -> state.setValue(STYLE, style));
    }

    private static void cycleGroup(Level world, BlockPos pos, java.util.function.UnaryOperator<BlockState> operator) {
        setStyleAt(world, pos, operator);
        final BlockState state = world.getBlockState(pos);
        final net.minecraft.core.Direction facing = IBlock.getStatePropertySafe(state, FACING);
        for (net.minecraft.core.Direction direction : new net.minecraft.core.Direction[]{facing.getClockWise(), facing.getCounterClockWise()}) {
            for (int i = 1; i <= 8; i++) {
                final BlockPos offsetPos = pos.relative(direction, i);
                if (world.getBlockState(offsetPos).getBlock() instanceof BlockPSDTopTianjinWide) {
                    setStyleAt(world, offsetPos, operator);
                } else {
                    break;
                }
            }
        }
    }

    private static void setStyleAt(Level world, BlockPos pos, java.util.function.UnaryOperator<BlockState> operator) {
        final BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockPSDTopTianjinWide) {
            final BlockState updated = operator.apply(state);
            if (updated != state) {
                world.setBlockAndUpdate(pos, updated);
            }
        }
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPSDTopTianjinWide(pos, state);
    }

    public static class TileEntityPSDTopTianjinWide extends BlockPSDTop.TileEntityRouteBase
    {
        public TileEntityPSDTopTianjinWide(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PSD_TOP_TIANJIN_WIDE_TILE_ENTITY.get(), pos, state);
        }
    }
}
