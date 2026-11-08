package ziyue.tjmetro.block;

import mtr.block.BlockTicketBarrier;
import mtr.mappings.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import ziyue.tjmetro.data.IGuiExtends;

import java.util.List;

/**
 * Ticket barrier for <i>outbound transfer</i>: the player may leave the paid area without ending
 * their current journey, so the next entrance continues it. Ported from MTR4
 * {@code ziyue.tjmetro.mod.block.BlockTicketBarrierOutboundTransfer} (1.1.3).
 *
 * @author ZiYueCommentary
 * @see BlockTicketBarrier
 * @since 1.1.3
 */
public class BlockTicketBarrierOutboundTransfer extends BlockTicketBarrier
{
    public BlockTicketBarrierOutboundTransfer() {
        super(false);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
        IGuiExtends.addHoldShiftTooltip(tooltip, Text.translatable("tooltip.tjmetro.outbound_transfer"), true);
    }
}