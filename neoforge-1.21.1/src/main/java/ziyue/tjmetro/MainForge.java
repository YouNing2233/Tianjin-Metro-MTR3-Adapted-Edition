package ziyue.tjmetro;

import mtr.Registry;
import mtr.RegistryObject;
import mtr.item.ItemBlockEnchanted;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.RegistryUtilities;
import mtr.neoforge.DeferredRegisterHolder;
import mtr.neoforge.mappings.ForgeUtilities;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * 1.21 / 1.21.1 NeoForge 入口。
 * <p>
 * 与 1.18.2 Forge 版的差别：
 * <ul>
 *   <li>MTR 侧改用 NeoMTR，Forge 专用工具从 {@code mtr.mappings} 搬到了 <b>{@code mtr.neoforge.*}</b>
 *       （{@code mtr.neoforge.mappings.ForgeUtilities} / {@code mtr.neoforge.DeferredRegisterHolder}）；</li>
 *   <li>NeoForge 1.20.2+ 的包名由 {@code net.minecraftforge.*} 改为 {@code net.neoforged.*}；</li>
 *   <li>{@code @Mod} 构造函数可直接注入 {@code IEventBus}，不必再经 {@code FMLJavaModLoadingContext}；</li>
 *   <li>模组列表里的「配置」按钮用 {@code IConfigScreenFactory} 扩展点，不再有 {@code ConfigGuiHandler}。</li>
 * </ul>
 *
 * @since 1.1.2.7
 */

@Mod(Reference.MOD_ID)
public class MainForge
{
    public static final DeferredRegisterHolder<Item> ITEMS = new DeferredRegisterHolder<>(Reference.MOD_ID, ForgeUtilities.registryGetItem());
    public static final DeferredRegisterHolder<Block> BLOCKS = new DeferredRegisterHolder<>(Reference.MOD_ID, ForgeUtilities.registryGetBlock());
    public static final DeferredRegisterHolder<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new DeferredRegisterHolder<>(Reference.MOD_ID, ForgeUtilities.registryGetBlockEntityType());
    public static final DeferredRegisterHolder<EntityType<?>> ENTITY_TYPES = new DeferredRegisterHolder<>(Reference.MOD_ID, net.minecraft.core.registries.Registries.ENTITY_TYPE);
    public static final DeferredRegisterHolder<SoundEvent> SOUND_EVENTS = new DeferredRegisterHolder<>(Reference.MOD_ID, ForgeUtilities.registryGetSoundEvent());
    public static final DeferredRegisterHolder<CreativeModeTab> CREATIVE_MODE_TABS = new DeferredRegisterHolder<>(Reference.MOD_ID, Registries.CREATIVE_MODE_TAB);

    /**
     * 本模组物品栏里的物品。注册顺序即展示顺序。
     */
    private static final java.util.List<Item> TAB_ITEMS = new java.util.ArrayList<>();

    public MainForge(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);

        // 创造模式物品栏。
        //
        // 背景（踩过的两个坑）：
        //  1) mtr.CreativeModeTabs.Wrapper 的构造函数会把自己登记进 MTR 的静态表 CREATIVE_TABS，
        //     但该 wrapper 的 creativeModeTab 字段只有 MTR 自己的 registerCreativeModeTabsToDeferredRegistry
        //     才会赋值。MTR 在它自己的构造函数里跑那一步时本模组的条目还不存在（模组构造是依赖优先），
        //     于是 MTR 的 BuildCreativeModeTabContentsEvent 处理器遍历静态表时会碰到一个字段为 null 的条目并 NPE。
        //     下面这次「一次性 holder」调用就是为了把那个字段补上：传进去的 holder 从不挂到事件总线，
        //     所以不会真的注册任何物品栏，也就不存在重复注册。
        //  2) 物品栏本身用 NeoForge 的 builder 自建 —— MTR 的 ForgeUtilities 物品栏 API 只服务于 MTR 自身，
        //     外挂模组拿到的 Supplier 会返回 null。物品由 TAB_ITEMS 按注册顺序收集。
        final ResourceLocation tabId = TianjinMetro.CREATIVE_MODE_TAB.resourceLocation;
        ForgeUtilities.registerCreativeModeTabsToDeferredRegistry(new DeferredRegisterHolder<>(Reference.MOD_ID, Registries.CREATIVE_MODE_TAB));

        CREATIVE_MODE_TABS.register(tabId.getPath(), () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.tjmetro.tjmetro_tab"))
                .icon(() -> new ItemStack(BlockList.LOGO.get()))
                .displayItems((parameters, output) -> TAB_ITEMS.forEach(output::accept))
                .build());
        CREATIVE_MODE_TABS.register(modEventBus);

        TianjinMetro.init(MainForge::registerBlock, MainForge::registerBlockItem, MainForge::registerItem, MainForge::registerEnchantedBlockItem, MainForge::registerBlockEntityType, MainForge::registerEntityType);

        registerSoundEvent("smoke_alarm");

        // 客户端专属的事件监听只在客户端挂载：ClientRegistryImpl 与 FMLClientSetupEvent 都引用了客户端类，
        // 专用服务器上加载会出错。
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.register(ForgeClientSetup.class);
            // 模组列表里的「配置」按钮。NeoForge 1.21 用它自己的 IConfigScreenFactory 扩展点，
            // 1.18.2 Forge 的 ConfigGuiHandler 已经不存在了。
            modContainer.registerExtensionPoint(net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                    (container, parent) -> ziyue.tjmetro.client.ConfigScreenOpener.getConfigScreen(parent));
        }
    }

    public static void registerBlock(String path, RegistryObject<Block> block) {
        BLOCKS.register(path, block::get);
    }

    public static void registerBlockItem(String path, RegistryObject<Block> block, Object filter) {
        registerBlock(path, block);
        ITEMS.register(path, () -> {
            final BlockItem blockItem = new BlockItem(block.get(), RegistryUtilities.createItemProperties(TianjinMetro.CREATIVE_MODE_TAB::get));
            TAB_ITEMS.add(blockItem);
            return blockItem;
        });
    }

    public static void registerItem(String path, RegistryObject<Item> item, Object filter) {
        ITEMS.register(path, () -> {
            final Item value = item.get();
            TAB_ITEMS.add(value);
            return value;
        });
    }

    public static void registerEnchantedBlockItem(String path, RegistryObject<Block> block, Object filter) {
        registerBlock(path, block);
        ITEMS.register(path, () -> {
            final ItemBlockEnchanted item = new ItemBlockEnchanted(block.get(), RegistryUtilities.createItemProperties(TianjinMetro.CREATIVE_MODE_TAB::get));
            TAB_ITEMS.add(item);
            return item;
        });
    }

    public static void registerBlockEntityType(String path, RegistryObject<? extends BlockEntityType<? extends BlockEntityMapper>> blockEntityType) {
        BLOCK_ENTITY_TYPES.register(path, blockEntityType::get);
    }

    public static void registerEntityType(String path, RegistryObject<? extends EntityType<? extends Entity>> entityType) {
        ENTITY_TYPES.register(path, entityType::get);
    }

    public static void registerSoundEvent(String path) {
        final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, path);
        SOUND_EVENTS.register(path, () -> RegistryUtilities.createSoundEvent(id));
    }

    public static class ForgeClientSetup
    {
        @SubscribeEvent
        public static void onClientSetupEvent(FMLClientSetupEvent event) {
            TianjinMetroClient.init();
        }

        @SubscribeEvent
        public static void onRegisterRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityTypes.SEAT.get(), ziyue.tjmetro.render.RenderSeat::new);
        }
    }
}