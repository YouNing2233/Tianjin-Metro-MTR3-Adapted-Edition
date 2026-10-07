package ziyue.tjmetro.mapping;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

/**
 * Interface for accessing the inventory of a storage minecart.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.neoforge.mixin.StorageMinecartEntityMixin
 * @since 1.1.0
 */
public interface ContainerAccessor
{
    NonNullList<ItemStack> tianjin_Metro$getInventory();
}
