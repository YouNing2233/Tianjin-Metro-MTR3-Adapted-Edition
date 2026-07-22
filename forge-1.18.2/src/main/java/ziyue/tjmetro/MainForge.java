package ziyue.tjmetro;

import dev.architectury.platform.forge.EventBuses;
import mtr.RegistryObject;
import mtr.item.ItemBlockEnchanted;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.ForgeUtilities;
import mtr.mappings.RegistryUtilities;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ziyue.tjmetro.client.ConfigScreen;

/**
 * Forge 1.18.2 entry point for Tianjin Metro Mod.
 *
 * <p>Adapted from the 1.20.1 MainForge to use 1.18.2 Forge APIs:
 * <ul>
 *   <li>{@code DeferredRegister} instead of {@code DeferredRegisterHolder}</li>
 *   <li>{@code ForgeRegistries} instead of {@code Registries}</li>
 *   <li>{@code ForgeUtilities.createCreativeModeTab} (Architectury-backed) instead of {@code BuildCreativeModeTabContentsEvent}</li>
 *   <li>Item creative tabs set via {@code Item.Properties.tab} (through {@code RegistryUtilities.createItemProperties})</li>
 * </ul>
 *
 * <p>Note: {@code ForgeUtilities.registerCreativeModeTab} is an empty implementation in 1.18.2 MTR,
 * so items must be assigned to the creative tab via {@code Item.Properties.tab(CreativeModeTab)} at
 * construction time. {@code ItemWithCreativeTabBase} and its subclasses already handle this.
 *
 * @since 1.0.0-rc-2
 */
@Mod(Reference.MOD_ID)
public class MainForge
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Reference.MOD_ID);

    public MainForge() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Reference.MOD_ID, eventBus);

        // 注意：net.minecraftforge.client.ConfigScreenHandler 在 Forge 1.18.2 中不存在（1.19.2+ 才引入），
        // 因此这里不注册配置屏幕入口。配置屏幕仍可通过模组内的指令或键位访问。

        // 创建天津地铁创造物品栏（通过 Architectury CreativeTabRegistry）
        // 翻译键 itemGroup.tjmetro.tjmetro_tab 与 fabric.lang 中的条目对应
        ForgeUtilities.createCreativeModeTab(
                TianjinMetro.CREATIVE_MODE_TAB.resourceLocation,
                () -> new ItemStack(BlockList.LOGO.get()),
                "itemGroup.tjmetro.tjmetro_tab"
        );

        // 初始化模组（填充 DeferredRegister）
        // register 方法中使用 Supplier 延迟求值，避免立即访问注册表
        TianjinMetro.init(MainForge::registerBlock, MainForge::registerBlockItem, MainForge::registerItem, MainForge::registerEnchantedBlockItem, MainForge::registerBlockEntityType);

        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);

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
        // ItemWithCreativeTabBase 及其子类（如 WRENCH、ItemPSDAPGTianjinBase）已在构造时
        // 通过 Item.Properties.tab() 设置创造物品栏，无需额外调用 ForgeUtilities.registerCreativeModeTab
        // （1.18.2 MTR 中该方法为空实现）
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
