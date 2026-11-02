package ziyue.tjmetro;

import mtr.RegistryObject;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import ziyue.tjmetro.entity.EntitySeat;

/**
 * Entity types owned by this mod.
 *
 * <p>NeoMTR (MTR 3.2.x on 1.21.1) removed {@code mtr.entity.EntitySeat}, together with the
 * per-seat train renderer that used to spawn it. The bench therefore needs its own seat entity
 * and this mod has to register the entity type itself instead of borrowing MTR's.</p>
 *
 * @since 1.1.3.1
 */

public interface EntityTypes
{
    RegistryObject<EntityType<EntitySeat>> SEAT = new RegistryObject<>(() -> EntityType.Builder
            .of(EntitySeat::new, MobCategory.MISC)
            .sized(0.6F, 0.6F)
            .noSummon()
            // The seat only ever lives while somebody sits on it, it must never be written to disk.
            .noSave()
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(Reference.MOD_ID + ":seat"));
}
