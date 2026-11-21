package ziyue.tjmetro.item;

import mtr.RegistryObject;
import mtr.block.BlockPSDAPGBase;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.BlockPSDAPGGlassEndBase;
import mtr.block.BlockPSDAPGGlassBase;
import mtr.block.IBlock;
import mtr.item.ItemPSDAPGBase;
import mtr.item.ItemWithCreativeTabBase;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import ziyue.tjmetro.TianjinMetro;
import ziyue.tjmetro.block.BlockPSDTopTianjin;
import ziyue.tjmetro.block.BlockPSDTopTianjinBMT;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjin;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjinBMT;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjinTRT;
import ziyue.tjmetro.block.flag.BlockFlagDoorSingle;
import ziyue.tjmetro.block.BlockPSDDoorTianjinWide;
import ziyue.tjmetro.block.flag.BlockFlagDoorWide;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjin;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinBMT;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinJinjing;

import org.jetbrains.annotations.Nullable;
import java.util.List;

import static mtr.block.IBlock.HALF;
import static mtr.block.IBlock.SIDE;
import static mtr.block.IBlock.SIDE_EXTENDED;

/**
 * Tianjin variant of {@link ItemPSDAPGBase}.
 * Handles multi-block placement for PSD/APG door, glass, and glass end blocks.
 * Automatically places the corresponding PSD top block for PSD variants.
 *
 * @author ZiYueCommentary
 * @since 1.1.6 - Ported from 4.0
 */
public class ItemPSDAPGTianjinBase extends ItemWithCreativeTabBase implements IBlock
{
    public final RegistryObject<Block> blockRegistryObject;

    public ItemPSDAPGTianjinBase(RegistryObject<Block> blockRegistryObject) {
        super(TianjinMetro.CREATIVE_MODE_TAB);
        this.blockRegistryObject = blockRegistryObject;
    }

    public Block getBlock() {
        return blockRegistryObject.get();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Block block = getBlock();
        final int horizontalBlocks = block instanceof BlockFlagDoorWide ? 3 : (block instanceof BlockPSDAPGDoorBase && !(block instanceof BlockFlagDoorSingle)) ? 2 : 1;
        if (ItemPSDAPGBase.blocksNotReplaceable(context, horizontalBlocks, isAPG() ? 2 : 3, block)) {
            return InteractionResult.FAIL;
        }

        final Level world = context.getLevel();
        final Direction playerFacing = context.getHorizontalDirection();
        final BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        for (int x = 0; x < horizontalBlocks; x++) {
            final BlockPos newPos = pos.relative(playerFacing.getClockWise(), x);

            for (int y = 0; y < 2; y++) {
                final BlockState state = block.defaultBlockState()
                        .setValue(BlockPSDAPGBase.FACING, playerFacing)
                        .setValue(HALF, y == 1 ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);

                if (block instanceof BlockFlagDoorSingle doorSingle) {
                    world.setBlockAndUpdate(newPos.above(y), state.setValue(SIDE, doorSingle.isLeft() ? EnumSide.LEFT : EnumSide.RIGHT));
                } else if (block instanceof BlockFlagDoorWide) {
                    world.setBlockAndUpdate(newPos.above(y), state
                            .setValue(SIDE, x == 0 ? EnumSide.LEFT : EnumSide.RIGHT)
                            .setValue(BlockPSDDoorTianjinWide.WIDE_SIDE, x == 0 ? BlockPSDDoorTianjinWide.WideSide.LEFT : x == 1 ? BlockPSDDoorTianjinWide.WideSide.MIDDLE : BlockPSDDoorTianjinWide.WideSide.RIGHT));
                } else if (block instanceof BlockPSDAPGDoorBase) {
                    world.setBlockAndUpdate(newPos.above(y), state.setValue(SIDE, x == 0 ? EnumSide.LEFT : EnumSide.RIGHT));
                } else {
                    world.setBlockAndUpdate(newPos.above(y), state.setValue(SIDE_EXTENDED, EnumSide.SINGLE));
                }
            }

            if (!isAPG()) {
                if (block instanceof BlockFlagPSDTianjinBMT) {
                    world.setBlockAndUpdate(newPos.above(2), BlockPSDTopTianjinBMT.getActualState(world, newPos.above(2)).setValue(BlockPSDTopTianjinBMT.STYLE, BlockPSDTopTianjinBMT.EnumPSDType.BMT));
                } else if (block instanceof BlockFlagDoorWide) {
                    // 三扇宽门：吊板换成专用宽版方块，三块合起来画成一整块牌子
                    final BlockPos topPos = newPos.above(2);
                    world.setBlockAndUpdate(topPos, ziyue.tjmetro.BlockList.PSD_TOP_TIANJIN_WIDE.get().defaultBlockState());
                    world.setBlockAndUpdate(topPos, BlockPSDTopTianjin.getActualState(world, topPos, true).setValue(BlockPSDTopTianjin.STYLE, BlockPSDTopTianjin.EnumPSDType.STATION_NAME));
                } else if (block instanceof BlockFlagPSDTianjin) {
                    final BlockState topState = BlockPSDTopTianjin.getActualState(world, newPos.above(2), block instanceof BlockFlagPSDTianjinJinjing).setValue(BlockPSDTopTianjin.STYLE, BlockPSDTopTianjin.EnumPSDType.STATION_NAME);
                    world.setBlockAndUpdate(newPos.above(2), topState);
                }
            }
        }

        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }

    public boolean isAPG() {
        final Block block = getBlock();
        return block instanceof BlockFlagAPGTianjin || block instanceof BlockFlagAPGTianjinBMT || block instanceof BlockFlagAPGTianjinTRT;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        final Block block = getBlock();
        if (block instanceof BlockPSDAPGDoorBase) {
            tooltip.add(Text.translatable("tooltip.mtr.psd_apg_door").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        } else if (block instanceof BlockPSDAPGGlassEndBase) {
            tooltip.add(Text.translatable("tooltip.mtr.psd_apg_glass_end").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        } else if (block instanceof BlockPSDAPGGlassBase) {
            tooltip.add(Text.translatable("tooltip.mtr.psd_apg_glass").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }
}
