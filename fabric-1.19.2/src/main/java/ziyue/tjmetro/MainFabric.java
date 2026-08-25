package ziyue.tjmetro;

import mtr.RegistryObject;
import mtr.item.ItemBlockEnchanted;
import mtr.item.ItemWithCreativeTabBase;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.FabricRegistryUtilities;
import mtr.mappings.RegistryUtilities;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class MainFabric implements ModInitializer
{
    @Override
    public void onInitialize() {
        TianjinMetro.init(MainFabric::registerBlock, MainFabric::registerBlock, MainFabric::registerItem, MainFabric::registerEnchantedBlock, MainFabric::registerBlockEntityType);
        registerSoundEvent("smoke_alarm");
    }

    public static void registerSoundEvent(String path) {
        final ResourceLocation id = new ResourceLocation(Reference.MOD_ID, path);
        Registry.register(RegistryUtilities.registryGetSoundEvent(), id, RegistryUtilities.createSoundEvent(id));
    }

    public static void registerBlock(String path, RegistryObject<Block> block) {
        Registry.register(RegistryUtilities.registryGetBlock(), new ResourceLocation(Reference.MOD_ID, path), block.get());
    }

    public static void registerBlock(String path, RegistryObject<Block> block, Object filter) {
        registerBlock(path, block);
        BlockItem blockItem = new BlockItem(block.get(), RegistryUtilities.createItemProperties(TianjinMetro.CREATIVE_MODE_TAB::get));
        Registry.register(RegistryUtilities.registryGetItem(), new ResourceLocation(Reference.MOD_ID, path), blockItem);
        FabricRegistryUtilities.registerCreativeModeTab(TianjinMetro.CREATIVE_MODE_TAB.get(), blockItem);
    }

    public static void registerItem(String path, RegistryObject<Item> item, Object filter) {
        Registry.register(RegistryUtilities.registryGetItem(), new ResourceLocation(Reference.MOD_ID, path), item.get());
        if (item.get() instanceof ItemWithCreativeTabBase) {
            FabricRegistryUtilities.registerCreativeModeTab(((ItemWithCreativeTabBase)item.get()).creativeModeTab.get(), item.get());
        } else if (item.get() instanceof ItemWithCreativeTabBase.ItemPlaceOnWater) {
            FabricRegistryUtilities.registerCreativeModeTab(((ItemWithCreativeTabBase.ItemPlaceOnWater)item.get()).creativeModeTab.get(), item.get());
        }
    }

    public static void registerEnchantedBlock(String path, RegistryObject<Block> block, Object filter) {
        registerBlock(path, block);
        ItemBlockEnchanted itemBlockEnchanted = new ItemBlockEnchanted(block.get(), RegistryUtilities.createItemProperties(TianjinMetro.CREATIVE_MODE_TAB::get));
        Registry.register(RegistryUtilities.registryGetItem(), new ResourceLocation(Reference.MOD_ID, path), itemBlockEnchanted);
        FabricRegistryUtilities.registerCreativeModeTab(TianjinMetro.CREATIVE_MODE_TAB.get(), itemBlockEnchanted);
    }

    public static void registerBlockEntityType(String path, RegistryObject<? extends BlockEntityType<? extends BlockEntityMapper>> blockEntityType) {
        Registry.register(RegistryUtilities.registryGetBlockEntityType(), new ResourceLocation(Reference.MOD_ID, path), blockEntityType.get());
    }
}