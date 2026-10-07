package ziyue.tjmetro.screen;

import mtr.client.ClientData;
import mtr.data.DataConverter;
import mtr.data.IGui;
import mtr.data.NameColorDataBase;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import mtr.screen.DashboardListSelectorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import ziyue.tjmetro.block.BlockStationNavigator;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.*;

/**
 * Station navigator screen for selecting routes.
 * Opens directly to a route selection list showing all available routes.
 */
public class StationNavigatorScreen extends ScreenMapper implements IGui
{
    private final BlockPos blockPos;
    private final Set<Long> selectedRoutes;

    public StationNavigatorScreen(BlockPos blockPos) {
        super(Text.literal(""));
        this.blockPos = blockPos;
        this.selectedRoutes = new HashSet<>();

        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(blockPos);
        if (blockEntity instanceof BlockStationNavigator.BlockEntity navigatorEntity) {
            selectedRoutes.addAll(navigatorEntity.getSelectedIds());
        }
    }

    @Override
    protected void init() {
        super.init();
        openRouteSelector();
    }

    @Override
    public void onClose() {
        PacketGuiServer.sendStationNavigatorConfigC2S(blockPos, selectedRoutes);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void openRouteSelector() {
        final List<NameColorDataBase> routesForList = new ArrayList<>();
        ClientData.ROUTES.forEach(route -> {
            routesForList.add(new DataConverter(route.color, route.name, route.color));
        });

        final Minecraft minecraft = Minecraft.getInstance();
        UtilitiesClient.setScreen(minecraft, new DashboardListSelectorScreen(
                this::onClose,
                routesForList,
                selectedRoutes,
                false,
                false));
    }
}
