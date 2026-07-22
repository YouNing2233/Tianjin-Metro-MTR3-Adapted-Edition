package ziyue.tjmetro.forge;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;

public class RegistryImpl
{
    /**
     * 注册自定义颜色方块的颜色提供者。
     * 实际实现委托给 ClientRegistryImpl（客户端专用类），此处仅在客户端环境调用。
     * 服务端环境下此方法不应被调用，但由于 @ExpectPlatform 机制要求方法签名存在，
     * 因此保留空实现。客户端颜色注册由 TianjinMetroClient.init() 触发，
     * 该 init 仅在 ForgeClientSetup.onClientSetupEvent 中调用。
     */
    public static void registerCustomColorBlock(Block block) {
        // 通过反射调用 ClientRegistryImpl.registerCustomColorBlock，
        // 避免在类加载时引用 BlockColor 等客户端类。
        // 此方法只在客户端环境下被调用（从 TianjinMetroClient.init()），
        // 所以反射调用是安全的。
        try {
            Class<?> clazz = Class.forName("ziyue.tjmetro.forge.ClientRegistryImpl");
            clazz.getMethod("registerCustomColorBlock", Block.class).invoke(null, block);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register custom color block", e);
        }
    }

    public static void registerFixedColorBlock(Block block, int color) {
        // 通过反射调用 ClientRegistryImpl.registerFixedColorBlock，
        // 避免在类加载时引用 BlockColor 等客户端类。
        try {
            Class<?> clazz = Class.forName("ziyue.tjmetro.forge.ClientRegistryImpl");
            clazz.getMethod("registerFixedColorBlock", Block.class, int.class).invoke(null, block, color);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register fixed color block", e);
        }
    }

    public static GameRules.Key<GameRules.BooleanValue> registerBooleanGameRule(String name, GameRules.Category category, boolean defaultValue) {
        return GameRules.register(name, category, GameRules.BooleanValue.create(defaultValue));
    }

    public static GameRules.Key<GameRules.IntegerValue> registerIntegerGameRule(String name, GameRules.Category category, int defaultValue) {
        return GameRules.register(name, category, GameRules.IntegerValue.create(defaultValue));
    }
}
