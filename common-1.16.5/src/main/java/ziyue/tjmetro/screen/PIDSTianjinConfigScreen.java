package ziyue.tjmetro.screen;

import io.netty.buffer.Unpooled;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.DataConverter;
import mtr.data.IGui;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import mtr.RegistryClient;
import mtr.screen.DashboardListSelectorScreen;
import mtr.screen.WidgetBetterCheckbox;
import mtr.screen.WidgetBetterTextField;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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

/**
 * PIDS (Tianjin Metro) configuration screen.
 *
 * <p>Layout is a row-by-row port of the upstream Mapping API
 * {@code ziyue.tjmetro.mod.screen.PIDSTianjinConfigScreen}: every widget uses the same
 * {@code SQUARE_SIZE} / {@code TEXT_PADDING} / {@code TEXT_FIELD_PADDING} row formulas and the
 * same two-column split for the page number field and the advertisement button.</p>
 *
 * <p>Two intentional differences from upstream (see the porting report):</p>
 * <ul>
 *     <li>The two button labels keep this fork's descriptive translation keys
 *     ({@code gui.tjmetro.filter_platforms} / {@code gui.tjmetro.play_advertisements}) instead of
 *     upstream's copy-pasted vanilla {@code selectWorld.edit} placeholder.</li>
 *     <li>The fork-only "use real time" checkbox (no upstream equivalent: upstream's block entity
 *     has no such field) is appended on its own row below the upstream block, so every upstream
 *     control stays at its exact upstream coordinates.</li>
 * </ul>
 *
 * <p>1.18.2 replica: rendering goes through {@link PoseStack} instead of {@code GuiGraphics}.</p>
 */
public class PIDSTianjinConfigScreen extends ScreenMapper implements IGui {

    private final BlockPos blockPos;
    private final WidgetBetterCheckbox selectAllCheckbox;
    private final WidgetBetterCheckbox useRealTimeCheckbox;
    private final WidgetBetterCheckbox onlyFirstLastTrainCheckbox;
    private final WidgetBetterTextField displayPageInput;
    private Button filterButton;
    private Button categoryButton;
    private final Set<Long> filterPlatformIds;
    private final int displayPage;
    private final int filteredAdsCount;
    private boolean useRealTime;
    private boolean onlyFirstLastTrain;
    private boolean psdTopScreen;

    public PIDSTianjinConfigScreen(BlockPos blockPos) {
        super(Text.literal(""));
        this.blockPos = blockPos;
        this.filterPlatformIds = new HashSet<>();

        int displayPage = 0;
        int filteredAdsCount = 0;
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(blockPos);
        if (blockEntity instanceof BlockPIDSTianjin.TileEntityBlockPIDSTianjin) {
            BlockPIDSTianjin.TileEntityBlockPIDSTianjin pidsEntity = (BlockPIDSTianjin.TileEntityBlockPIDSTianjin) blockEntity;
            filterPlatformIds.addAll(pidsEntity.getPlatformIds());
            useRealTime = pidsEntity.isUseRealTime();
        psdTopScreen = blockEntity instanceof ziyue.tjmetro.block.BlockPIDSTianjinPsdTop.TileEntityBlockPIDSTianjinPsdTop;
        onlyFirstLastTrain = pidsEntity.isOnlyFirstLastTrain();
            displayPage = pidsEntity.getDisplayPage();
            filteredAdsCount = pidsEntity.getCategories().size();
        }
        this.displayPage = displayPage;
        this.filteredAdsCount = filteredAdsCount;

        selectAllCheckbox = new WidgetBetterCheckbox(0, 0, PANEL_WIDTH, SQUARE_SIZE, Text.translatable("gui.mtr.automatically_detect_nearby_platform"), selected -> {
            if (selected) {
                filterPlatformIds.clear();
            }
        });

        useRealTimeCheckbox = new WidgetBetterCheckbox(0, 0, PANEL_WIDTH, SQUARE_SIZE, Text.translatable("gui.tjmetro.use_real_time"), selected -> {
            useRealTime = selected;
        });

        onlyFirstLastTrainCheckbox = new WidgetBetterCheckbox(0, 0, PANEL_WIDTH, SQUARE_SIZE, Text.translatable("gui.tjmetro.only_first_last_train"), selected -> {
            onlyFirstLastTrain = selected;
        });

        displayPageInput = new WidgetBetterTextField(WidgetBetterTextField.TextFieldFilter.POSITIVE_INTEGER, "1", 3);
    }

    @Override
    protected void init() {
        super.init();

        selectAllCheckbox.setChecked(filterPlatformIds.isEmpty());
        IDrawing.setPositionAndWidth(selectAllCheckbox, SQUARE_SIZE, SQUARE_SIZE, PANEL_WIDTH);
        addDrawableChild(selectAllCheckbox);

        filterButton = UtilitiesClient.newButton(Text.translatable("gui.tjmetro.filter_platforms"), button -> openPlatformSelector());
        IDrawing.setPositionAndWidth(filterButton, SQUARE_SIZE, SQUARE_SIZE * 3, PANEL_WIDTH / 2);
        addDrawableChild(filterButton);

        displayPageInput.setValue(String.valueOf(displayPage + 1));
        IDrawing.setPositionAndWidth(displayPageInput, SQUARE_SIZE + TEXT_FIELD_PADDING / 2, SQUARE_SIZE * 5 + TEXT_FIELD_PADDING / 2, PANEL_WIDTH / 2 - TEXT_FIELD_PADDING);
        addDrawableChild(displayPageInput);

        categoryButton = UtilitiesClient.newButton(Text.translatable("gui.tjmetro.play_advertisements"), button -> openCategorySelector());
        IDrawing.setPositionAndWidth(categoryButton, SQUARE_SIZE + TEXT_FIELD_PADDING / 2, SQUARE_SIZE * 7 + TEXT_FIELD_PADDING, PANEL_WIDTH / 2);
        addDrawableChild(categoryButton);

        useRealTimeCheckbox.setChecked(useRealTime);
        IDrawing.setPositionAndWidth(useRealTimeCheckbox, SQUARE_SIZE, SQUARE_SIZE * 8 + TEXT_FIELD_PADDING * 2, PANEL_WIDTH);
        addDrawableChild(useRealTimeCheckbox);

        if (!psdTopScreen) {
            onlyFirstLastTrainCheckbox.setChecked(onlyFirstLastTrain);
            IDrawing.setPositionAndWidth(onlyFirstLastTrainCheckbox, SQUARE_SIZE, SQUARE_SIZE * 9 + TEXT_FIELD_PADDING * 2, PANEL_WIDTH);
            addDrawableChild(onlyFirstLastTrainCheckbox);
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        font.draw(matrices, Text.translatable("gui.mtr.filtered_platforms", selectAllCheckbox.selected() ? 0 : filterPlatformIds.size()).getString(), SQUARE_SIZE, SQUARE_SIZE * 2 + TEXT_PADDING, ARGB_WHITE);
        font.draw(matrices, Text.translatable("gui.mtr.display_page").getString(), SQUARE_SIZE, SQUARE_SIZE * 4 + TEXT_PADDING, ARGB_WHITE);
        font.draw(matrices, Text.translatable("gui.tjmetro.filtered_ads", filteredAdsCount).getString(), SQUARE_SIZE, SQUARE_SIZE * 6 + TEXT_PADDING * 2, ARGB_WHITE);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (selectAllCheckbox.selected()) {
            filterPlatformIds.clear();
        }
        int displayPage = 0;
        try {
            displayPage = Math.max(0, Integer.parseInt(displayPageInput.getValue()) - 1);
        } catch (Exception e) {
            TianjinMetro.LOGGER.error("", e);
        }
        sendConfigToServer(filterPlatformIds, displayPage, useRealTime, onlyFirstLastTrain);
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
        if (blockEntity instanceof BlockPIDSTianjin.TileEntityBlockPIDSTianjin) {
            BlockPIDSTianjin.TileEntityBlockPIDSTianjin pidsEntity = (BlockPIDSTianjin.TileEntityBlockPIDSTianjin) blockEntity;
            minecraft.setScreen(new CategorySelectorScreen(pidsEntity, this));
        }
    }

    private void sendConfigToServer(Set<Long> platformIds, int displayPage, boolean useRealTime, boolean onlyFirstLastTrain) {
        final net.minecraft.network.FriendlyByteBuf packet = new net.minecraft.network.FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(blockPos);
        packet.writeVarInt(platformIds.size());
        for (long id : platformIds) {
            packet.writeLong(id);
        }
        packet.writeInt(displayPage);
        packet.writeBoolean(useRealTime);
        packet.writeBoolean(onlyFirstLastTrain);
        RegistryClient.sendToServer(PACKET_UPDATE_PIDS_TIANJIN_CONFIG, packet);
    }
}
