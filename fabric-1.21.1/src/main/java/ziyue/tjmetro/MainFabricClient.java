package ziyue.tjmetro;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import ziyue.tjmetro.render.RenderSeat;

public class MainFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        TianjinMetroClient.init();
        // The bench seat entity is ours on 1.21.1 (NeoMTR removed MTR's own seat), so it needs a renderer.
        EntityRendererRegistry.register(EntityTypes.SEAT.get(), RenderSeat::new);
        // MTR's client-side receiver commit also runs before this mod registers its receivers.
        ziyue.tjmetro.fabric.TianjinNetworkClient.registerClient();
    }
}