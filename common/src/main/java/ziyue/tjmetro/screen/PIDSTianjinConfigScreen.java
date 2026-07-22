package ziyue.tjmetro.screen;

import io.netty.buffer.Unpooled;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.data.DataConverter;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import mtr.RegistryClient;
import mtr.screen.DashboardListSelectorScreen;
import mtr.screen.WidgetBetterCheckbox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import ziyue.tjmetro.TianjinMetro;
import ziyue.tjmetro.block.BlockPIDSTianjin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ziyue.tjmetro.packet.IPacket.PACKET_UPDATE_PIDS_TIANJIN_CONFIG;

public class PIDSTianjinConfigScreen extends ScreenMapper implements IGui {

    private final BlockPos blockPos;
    private final WidgetBetterCheckbox selectAllCheckbox;
    private final Button filterButton;
    private final Button categoryButton;
    private final Set<Long> filterPlatformIds;

    public PIDSTianjinConfigScreen(BlockPos blockPos) {
        super(Text.literal(""));
        this.blockPos = blockPos;
        this.filterPlatformIds = new HashSet<>();

        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(blockPos);
        if (blockEntity instanceof BlockPIDSTianjin.TileEntityBlockPIDSTianjin pidsEntity) {
            filterPlatformIds.addAll(pidsEntity.getPlatformIds());
        }

        selectAllCheckbox = new WidgetBetterCheckbox(0, 0, PANEL_WIDTH, SQUARE_SIZE, Text.translatable("gui.mtr.automatically_detect_nearby_platform"), selected -> {
            if (selected) {
                filterPlatformIds.clear();
            }
        });

        filterButton = Button.builder(Component.translatable("gui.tjmetro.filter_platforms"), button -> openPlatformSelector()).size(PANEL_WIDTH, SQUARE_SIZE).build();
        categoryButton = Button.builder(Component.translatable("gui.tjmetro.play_advertisements"), button -> openCategorySelector()).size(PANEL_WIDTH, SQUARE_SIZE).build();
    }

    @Override
    protected void init() {
        super.init();

        selectAllCheckbox.setChecked(filterPlatformIds.isEmpty());
        IDrawing.setPositionAndWidth(selectAllCheckbox, SQUARE_SIZE, SQUARE_SIZE, PANEL_WIDTH);
        addDrawableChild(selectAllCheckbox);

        filterButton.setPosition(SQUARE_SIZE, SQUARE_SIZE * 2 + TEXT_FIELD_PADDING);
        addDrawableChild(filterButton);

        categoryButton.setPosition(SQUARE_SIZE, SQUARE_SIZE * 3 + TEXT_FIELD_PADDING);
        addDrawableChild(categoryButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        final Font textRenderer = Minecraft.getInstance().font;
        renderBackground(guiGraphics);
        guiGraphics.drawString(textRenderer, Text.translatable("gui.mtr.filtered_platforms", selectAllCheckbox.selected() ? 0 : filterPlatformIds.size()).getString(), SQUARE_SIZE, SQUARE_SIZE * 2 + TEXT_PADDING, ARGB_WHITE, false);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (selectAllCheckbox.selected()) {
            filterPlatformIds.clear();
        }
        sendConfigToServer(filterPlatformIds, 0);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void openPlatformSelector() {
        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, blockPos);
        if (station != null) {
            final List<mtr.data.NameColorDataBase> platformsForList = new ArrayList<>();
            final List<Platform> platforms = new ArrayList<>(ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id).values());
            Collections.sort(platforms);
            platforms.stream().map(platform -> new DataConverter(platform.id, platform.name + " " + IGui.mergeStations(ClientData.DATA_CACHE.requestPlatformIdToRoutes(platform.id).stream().map(route -> route.stationDetails.get(route.stationDetails.size() - 1).stationName).collect(Collectors.toList())), 0)).forEach(platformsForList::add);
            if (selectAllCheckbox.selected()) {
                filterPlatformIds.clear();
            }
            final Minecraft minecraft = Minecraft.getInstance();
            UtilitiesClient.setScreen(minecraft, new DashboardListSelectorScreen(() -> {
                UtilitiesClient.setScreen(minecraft, PIDSTianjinConfigScreen.this);
                selectAllCheckbox.setChecked(filterPlatformIds.isEmpty());
            }, platformsForList, filterPlatformIds, false, false));
        }
    }

    private void openCategorySelector() {
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(blockPos);
        if (blockEntity instanceof BlockPIDSTianjin.TileEntityBlockPIDSTianjin pidsEntity) {
            minecraft.setScreen(new CategorySelectorScreen(pidsEntity, this));
        }
    }

    private void sendConfigToServer(Set<Long> platformIds, int displayPage) {
        final net.minecraft.network.FriendlyByteBuf packet = new net.minecraft.network.FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(blockPos);
        packet.writeVarInt(platformIds.size());
        for (long id : platformIds) {
            packet.writeLong(id);
        }
        packet.writeInt(displayPage);
        RegistryClient.sendToServer(PACKET_UPDATE_PIDS_TIANJIN_CONFIG, packet);
    }
}