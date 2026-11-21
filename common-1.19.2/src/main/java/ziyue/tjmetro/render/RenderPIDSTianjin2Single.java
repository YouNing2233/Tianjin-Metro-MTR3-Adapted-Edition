package ziyue.tjmetro.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import ziyue.tjmetro.block.BlockPIDSTianjin2Single;

/**
 * Renderer for the single-faced Line 2 PIDS. Only the front face is drawn; the dummy half
 * ({@code should_render=false}) is skipped because {@code canStoreData} is false there.
 *
 * <p>{@code rotate90=false} mirrors the registration of the original single-faced PIDS,
 * whose model is oriented differently from the double-sided one.</p>
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see RenderPIDSTianjin2
 * @see ziyue.tjmetro.render.RenderPIDSTianjinSingle
 */
public class RenderPIDSTianjin2Single extends RenderPIDSTianjin2<BlockPIDSTianjin2Single.TileEntityBlockPIDSTianjin2Single> {

    public RenderPIDSTianjin2Single(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, false, false);
    }
}
