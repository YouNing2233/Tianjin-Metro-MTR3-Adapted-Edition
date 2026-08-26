package ziyue.tjmetro.render;

import mtr.block.BlockPSDAPGDoorBase;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

/**
 * Custom renderer for the Tianjin APG door (half-height, default / TRT style).
 * <p>
 * Reuses {@link RenderTianjinAPGDoor} with the default APG door textures and
 * the texture-based light strip. Also used for the BMT door and the single
 * doors.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.block.BlockAPGDoorTianjin
 * @since 1.1.6 - Ported from 4.0
 */
public class RenderAPGDoorTianjin<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends RenderTianjinAPGDoor<T>
{
    public RenderAPGDoorTianjin(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, "apg_door_tianjin", "apg_door_tianjin", false);
    }
}
