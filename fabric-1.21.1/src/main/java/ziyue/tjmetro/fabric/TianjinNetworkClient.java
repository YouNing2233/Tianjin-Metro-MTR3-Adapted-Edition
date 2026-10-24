package ziyue.tjmetro.fabric;

import mtr.MTRFabric;
import mtr.fabric.CompatPacket;
import mtr.fabric.CompatPacketRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import ziyue.tjmetro.Reference;

import java.util.function.Consumer;

/**
 * Client half of {@link TianjinNetwork}: registers the S2C payload types (the open screen channels
 * only exist in MTR's registry by now, after {@code TianjinMetroClient.init()}) and the S2C
 * receivers that MTR's own {@code commitClient()} pass missed.
 *
 * @see TianjinNetwork
 * @since 1.1.3
 */

public final class TianjinNetworkClient
{
    private TianjinNetworkClient() {
    }

    public static void registerClient() {
        // Must run first: without the payload types in Fabric's registry, both the receiver
        // registration below and every outgoing packet would throw.
        TianjinNetwork.ensureTypes();

        final CompatPacketRegistry registry = MTRFabric.PACKET_REGISTRY;
        if (registry == null) {
            return;
        }

        for (final CompatPacket packet : registry.packets.values()) {
            if (!Reference.MOD_ID.equals(packet.id.getNamespace())) {
                continue;
            }

            final Consumer<FriendlyByteBuf> consumer = registry.packetsS2C.get(packet.id);
            if (consumer != null) {
                try {
                    ClientPlayNetworking.registerGlobalReceiver(packet.TYPE, (payload, context) -> consumer.accept(payload.buffer));
                } catch (final IllegalArgumentException ignored) {
                    // Already registered by MTR itself, or by an earlier call of this method.
                }
            }
        }
    }
}
