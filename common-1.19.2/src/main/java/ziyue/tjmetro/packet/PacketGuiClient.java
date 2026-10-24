package ziyue.tjmetro.packet;

import io.netty.buffer.Unpooled;
import mtr.RegistryClient;
import mtr.mappings.UtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import ziyue.tjmetro.block.BlockStationNavigator;
import ziyue.tjmetro.block.base.BlockCustomColorBase;
import ziyue.tjmetro.block.base.BlockCustomContentBlockBase;
import ziyue.tjmetro.screen.ColorPickerScreen;
import ziyue.tjmetro.screen.CustomContentScreen;
import ziyue.tjmetro.screen.PIDSTianjinConfigScreen;
import ziyue.tjmetro.screen.RailwaySignDoubleScreen;
import ziyue.tjmetro.screen.RailwaySignScreen;
import ziyue.tjmetro.screen.RailwaySignSingleScreen;
import ziyue.tjmetro.screen.StationNavigatorScreen;

import static ziyue.tjmetro.packet.IPacket.*;

/**
 * @since beta-1
 */

public class PacketGuiClient
{
    public static void openCustomContentScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (minecraftClient.level != null && !(minecraftClient.screen instanceof CustomContentScreen)) {
                final BlockEntity entity = minecraftClient.level.getBlockEntity(pos);
                if (entity instanceof BlockCustomContentBlockBase.CustomContentBlockEntity) {
                    UtilitiesClient.setScreen(minecraftClient, new CustomContentScreen(pos));
                }
            }
        });
    }

    public static void sendCustomContentC2S(BlockPos pos, String content) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(pos);
        packet.writeUtf(content);
        RegistryClient.sendToServer(PACKET_UPDATE_CUSTOM_CONTENT, packet);
    }


    public static void openCustomColorScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (minecraftClient.level != null && !(minecraftClient.screen instanceof CustomContentScreen)) {
                final BlockEntity entity = minecraftClient.level.getBlockEntity(pos);
                if (entity instanceof BlockCustomColorBase.CustomColorBlockEntity entity1) {
                    UtilitiesClient.setScreen(minecraftClient, new ColorPickerScreen(pos, entity1.color));
                }
            }
        });
    }

    public static void sendCustomColorC2S(BlockPos pos, int color) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(pos);
        packet.writeInt(color);
        RegistryClient.sendToServer(PACKET_UPDATE_CUSTOM_COLOR, packet);
    }

    public static void openRailwaySignScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (!(minecraftClient.screen instanceof RailwaySignScreen)) {
                UtilitiesClient.setScreen(minecraftClient, new RailwaySignScreen(pos));
            }
        });
    }

    public static void openRailwaySignDoubleScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (!(minecraftClient.screen instanceof RailwaySignScreen)) {
                UtilitiesClient.setScreen(minecraftClient, new RailwaySignDoubleScreen(pos));
            }
        });
    }

    public static void openPIDSTianjinScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (!(minecraftClient.screen instanceof PIDSTianjinConfigScreen)) {
                UtilitiesClient.setScreen(minecraftClient, new PIDSTianjinConfigScreen(pos));
            }
        });
    }

    public static void openStationNavigatorScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        minecraftClient.execute(() -> {
            if (minecraftClient.level != null && !(minecraftClient.screen instanceof StationNavigatorScreen)) {
                final BlockEntity entity = minecraftClient.level.getBlockEntity(pos);
                if (entity instanceof BlockStationNavigator.BlockEntity) {
                    UtilitiesClient.setScreen(minecraftClient, new StationNavigatorScreen(pos));
                }
            }
        });
    }

    /* 1.1.3 - Ported from 1.1.3 - Single-block railway sign */

    public static void openRailwaySignSingleScreenS2C(Minecraft minecraftClient, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        final int index = packet.readInt();
        minecraftClient.execute(() -> {
            if (minecraftClient.level != null && !(minecraftClient.screen instanceof RailwaySignSingleScreen)) {
                UtilitiesClient.setScreen(minecraftClient, new RailwaySignSingleScreen(pos, index));
            }
        });
    }
}
