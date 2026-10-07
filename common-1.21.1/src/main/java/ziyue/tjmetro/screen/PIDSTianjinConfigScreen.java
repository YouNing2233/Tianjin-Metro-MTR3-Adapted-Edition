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
import mtr.screen.WidgetBetterTextField;
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
 */
public class PIDSTianjinConfigScreen extends ScreenMapper implements IGui {

    private final BlockPos blockPos;
    private final WidgetBetterCheckbox selectAllCheckbox;
    private final WidgetBetterCheckbox useRealTimeCheckbox;
    private final Button filterButton;
    private final Button categoryButton;
    private final WidgetBetterTextField displayPageInput;
    private final Set<Long> filterPlatformIds;
    private final int displayPage;
    private final int filteredAdsCount;
    private boolean useRealTime;

    public PIDSTianjinConfigScreen(BlockPos blockPos) {
        super(Text.literal(""));
        this.blockPos = blockPos;
        this.filterPlatformIds = new HashSet<>();

        int displayPage = 0;
        int filteredAdsCount = 0;
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(blockPos);
        if (blockEntity instanceof BlockPIDSTianjin.TileEntityBlockPIDSTianjin pidsEntity) {
            filterPlatformIds.addAll(pidsEntity.getPlatformIds());
            useRealTime = pidsEntity.isUseRealTime();
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

        filterButton = Button.builder(Component.translatable("gui.tjmetro.filter_platforms"), button -> openPlatformSelector()).size(PANEL_WIDTH / 2, SQUARE_SIZE).build();
        categoryButton = Button.builder(Component.translatable("gui.tjmetro.play_advertisements"), button -> openCategorySelector()).size(PANEL_WIDTH / 2, SQUARE_SIZE).build();
        displayPageInput = new WidgetBetterTextField(WidgetBetterTextField.TextFieldFilter.POSITIVE_INTEGER, "1", 3);
    }

    @Override
    protected void init() {
        super.init();

        selectAllCheckbox.setChecked(filterPlatformIds.isEmpty());
        IDrawing.setPositionAndWidth(selectAllCheckbox, SQUARE_SIZE, SQUARE_SIZE, PANEL_WIDTH);
        addDrawableChild(selectAllCheckbox);

        IDrawing.setPositionAndWidth(filterButton, SQUARE_SIZE, SQUARE_SIZE * 3, PANEL_WIDTH / 2);
        addDrawableChild(filterButton);

        displayPageInput.setValue(String.valueOf(displayPage + 1));
        IDrawing.setPositionAndWidth(displayPageInput, SQUARE_SIZE + TEXT_FIELD_PADDING / 2, SQUARE_SIZE * 5 + TEXT_FIELD_PADDING / 2, PANEL_WIDTH / 2 - TEXT_FIELD_PADDING);
        addDrawableChild(displayPageInput);

        IDrawing.setPositionAndWidth(categoryButton, SQUARE_SIZE + TEXT_FIELD_PADDING / 2, SQUARE_SIZE * 7 + TEXT_FIELD_PADDING, PANEL_WIDTH / 2);
        addDrawableChild(categoryButton);

        useRealTimeCheckbox.setChecked(useRealTime);
        IDrawing.setPositionAndWidth(useRealTimeCheckbox, SQUARE_SIZE, SQUARE_SIZE * 8 + TEXT_FIELD_PADDING * 2, PANEL_WIDTH);
        addDrawableChild(useRealTimeCheckbox);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        final Font textRenderer = Minecraft.getInstance().font;
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawString(textRenderer, Text.translatable("gui.mtr.filtered_platforms", selectAllCheckbox.selected() ? 0 : filterPlatformIds.size()).getString(), SQUARE_SIZE, SQUARE_SIZE * 2 + TEXT_PADDING, ARGB_WHITE, false);
        guiGraphics.drawString(textRenderer, Text.translatable("gui.mtr.display_page").getString(), SQUARE_SIZE, SQUARE_SIZE * 4 + TEXT_PADDING, ARGB_WHITE, false);
        guiGraphics.drawString(textRenderer, Text.translatable("gui.tjmetro.filtered_ads", filteredAdsCount).getString(), SQUARE_SIZE, SQUARE_SIZE * 6 + TEXT_PADDING * 2, ARGB_WHITE, false);
        super.render(guiGraphics, mouseX, mouseY, delta);
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
        sendConfigToServer(filterPlatformIds, displayPage, useRealTime);
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

    private void sendConfigToServer(Set<Long> platformIds, int displayPage, boolean useRealTime) {
        final net.minecraft.network.FriendlyByteBuf packet = new net.minecraft.network.FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(blockPos);
        packet.writeVarInt(platformIds.size());
        for (long id : platformIds) {
            packet.writeLong(id);
        }
        packet.writeInt(displayPage);
        packet.writeBoolean(useRealTime);
        RegistryClient.sendToServer(PACKET_UPDATE_PIDS_TIANJIN_CONFIG, packet);
    }
}
