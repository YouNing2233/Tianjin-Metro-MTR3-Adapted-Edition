package ziyue.tjmetro.neoforge;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import mtr.client.ClientData;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import ziyue.tjmetro.block.base.BlockCustomColorBase;

/**
 * NeoForge 侧的方块 / 物品着色注册。
 * <p>
 * 走 Architectury 的 {@link ColorHandlerRegistry}（也就是 MTR / NeoMTR 自己在用的那条路，
 * 见 {@code mtr.neoforge.RegistryClientImpl.registerBlockColors}），而不是自己挂
 * {@code RegisterColorHandlersEvent}：后者依赖「本模组的方块列表在事件触发前就已填充」这一时序假设，
 * 一旦事件早于 {@code FMLClientSetupEvent} 触发，就会一个着色器都注册不上，
 * 表现为「跟随车站颜色的方块全都显示默认色」。
 *
 * @since 1.1.2.7
 */

@OnlyIn(Dist.CLIENT)
public class ClientRegistryImpl
{
    /**
     * 跟随所在车站颜色的着色器；若方块实体自带颜色（即玩家在配置界面里选过），优先用方块实体的颜色。
     */
    private static final BlockColor STATION_COLOR = ClientRegistryImpl::getCustomColor;

    public static void registerCustomColorBlock(Block block) {
        ColorHandlerRegistry.registerBlockColors(STATION_COLOR, block);
    }

    public static void registerFixedColorBlock(Block block, int color) {
        ColorHandlerRegistry.registerBlockColors((state, level, pos, tintIndex) -> color, block);
        final Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> color, item);
        }
    }

    private static int getCustomColor(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int tintIndex) {
        final int defaultColor = 0x7F7F7F;
        if (pos != null && blockAndTintGetter != null) {
            try {
                final BlockEntity entity = blockAndTintGetter.getBlockEntity(pos);
                final int color = entity != null
                        ? ((BlockCustomColorBase.CustomColorBlockEntity) (((BlockCustomColorBase.CustomColorBlockEntity) entity).getRenderAttachmentData())).color
                        : -1;
                return color != -1 ? color : ClientData.STATIONS.stream()
                        .filter(station -> station.inArea(pos.getX(), pos.getZ()))
                        .findFirst()
                        .map(station -> station.color)
                        .orElse(defaultColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultColor;
    }
}
