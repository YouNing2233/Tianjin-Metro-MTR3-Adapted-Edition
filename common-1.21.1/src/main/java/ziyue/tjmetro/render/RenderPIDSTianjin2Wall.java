package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import ziyue.tjmetro.block.BlockPIDSTianjin2Wall;

/**
 * Renderer for the wall-mounted Tianjin PIDS with the Line 2 layout.
 *
 * <p>{@link RenderPIDSTianjin2} already knows how to draw a single face with {@code rotate90=false},
 * so only the mounting transform has to be replaced.</p>
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see RenderPIDSTianjin2
 * @see BlockPIDSTianjin2Wall
 */
public class RenderPIDSTianjin2Wall extends RenderPIDSTianjin2<BlockPIDSTianjin2Wall.TileEntityBlockPIDSTianjin2Wall> {

    public RenderPIDSTianjin2Wall(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, false, false);
    }

    @Override
    protected void applyDisplayTransform(PoseStack matrices, Direction facing, float scale, float offsetX, float offsetZ) {
        // Upright panel bolted onto the wall: no backward lean, picture centred on the casing.
        matrices.translate(0.5 + offsetX, 0.949, 0.5 + offsetZ);
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-facing.toYRot()));
        matrices.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
        matrices.translate(-0.48, 0, 0.48);
        matrices.scale(1F / scale, 1F / scale, 1F / scale);
    }
}
