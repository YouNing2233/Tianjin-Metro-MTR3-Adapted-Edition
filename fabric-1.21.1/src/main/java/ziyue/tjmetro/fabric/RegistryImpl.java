package ziyue.tjmetro.fabric;

import mtr.client.ClientData;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import ziyue.tjmetro.block.base.BlockCustomColorBase;

public class RegistryImpl
{
    public static void registerCustomColorBlock(Block block) {
        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
            final int defaultColor = 0x7F7F7F;
            if (pos != null) {
                try {
                    BlockEntity entity = world.getBlockEntity(pos);
                    int color = entity != null ? ((BlockCustomColorBase.CustomColorBlockEntity) (((BlockCustomColorBase.CustomColorBlockEntity) entity).getRenderAttachmentData())).color : -1;
                    return color != -1 ? color : (ClientData.STATIONS.stream().filter(station1 -> station1.inArea(pos.getX(), pos.getZ())).findFirst().map(station2 -> station2.color).orElse(defaultColor));
                } catch (Exception ignored) {
                }
            }
            return defaultColor;
        }, block);
    }

    public static void registerFixedColorBlock(Block block, int color) {
        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> color, block);
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> color, item);
        }
    }

    public static GameRules.Key<GameRules.BooleanValue> registerBooleanGameRule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    public static GameRules.Key<GameRules.IntegerValue> registerIntegerGameRule(String name, GameRules.Category category, int defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createIntRule(defaultValue));
    }

    /**
     * Whether Cloth Config API is available on the client.
     *
     * @return true if Cloth Config API is loaded
     * @since 1.1.2.1
     */
    public static boolean isClothConfigLoaded() {
        return FabricLoader.getInstance().isModLoaded("cloth-config");
    }
}