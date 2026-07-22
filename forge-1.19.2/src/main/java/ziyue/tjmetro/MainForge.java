package ziyue.tjmetro;

import dev.architectury.platform.forge.EventBuses;
import mtr.RegistryObject;
import mtr.item.ItemBlockEnchanted;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.DeferredRegisterHolder;
import mtr.mappings.ForgeUtilities;
import mtr.mappings.RegistryUtilities;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ziyue.tjmetro.client.ConfigScreen;

@Mod(Reference.MOD_ID)
public class MainForge
{
    public static final DeferredRegisterHolder<Item> ITEMS = new DeferredRegisterHolder<>(Reference.MOD_ID, ForgeUtilities.registryGetItem());
    public static final DeferredRegisterHolder<Block> BLOCKS = new DeferredRegisterHolder<>(Reference.MOD_ID, ForgeUtilities.registryGetBlock());
    public static final DeferredRegisterHolder<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new DeferredRegisterHolder<>(Reference.MOD_ID, ForgeUtilities.registryGetBlockEntityType());

    // 注意：1.19.2 不使用 BuildCreativeModeTabContentsEvent（1.20.1 才引入）。
    // 创造物品栏通过 ForgeUtilities.createCreativeModeTab 注册（内部使用 Architectury CreativeTabRegistry）。
    // 物品通过 RegistryUtilities.createItemProperties 设置 tab，会自动加入对应创造物品栏。
    // 所有注册逻辑移到构造函数中，使用 Supplier 延迟求值避免注册表冻结问题。

    public MainForge() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Reference.MOD_ID, eventBus);

        ModLoadingContext.get().registerExtensionPoint(net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreen.getConfigScreen(parent)));

        // 创建天津地铁创造物品栏（图标为 LOGO，翻译键为 itemGroup.tjmetro.tjmetro_tab）
        ForgeUtilities.createCreativeModeTab(TianjinMetro.CREATIVE_MODE_TAB.resourceLocation, () -> new ItemStack(BlockList.LOGO.get()), "itemGroup.tjmetro.tjmetro_tab");

        // 初始化模组（填充 DeferredRegister）
        TianjinMetro.init(MainForge::registerBlock, MainForge::registerBlockItem, MainForge::registerItem, MainForge::registerEnchantedBlockItem, MainForge::registerBlockEntityType);

        ITEMS.register();
        BLOCKS.register();
        BLOCK_ENTITY_TYPES.register();

        eventBus.register(ForgeClientSetup.class);
    }

    public static void registerBlock(String path, RegistryObject<Block> block) {
        BLOCKS.register(path, block::get);
    }

    public static void registerBlockItem(String path, RegistryObject<Block> block, Object filter) {
        registerBlock(path, block);
        ITEMS.register(path, () -> new BlockItem(block.get(), RegistryUtilities.createItemProperties(TianjinMetro.CREATIVE_MODE_TAB::get)));
    }

    public static void registerItem(String path, RegistryObject<Item> item, Object filter) {
        ITEMS.register(path, item::get);
    }

    public static void registerEnchantedBlockItem(String path, RegistryObject<Block> block, Object filter) {
        registerBlock(path, block);
        ITEMS.register(path, () -> new ItemBlockEnchanted(block.get(), RegistryUtilities.createItemProperties(TianjinMetro.CREATIVE_MODE_TAB::get)));
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
