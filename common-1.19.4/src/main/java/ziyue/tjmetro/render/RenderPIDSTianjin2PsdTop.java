package ziyue.tjmetro.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import ziyue.tjmetro.block.BlockPIDSTianjin2PsdTop;

/**
 * Line 2 flavour of the header mounted screen. The layout is shared with
 * {@link RenderPIDSTianjinPsdTop}; the line colour and name come from the route data.
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 */
public class RenderPIDSTianjin2PsdTop<T extends BlockPIDSTianjin2PsdTop.TileEntityBlockPIDSTianjin2PsdTop> extends RenderPIDSTianjinPsdTop<T>
{
    public RenderPIDSTianjin2PsdTop(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }
}
