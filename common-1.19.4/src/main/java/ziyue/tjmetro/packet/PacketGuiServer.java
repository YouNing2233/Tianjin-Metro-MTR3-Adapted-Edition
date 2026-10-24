package ziyue.tjmetro.packet;

import io.netty.buffer.Unpooled;
import mtr.Registry;
import mtr.RegistryClient;
import mtr.block.BlockRouteSignBase;
import mtr.data.RailwayData;
import mtr.data.RailwayDataLoggingModule;
import mtr.data.SerializedDataBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import ziyue.tjmetro.block.BlockPIDSTianjin;
import ziyue.tjmetro.block.BlockRailwaySignSingle;
import ziyue.tjmetro.block.BlockRailwaySignTianjinDouble;
import ziyue.tjmetro.block.BlockRailwaySignWallDouble;
import ziyue.tjmetro.block.BlockStationNameEntranceTianjin;
import ziyue.tjmetro.block.BlockStationNavigator;
import ziyue.tjmetro.block.base.BlockCustomColorBase;
import ziyue.tjmetro.block.base.BlockCustomContentBlockBase;
import ziyue.tjmetro.block.base.BlockRailwaySignBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static ziyue.tjmetro.packet.IPacket.*;

/**
 * @since beta-1
 */

public class PacketGuiServer
{
    public static void openCustomContentScreenS2C(ServerPlayer player, BlockPos blockPos) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(blockPos);
        Registry.sendToPlayer(player, PACKET_OPEN_CUSTOM_CONTENT_SCREEN, packet);
    }

    public static void receiveCustomContentC2S(MinecraftServer minecraftServer, ServerPlayer player, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        final String content = packet.readUtf(SerializedDataBase.PACKET_STRING_READ_LENGTH);
        minecraftServer.execute(() -> {
            final BlockEntity entity = player.level.getBlockEntity(pos);
            if (entity instanceof BlockCustomContentBlockBase.CustomContentBlockEntity entity1) {
                entity1.setData(content);
            }
        });
    }

    public static void openCustomColorScreenS2C(ServerPlayer player, BlockPos blockPos) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(blockPos);
        Registry.sendToPlayer(player, PACKET_OPEN_CUSTOM_COLOR_SCREEN, packet);
    }

    public static void receiveCustomColorC2S(MinecraftServer minecraftServer, ServerPlayer player, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        final int color = packet.readInt();
        minecraftServer.execute(() -> {
            final BlockEntity entity = player.level.getBlockEntity(pos);
            if (entity instanceof BlockCustomColorBase.CustomColorBlockEntity entity1) {
                entity1.setData(color);
            }
        });
    }

    public static void openRailwaySignScreenS2C(ServerPlayer player, BlockPos signPos) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        Registry.sendToPlayer(player, PACKET_OPEN_RAILWAY_SIGN_SCREEN, packet);
    }

    public static void sendSignIdsC2S(BlockPos signPos, Set<Long> selectedIds, String[] signIds) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        packet.writeInt(selectedIds.size());
        selectedIds.forEach(packet::writeLong);
        packet.writeInt(signIds.length);
        for (final String signType : signIds) {
            packet.writeUtf(signType == null ? "" : signType);
        }
        RegistryClient.sendToServer(PACKET_SIGN_TYPES, packet);
    }

    public static void receiveSignIdsC2S(MinecraftServer minecraftServer, ServerPlayer player, FriendlyByteBuf packet) {
        final BlockPos signPos = packet.readBlockPos();
        final int selectedIdsLength = packet.readInt();
        final Set<Long> selectedIds = new HashSet<>();
        for (int i = 0; i < selectedIdsLength; i++) {
            selectedIds.add(packet.readLong());
        }
        final int signLength = packet.readInt();
        final String[] signIds = new String[signLength];
        for (int i = 0; i < signLength; i++) {
            final String signId = packet.readUtf(SerializedDataBase.PACKET_STRING_READ_LENGTH);
            signIds[i] = signId.isEmpty() ? null : signId;
        }

        minecraftServer.execute(() -> {
            final BlockEntity entity = player.level.getBlockEntity(signPos);
            if (entity instanceof BlockRailwaySignBase.TileEntityRailwaySign entity1) {
                setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setData(selectedIds, signIds), entity1);
            } else if (entity instanceof BlockRouteSignBase.TileEntityRouteSignBase entity1) {
                final long platformId = selectedIds.isEmpty() ? 0 : (long) selectedIds.toArray()[0];
                final BlockEntity entityAbove = player.level.getBlockEntity(signPos.above());
                if (entityAbove instanceof BlockRouteSignBase.TileEntityRouteSignBase entity2) {
                    setTileEntityDataAndWriteUpdate(player, entity3 -> entity3.setPlatformId(platformId), entity2, entity1);
                } else {
                    setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setPlatformId(platformId), entity1);
                }
            } else if (entity instanceof BlockStationNameEntranceTianjin.TileEntityStationNameEntranceTianjin entity1) {
                final List<Long> selectedIdsList = selectedIds.stream().toList();
                setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setData(selectedIdsList.isEmpty() ? -1 : selectedIdsList.get(0)), entity1);
            }
        });
    }

    public static void openRailwaySignDoubleScreenS2C(ServerPlayer player, BlockPos signPos) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        Registry.sendToPlayer(player, PACKET_OPEN_RAILWAY_SIGN_DOUBLE_SCREEN, packet);
    }

    public static void sendSignIdsDoubleC2S(BlockPos signPos, List<Set<Long>> selectedIds, String[][] signIds) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(signPos);
        selectedIds.forEach(line -> {
            packet.writeInt(line.size());
            line.forEach(packet::writeLong);
        });
        packet.writeInt(signIds[0].length);
        for (int i = 0; i < 2; i++) {
            for (final String signType : signIds[i]) {
                packet.writeUtf(signType == null ? "" : signType);
            }
        }
        RegistryClient.sendToServer(PACKET_SIGN_TYPES_DOUBLE, packet);
    }

    public static void receiveSignIdsDoubleC2S(MinecraftServer minecraftServer, ServerPlayer player, FriendlyByteBuf packet) {
        final BlockPos signPos = packet.readBlockPos();
        final List<Set<Long>> selectedIds = new ArrayList<>();
        selectedIds.add(new HashSet<>());
        selectedIds.add(new HashSet<>());
        for (int i = 0; i < 2; i++) {
            final int selectedIdsLength = packet.readInt();
            for (int j = 0; j < selectedIdsLength; j++) {
                selectedIds.get(i).add(packet.readLong());
            }
        }
        final int signLength = packet.readInt();
        final String[][] signIds = new String[2][signLength];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < signLength; j++) {
                final String signId = packet.readUtf(SerializedDataBase.PACKET_STRING_READ_LENGTH);
                signIds[i][j] = signId.isEmpty() ? null : signId;
            }
        }

        minecraftServer.execute(() -> {
            final BlockEntity entity = player.level.getBlockEntity(signPos);
            if (entity instanceof BlockRailwaySignWallDouble.TileEntityRailwaySignWallDouble sign) {
                setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setData(selectedIds, signIds), sign);
            } else if (entity instanceof BlockRailwaySignTianjinDouble.TileEntityRailwaySignTianjinDouble sign1) {
                setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setData(selectedIds, signIds), sign1);
            }
        });
    }


    @SafeVarargs
    public static <T extends BlockEntityMapper> void setTileEntityDataAndWriteUpdate(ServerPlayer player, Consumer<T> setData, T... entities) {
        final RailwayData railwayData = RailwayData.getInstance(player.level);

        if (railwayData != null && entities.length > 0) {
            final CompoundTag compoundTagOld = new CompoundTag();
            entities[0].writeCompoundTag(compoundTagOld);

            BlockPos blockPos = null;
            long posLong = 0;
            for (final T entity : entities) {
                setData.accept(entity);
                final BlockPos entityPos = entity.getBlockPos();
                if (blockPos == null || entityPos.asLong() > posLong) {
                    blockPos = entityPos;
                    posLong = entityPos.asLong();
                }
            }

            final CompoundTag compoundTagNew = new CompoundTag();
            entities[0].writeCompoundTag(compoundTagNew);

            railwayData.railwayDataLoggingModule.addEvent(player, entities[0].getClass(), RailwayDataLoggingModule.getData(compoundTagOld), RailwayDataLoggingModule.getData(compoundTagNew), blockPos);
        }
    }

    public static void openPIDSTianjinScreenS2C(ServerPlayer player, BlockPos pos) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(pos);
        Registry.sendToPlayer(player, PACKET_OPEN_PIDS_TIANJIN_SCREEN, packet);
    }

    public static void receivePIDSTianjinConfigC2S(MinecraftServer minecraftServer, ServerPlayer player, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        final int platformIdCount = packet.readVarInt();
        final Set<Long> platformIds = new HashSet<>();
        for (int i = 0; i < platformIdCount; i++) {
            platformIds.add(packet.readLong());
        }
        final int displayPage = packet.readInt();
        final boolean useRealTime = packet.readBoolean();

        minecraftServer.execute(() -> {
            final BlockEntity entity = player.level.getBlockEntity(pos);
            if (entity instanceof BlockPIDSTianjin.TileEntityBlockPIDSTianjin pidsEntity) {
                pidsEntity.setData(new String[0], new boolean[0], platformIds, displayPage, useRealTime);
            }
        });
    }

    public static void receivePIDSAdsConfigC2S(MinecraftServer minecraftServer, ServerPlayer player, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        final int categoryCount = packet.readVarInt();
        final BlockPIDSTianjin.LongAdderList categories = new BlockPIDSTianjin.LongAdderList();
        for (int i = 0; i < categoryCount; i++) {
            categories.add(packet.readLong());
        }

        minecraftServer.execute(() -> {
            final BlockEntity entity = player.level.getBlockEntity(pos);
            if (entity instanceof BlockPIDSTianjin.TileEntityBlockPIDSTianjin pidsEntity) {
                pidsEntity.setCategories(categories);
            }
        });
    }

    public static void openStationNavigatorScreenS2C(ServerPlayer player, BlockPos pos) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(pos);
        Registry.sendToPlayer(player, PACKET_OPEN_STATION_NAVIGATOR_SCREEN, packet);
    }

    public static void sendStationNavigatorConfigC2S(BlockPos pos, Set<Long> selectedRoutes) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(pos);
        packet.writeInt(selectedRoutes.size());
        selectedRoutes.forEach(packet::writeLong);
        RegistryClient.sendToServer(PACKET_STATION_NAVIGATOR_CONFIG, packet);
    }

    public static void receiveStationNavigatorConfigC2S(MinecraftServer minecraftServer, ServerPlayer player, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        final int selectedRoutesLength = packet.readInt();
        final Set<Long> selectedRoutes = new HashSet<>();
        for (int i = 0; i < selectedRoutesLength; i++) {
            selectedRoutes.add(packet.readLong());
        }

        minecraftServer.execute(() -> {
            final BlockEntity entity = player.level.getBlockEntity(pos);
            if (entity instanceof BlockStationNavigator.BlockEntity entity1) {
                setTileEntityDataAndWriteUpdate(player, entity2 -> entity2.setData(selectedRoutes), entity1);
            }
        });
    }

    /* 1.1.3 - Ported from 1.1.3 - Single-block railway sign */

    public static void openRailwaySignSingleScreenS2C(ServerPlayer player, BlockPos pos, int index) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(pos);
        packet.writeInt(index);
        Registry.sendToPlayer(player, PACKET_OPEN_RAILWAY_SIGN_SINGLE_SCREEN, packet);
    }

    public static void sendRailwaySignSingleConfigC2S(BlockPos pos, int index, String signId) {
        final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(pos);
        packet.writeInt(index);
        packet.writeUtf(signId == null ? "" : signId);
        RegistryClient.sendToServer(PACKET_RAILWAY_SIGN_SINGLE_CONFIG, packet);
    }

    public static void receiveRailwaySignSingleConfigC2S(MinecraftServer minecraftServer, ServerPlayer player, FriendlyByteBuf packet) {
        final BlockPos pos = packet.readBlockPos();
        final int index = packet.readInt();
        final String rawSignId = packet.readUtf(SerializedDataBase.PACKET_STRING_READ_LENGTH);
        final String signId = rawSignId.isEmpty() ? null : rawSignId;

        minecraftServer.execute(() -> {
            final BlockEntity entity = player.level.getBlockEntity(pos);
            if (entity instanceof BlockRailwaySignSingle.BlockEntity entity1) {
                // Applied directly: setTileEntityDataAndWriteUpdate() only runs the setter when the level
                // already has RailwayData, which silently dropped the sign id.
                entity1.setData(index, signId);
            }
        });
    }
}
