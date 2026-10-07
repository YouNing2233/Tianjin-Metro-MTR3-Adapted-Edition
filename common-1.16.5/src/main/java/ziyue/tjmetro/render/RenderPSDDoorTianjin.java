package ziyue.tjmetro.render;

import mtr.block.BlockPSDAPGDoorBase;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

/**
 * Custom renderer for the Tianjin PSD door.
 * <p>
 * Reuses {@link RenderTianjinPSDDoor}, which was ported from MTR 4.0
 * {@code RenderPSDDoorTianjin} and already renders the tjmetro PSD door textures.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.block.BlockPSDDoorTianjin
 * @since 1.1.6 - Ported from 4.0
 */
public class RenderPSDDoorTianjin<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends RenderTianjinPSDDoor<T>
{
    public RenderPSDDoorTianjin(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }
}
