package ziyue.tjmetro;

import net.fabricmc.api.ClientModInitializer;

public class MainFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        TianjinMetroClient.init();
        // MTR's client-side receiver commit also runs before this mod registers its receivers.
        ziyue.tjmetro.fabric.TianjinNetworkClient.registerClient();
    }
}