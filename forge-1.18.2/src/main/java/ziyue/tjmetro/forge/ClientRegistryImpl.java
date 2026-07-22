package ziyue.tjmetro.forge;

import mtr.client.ClientData;
import mtr.mappings.RegistryUtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ziyue.tjmetro.block.base.BlockCustomColorBase;

@OnlyIn(Dist.CLIENT)
public class ClientRegistryImpl
{
    public static void registerCustomColorBlock(Block block) {
        RegistryUtilitiesClient.registerBlockColors(new CustomColor(), block);
    }

    public static void registerFixedColorBlock(Block block, int color) {
        RegistryUtilitiesClient.registerBlockColors((state, world, pos, tintIndex) -> color, block);
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> color, item);
        }
    }

    protected static class CustomColor implements BlockColor
    {
        @Override
        public int getColor(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int i) {
            final int defaultColor = 0x7F7F7F;
            if (pos != null) {
                try {
                    BlockEntity entity = blockAndTintGetter.getBlockEntity(pos);
                    int color = entity != null ? ((BlockCustomColorBase.CustomColorBlockEntity) (((BlockCustomColorBase.CustomColorBlockEntity) entity).getRenderAttachmentData())).color : -1;
                    return color != -1 ? color : ClientData.STATIONS.stream().filter(station1 -> station1.inArea(pos.getX(), pos.getZ())).findFirst().map(station2 -> station2.color).orElse(defaultColor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return defaultColor;
        }
    }
}