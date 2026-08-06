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

    public MainForge() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Reference.MOD_ID, eventBus);

        ModLoadingContext.get().registerExtensionPoint(net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreen.getConfigScreen(parent)));

        ForgeUtilities.createCreativeModeTab(TianjinMetro.CREATIVE_MODE_TAB.resourceLocation, () -> new net.minecraft.world.item.ItemStack(BlockList.LOGO.get()), "itemGroup.tjmetro.tjmetro_tab");

        ITEMS.register();
        BLOCKS.register();
        BLOCK_ENTITY_TYPES.register();

        TianjinMetro.init(MainForge::registerBlock, MainForge::registerBlockItem, MainForge::registerItem, MainForge::registerEnchantedBlockItem, MainForge::registerBlockEntityType);

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