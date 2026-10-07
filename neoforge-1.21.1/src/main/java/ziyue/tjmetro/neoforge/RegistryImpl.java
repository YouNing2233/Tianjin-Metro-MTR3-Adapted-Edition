package ziyue.tjmetro.neoforge;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import ziyue.tjmetro.neoforge.mixin.GameRulesBooleanValueAccessor;
import ziyue.tjmetro.neoforge.mixin.GameRulesIntegerValueAccessor;

public class RegistryImpl
{
    public static void registerCustomColorBlock(Block block) {
        try {
            Class<?> clazz = Class.forName("ziyue.tjmetro.neoforge.ClientRegistryImpl");
            clazz.getMethod("registerCustomColorBlock", Block.class).invoke(null, block);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register custom color block", e);
        }
    }

    public static void registerFixedColorBlock(Block block, int color) {
        try {
            Class<?> clazz = Class.forName("ziyue.tjmetro.neoforge.ClientRegistryImpl");
            clazz.getMethod("registerFixedColorBlock", Block.class, int.class).invoke(null, block, color);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register fixed color block", e);
        }
    }

    public static GameRules.Key<GameRules.BooleanValue> registerBooleanGameRule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRules.register(name, category, GameRulesBooleanValueAccessor.invokeCreate(defaultValue));
    }

    public static GameRules.Key<GameRules.IntegerValue> registerIntegerGameRule(String name, GameRules.Category category, int defaultValue) {
        return GameRules.register(name, category, GameRulesIntegerValueAccessor.invokeCreate(defaultValue));
    }

    /**
     * Whether Cloth Config API is available on the client.
     *
     * @return true if Cloth Config API is loaded
     * @since 1.1.2.1
     */
    public static boolean isClothConfigLoaded() {
        return net.neoforged.fml.ModList.get().isLoaded("cloth_config");
    }
}