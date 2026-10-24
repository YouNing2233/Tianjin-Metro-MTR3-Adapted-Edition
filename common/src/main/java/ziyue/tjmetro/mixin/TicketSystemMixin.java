package ziyue.tjmetro.mixin;

import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.data.TicketSystem;
import mtr.mappings.Text;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.tjmetro.block.BlockTicketBarrierOutboundTransfer;

/**
 * Outbound transfer fare rule, behaviourally ported from the MTR4 Tianjin Metro addon
 * (ziyue.tjmetro.mod.mixin.TicketSystemMixin, 1.1.3).
 * <p>
 * Everything is injected into the public passThrough method instead of the private onEnter, because
 * passThrough has an identical descriptor on every MTR 3.x build we target while onEnter changed its
 * score type in 1.21 (Score became ScoreAccess). A single stable injector also means the rule can
 * never fail to apply and break ticket barriers outright.
 * <p>
 * Behaviour, matching the original:
 * <ul>
 *   <li>Leaving through an outbound-transfer barrier with a journey in progress records the station,
 *       the current minute and the zone the player entered at, prints
 *       gui.tjmetro.outbound_transfer_exit and opens the barrier without charging. The entry zone is
 *       deliberately left untouched, exactly like the original.</li>
 *   <li>Leaving with no journey prints gui.tjmetro.outbound_no_journey, plays the failure sound and
 *       keeps the barrier shut.</li>
 *   <li>Entering the same station within ten minutes continues the journey: the stock entrance logic
 *       is skipped so the entry zone survives, and gui,tjmetro.outbound_transfer_success is shown.</li>
 *   <li>Entering elsewhere or after ten minutes charges the previous journey (base fare plus the
 *       zone delta) or the 500 evasion fine, prints outbound_transfer_expired /
 *       outbound_transfer_invalid and starts a fresh journey.</li>
 * </ul>
 *
 * @author ZiYueCommentary
 * @since 1.1.3
 */
@Mixin(TicketSystem.class)
public abstract class TicketSystemMixin
{
    private static final String ENTRY_ZONE_OBJECTIVE = "mtr_entry_zone";
    private static final String BALANCE_OBJECTIVE = "mtr_balance";
    private static final String TRANSFER_STATION_OBJECTIVE = "tjmetro_outbound_transfer_station";
    private static final String TRANSFER_TIME_OBJECTIVE = "tjmetro_outbound_transfer_time";
    private static final String TRANSFER_ZONE_OBJECTIVE = "tjmetro_outbound_transfer_zone";

    private static final int TRANSFER_MINUTES = 10;
    private static final int BASE_FARE = 5;
    private static final int EVASION_FINE = 500;

    @Inject(method = "passThrough", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void tjmetro$passThrough(Level world, BlockPos pos, Player player, boolean isEntrance, boolean isConcessionary, SoundEvent entrySound, SoundEvent entrySoundConcessionary, SoundEvent exitSound, SoundEvent exitSoundConcessionary, SoundEvent failSound, boolean remindIfNoRecord, CallbackInfoReturnable<TicketSystem.EnumTicketBarrierOpen> cir) {
        if (world == null || player == null || world.isClientSide()) return;

        try {
            if (isEntrance) {
                tjmetro$handleEntrance(world, pos, player, isConcessionary, entrySound, entrySoundConcessionary, cir);
            } else if (world.getBlockState(pos).getBlock() instanceof BlockTicketBarrierOutboundTransfer) {
                tjmetro$handleTransferExit(world, pos, player, isConcessionary, exitSound, exitSoundConcessionary, failSound, cir);
            }
        } catch (Exception ignored) {
        }
    }

    private static void tjmetro$handleTransferExit(Level world, BlockPos pos, Player player, boolean isConcessionary, SoundEvent exitSound, SoundEvent exitSoundConcessionary, SoundEvent failSound, CallbackInfoReturnable<TicketSystem.EnumTicketBarrierOpen> cir) {
        final Station station = stationAt(world, pos);
        if (station == null) return;

        final int entryZoneScore = getScore(world, player, ENTRY_ZONE_OBJECTIVE);
        if (entryZoneScore != 0) {
            setScore(world, player, TRANSFER_STATION_OBJECTIVE, (int) station.id);
            setScore(world, player, TRANSFER_TIME_OBJECTIVE, nowMinutes());
            // mtr_entry_zone stores encodeZone(zone), i.e. zone + 1 for non-negative zones.
            setScore(world, player, TRANSFER_ZONE_OBJECTIVE, decodeZone(entryZoneScore));
            player.displayClientMessage(Text.translatable("gui.tjmetro.outbound_transfer_exit"), true);
            // Cancelling passThrough also skips its own sound code, so play it here.
            world.playSound(null, pos, isConcessionary ? exitSoundConcessionary : exitSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            cir.setReturnValue(isConcessionary ? TicketSystem.EnumTicketBarrierOpen.OPEN_CONCESSIONARY : TicketSystem.EnumTicketBarrierOpen.OPEN);
        } else {
            player.displayClientMessage(Text.translatable("gui.tjmetro.outbound_no_journey"), true);
            if (failSound != null) {
                world.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            cir.setReturnValue(TicketSystem.EnumTicketBarrierOpen.CLOSED);
        }
        cir.cancel();
    }

    private static void tjmetro$handleEntrance(Level world, BlockPos pos, Player player, boolean isConcessionary, SoundEvent entrySound, SoundEvent entrySoundConcessionary, CallbackInfoReturnable<TicketSystem.EnumTicketBarrierOpen> cir) {
        if (getScore(world, player, TRANSFER_TIME_OBJECTIVE) == 0) return;

        final Station station = stationAt(world, pos);
        if (station == null) return;

        final int now = nowMinutes();
        final boolean expired = now - getScore(world, player, TRANSFER_TIME_OBJECTIVE) > TRANSFER_MINUTES;
        final boolean sameStation = getScore(world, player, TRANSFER_STATION_OBJECTIVE) == (int) station.id;

        if (sameStation && !expired) {
            clearTransfer(world, player);
            player.displayClientMessage(Text.translatable("gui,tjmetro.outbound_transfer_success"), true);
            world.playSound(null, pos, isConcessionary ? entrySoundConcessionary : entrySound, SoundSource.BLOCKS, 1.0F, 1.0F);
            // Skipping the stock entrance logic keeps the original entry zone, so the journey
            // continues across the transfer instead of starting over.
            cir.setReturnValue(isConcessionary ? TicketSystem.EnumTicketBarrierOpen.OPEN_CONCESSIONARY : TicketSystem.EnumTicketBarrierOpen.OPEN);
            cir.cancel();
            return;
        }

        final boolean entered = getScore(world, player, ENTRY_ZONE_OBJECTIVE) != 0;
        final int fare = BASE_FARE + Math.abs(station.zone - getScore(world, player, TRANSFER_ZONE_OBJECTIVE));
        final int finalFare = entered ? fare : EVASION_FINE;

        setScore(world, player, ENTRY_ZONE_OBJECTIVE, 0);
        clearTransfer(world, player);
        setScore(world, player, BALANCE_OBJECTIVE, getScore(world, player, BALANCE_OBJECTIVE) - finalFare);

        player.displayClientMessage(Text.translatable(expired ? "gui.tjmetro.outbound_transfer_expired" : "gui.tjmetro.outbound_transfer_invalid", finalFare), true);
        world.playSound(null, pos, isConcessionary ? entrySoundConcessionary : entrySound, SoundSource.BLOCKS, 1.0F, 1.0F);
        cir.setReturnValue(isConcessionary ? TicketSystem.EnumTicketBarrierOpen.OPEN_CONCESSIONARY : TicketSystem.EnumTicketBarrierOpen.OPEN);
        cir.cancel();
    }

    private static Station stationAt(Level world, BlockPos pos) {
        final RailwayData railwayData = RailwayData.getInstance(world);
        return railwayData == null ? null : RailwayData.getStation(railwayData.stations, railwayData.dataCache, pos);
    }

    private static void clearTransfer(Level world, Player player) {
        setScore(world, player, TRANSFER_STATION_OBJECTIVE, 0);
        setScore(world, player, TRANSFER_TIME_OBJECTIVE, 0);
        setScore(world, player, TRANSFER_ZONE_OBJECTIVE, 0);
    }

    private static int decodeZone(int encodedZone) {
        return encodedZone > 0 ? encodedZone - 1 : encodedZone;
    }

    private static int nowMinutes() {
        return (int) (System.currentTimeMillis() / 60000L);
    }

    private static int getScore(Level world, Player player, String objective) {
        try {
            return TicketSystem.getPlayerScore(world, player, objective).getScore();
        } catch (Exception e) {
            return 0;
        }
    }

    private static void setScore(Level world, Player player, String objective, int value) {
        try {
            TicketSystem.getPlayerScore(world, player, objective).setScore(value);
        } catch (Exception ignored) {
        }
    }
}