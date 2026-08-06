package ziyue.tjmetro;

import dev.architectury.platform.forge.EventBuses;
import mtr.RegistryObject;
import mtr.forge.mappings.ForgeUtilities;
import mtr.item.ItemBlockEnchanted;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.DeferredRegisterHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ziyue.tjmetro.client.ConfigScreen;

import java.util.ArrayList;

@Mod(Reference.MOD_ID)
public class MainForge
{
    public static final DeferredRegisterHolder<Item> ITEMS = new DeferredRegisterHolder<>(Reference.MOD_ID, Registries.ITEM);
    public static final DeferredRegisterHolder<Block> BLOCKS = new DeferredRegisterHolder<>(Reference.MOD_ID, Registries.BLOCK);
    public static final DeferredRegisterHolder<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new DeferredRegisterHolder<>(Reference.MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    public static final DeferredRegisterHolder<CreativeModeTab> CREATIVE_MODE_TABS = new DeferredRegisterHolder<>(Reference.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final ArrayList<RegistryObject<Item>> TAB_ITEMS = new ArrayList<>();

    // 注意：不使用 static 初始化块执行注册操作。
    // Forge 的注册表在类加载时可能已冻结，静态块中调用 block.get()/item.get() 会触发
    // "Registry is already frozen"。所有注册逻辑移到构造函数中，
    // 并使用 BuildCreativeModeTabContentsEvent 延迟添加创造物品栏内容。

    public MainForge() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Reference.MOD_ID, eventBus);

        ModLoadingContext.get().registerExtensionPoint(net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreen.getConfigScreen(parent)));

        // 创建天津地铁创造物品栏（图标为 LOGO，翻译键为 itemGroup.tjmetro.tjmetro_tab）
        ForgeUtilities.createCreativeModeTab(TianjinMetro.CREATIVE_MODE_TAB.resourceLocation, () -> new ItemStack(BlockList.LOGO.get()), "itemGroup.tjmetro.tjmetro_tab");
        ForgeUtilities.registerCreativeModeTabsToDeferredRegistry(CREATIVE_MODE_TABS);

        // 先注册 DeferredRegisterHolder 到事件总线，确保 init() 中添加的条目能正确注册
        ITEMS.register();
        BLOCKS.register();
        BLOCK_ENTITY_TYPES.register();
        CREATIVE_MODE_TABS.register();

        // 初始化模组（填充 DeferredRegister 和 TAB_ITEMS）
        // register 方法中不再立即调用 block.get()/item.get()，全部使用 Supplier 延迟求值
        TianjinMetro.init(MainForge::registerBlock, MainForge::registerBlockItem, MainForge::registerItem, MainForge::registerEnchantedBlockItem, MainForge::registerBlockEntityType);

        eventBus.register(ForgeClientSetup.class);
        // 注册创造物品栏内容构建事件：在所有物品注册完成后将 TAB_ITEMS 添加到天津地铁标签页
        eventBus.addListener(MainForge::onBuildCreativeModeTab);
    }

    /**
     * 在所有物品注册完成后由 Forge 触发，将 TAB_ITEMS 中的物品添加到天津地铁创造物品栏。
     * 此时 DeferredRegister 已完成注册，block.get()/item.get() 可以安全调用。
     */
    public static void onBuildCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, TianjinMetro.CREATIVE_MODE_TAB.resourceLocation);
        if (event.getTabKey().equals(tabKey)) {
            for (RegistryObject<Item> item : TAB_ITEMS) {
                event.accept(item::get);
            }
        }
    }

    public static void registerBlock(String path, RegistryObject<Block> block) {
        BLOCKS.register(path, block::get);
    }

    public static void registerBlockItem(String path, RegistryObject<Block> block, Object filter) {
        registerBlock(path, block);
        ITEMS.register(path, () -> new BlockItem(block.get(), new Item.Properties()));
        TAB_ITEMS.add(new RegistryObject<>(() -> block.get().asItem()));
    }

    public static void registerItem(String path, RegistryObject<Item> item, Object filter) {
        ITEMS.register(path, item::get);
        TAB_ITEMS.add(item);
    }

    public static void registerEnchantedBlockItem(String path, RegistryObject<Block> block, Object filter) {
        registerBlock(path, block);
        ITEMS.register(path, () -> new ItemBlockEnchanted(block.get(), new Item.Properties()));
        TAB_ITEMS.add(new RegistryObject<>(() -> block.get().asItem()));
    }

    public static void registerBlockEntityType(String path, RegistryObject<? extends BlockEntityType<? extends BlockEntityMapper>> blockEntityType) {
        BLOCK_ENTITY_TYPES.register(path, blockEntityType::get);
    }

    public static class ForgeClientSetup
    {
        @SubscribeEvent
        public static void onClientSetupEvent(FMLClientSetupEvent event) {
            TianjinMetroClient.init();
        }
    }
}
