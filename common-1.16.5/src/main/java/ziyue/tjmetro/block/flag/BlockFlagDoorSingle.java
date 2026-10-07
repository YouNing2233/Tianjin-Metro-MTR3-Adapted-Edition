package ziyue.tjmetro.block.flag;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.BlockPSDAPGGlassEndBase;
import mtr.block.IBlock;
import mtr.mappings.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import static mtr.block.IBlock.HALF;
import static mtr.block.BlockPSDAPGDoorBase.END;
import static mtr.block.BlockPSDAPGDoorBase.UNLOCKED;

/**
 * Interface for single-leaf PSD/APG doors (Tianjin variant).
 * Provides shared logic for neighbor update, interaction, and break behavior.
 *
 * @since 1.1.6 - Ported from 4.0
 */
public interface BlockFlagDoorSingle
{
    boolean isLeft();

    static BlockState getStateForNeighborUpdate(BlockState state, LevelAccessor world, BlockPos pos) {
        final boolean end = world.getBlockState(pos.relative(IBlock.getSideDirection(state).getOpposite())).getBlock() instanceof BlockPSDAPGGlassEndBase;
        return state.setValue(END, end);
    }

    static InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = IBlock.getStatePropertySafe(state, UNLOCKED);
            for (int y = -1; y <= 1; y++) {
                final BlockState scanState = world.getBlockState(pos.above(y));
                if (state.is(scanState.getBlock())) {
                    world.setBlockAndUpdate(pos.above(y), scanState.setValue(UNLOCKED, !unlocked));
                }
            }
            player.displayClientMessage(unlocked ? Text.translatable("gui.mtr.psd_apg_door_locked") : Text.translatable("gui.mtr.psd_apg_door_unlocked"), true);
        });
    }

    static void onBreak(Level world, BlockPos pos, BlockState state, Player player) {
        final BlockPos offsetPos = IBlock.getStatePropertySafe(state, HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER ? pos.below() : pos.above();
        IBlock.onBreakCreative(world, player, offsetPos);
        world.destroyBlock(pos, false, player);
    }
}
