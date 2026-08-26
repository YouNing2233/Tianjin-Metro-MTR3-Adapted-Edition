package ziyue.tjmetro.render;

import mtr.block.BlockPSDAPGDoorBase;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

/**
 * Custom renderer for the Tianjin Jinjing-line APG door (half-height).
 * <p>
 * Same door panels as the default APG door, but with the Jinjing light strip
 * texture.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.block.BlockAPGDoorTianjinJinjing
 * @since 1.1.6 - Ported from 4.0
 */
public class RenderAPGDoorTianjinJinjing<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends RenderTianjinAPGDoor<T>
{
    public RenderAPGDoorTianjinJinjing(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, "apg_door_tianjin", "apg_door_tianjin_jinjing", false);
    }
}
