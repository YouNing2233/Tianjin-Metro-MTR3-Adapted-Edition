package ziyue.tjmetro.forge.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ziyue.tjmetro.mapping.ContainerAccessor;

/**
 * Exposes the inventory of a storage minecart via {@link ContainerAccessor}.
 *
 * @author ZiYueCommentary
 * @since 1.1.0
 */
@Mixin(AbstractMinecartContainer.class)
public abstract class StorageMinecartEntityMixin extends AbstractMinecart implements ContainerAccessor
{
    @Shadow
    private NonNullList<ItemStack> itemStacks;

    protected StorageMinecartEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public NonNullList<ItemStack> tianjin_Metro$getInventory() {
        return this.itemStacks;
    }
}
