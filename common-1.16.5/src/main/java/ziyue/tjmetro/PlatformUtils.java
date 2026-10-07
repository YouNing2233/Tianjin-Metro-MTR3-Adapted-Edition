package ziyue.tjmetro;

import me.shedaniel.architectury.platform.Platform;

public class PlatformUtils
{
    public static boolean isFabric() {
        return Platform.isFabric();
    }

    public static boolean isForge() {
        return Platform.isForge();
    }

    public static boolean isModLoaded(String modId) {
        return Platform.isModLoaded(modId);
    }
}