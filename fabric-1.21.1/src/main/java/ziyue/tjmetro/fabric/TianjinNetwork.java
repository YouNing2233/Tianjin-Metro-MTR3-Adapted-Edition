package ziyue.tjmetro.fabric;

import mtr.MTRFabric;
import mtr.fabric.CompatPacket;
import mtr.fabric.CompatPacketRegistry;
import mtr.mappings.NetworkUtilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import ziyue.tjmetro.Reference;

/**
 * Workaround for the MTR 3.x (1.21.1) payload registration order.
 *
 * <p>On 1.21+, Fabric requires every custom payload type to be present in
 * {@link PayloadTypeRegistry} before it can be sent or received. MTR registers all of its
 * {@code mtr.fabric.CompatPacket} types inside {@code MTRFabric.onInitialize()} -- which the Fabric
 * loader runs <em>before</em> this mod's own initializer, because {@code tjmetro} depends on
 * {@code mtr}. Anything registered by the addon afterwards therefore never reaches Fabric's
 * registry, and encoding such a packet falls back to vanilla's {@code DiscardedPayload} codec,
 * throwing {@code ClassCastException: CompatPacket$Payload cannot be cast to DiscardedPayload}
 * and kicking the player.</p>
 *
 * <p>Call {@link #ensureTypes()} again after <em>each</em> batch of registrations: the S2C channels
 * (open screen packets) only enter MTR's registry during {@code TianjinMetroClient.init()}, i.e.
 * after the main initializer has already run. All calls are idempotent -- MTR's own entries are
 * already present and raise {@link IllegalArgumentException}, which is swallowed here.</p>
 *
 * @since 1.1.3
 */

public final class TianjinNetwork
{
    private TianjinNetwork() {
    }

    /**
     * Registers every {@code tjmetro:*} payload type in both directions. Safe to call repeatedly.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void ensureTypes() {
        final CompatPacketRegistry registry = MTRFabric.PACKET_REGISTRY;
        if (registry == null) {
            return;
        }

        for (final CompatPacket packet : registry.packets.values()) {
            if (!Reference.MOD_ID.equals(packet.id.getNamespace())) {
                continue;
            }

            // Payload types are needed on both sides of the connection, so register both.
            try {
                PayloadTypeRegistry.playS2C().register(packet.TYPE, (StreamCodec) packet.STREAM_CODEC);
            } catch (final IllegalArgumentException ignored) {
                // Already registered by MTR itself, or by an earlier call of this method.
            }
            try {
                PayloadTypeRegistry.playC2S().register(packet.TYPE, (StreamCodec) packet.STREAM_CODEC);
            } catch (final IllegalArgumentException ignored) {
                // Already registered by MTR itself, or by an earlier call of this method.
            }
        }
    }

    /**
     * Registers the payload types plus the C2S (server side) receivers.
     * Called from the common initializer, so it also runs on a dedicated server.
     */
    public static void registerServer() {
        ensureTypes();

        final CompatPacketRegistry registry = MTRFabric.PACKET_REGISTRY;
        if (registry == null) {
            return;
        }

        for (final CompatPacket packet : registry.packets.values()) {
            if (!Reference.MOD_ID.equals(packet.id.getNamespace())) {
                continue;
            }

            final NetworkUtilities.PacketCallback callback = registry.packetsC2S.get(packet.id);
            if (callback != null) {
                try {
                    ServerPlayNetworking.registerGlobalReceiver(packet.TYPE, (payload, context) -> callback.packetCallback(context.server(), context.player(), payload.buffer));
                } catch (final IllegalArgumentException ignored) {
                    // Already registered by MTR itself, or by an earlier call of this method.
                }
            }
        }
    }
}
