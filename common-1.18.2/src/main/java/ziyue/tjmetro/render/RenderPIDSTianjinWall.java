package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import mtr.block.IBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import ziyue.tjmetro.block.BlockPIDSTianjin;
import ziyue.tjmetro.block.BlockPIDSTianjinWall;

/**
 * Renderer for the wall-mounted Tianjin PIDS.
 *
 * <p>Everything on the canvas is drawn by {@link RenderPIDSTianjin} unchanged - same textures, same
 * fonts, same layout, same pixel scale (1 block = {@code scale} px, so the 251x115 canvas stays
 * 1.96 x 0.90 blocks). Only two things differ: the panel does not lean back by 22.5 degrees, and it
 * is centred vertically so that the picture lands inside the two-block television casing.</p>
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see RenderPIDSTianjin
 * @see BlockPIDSTianjinWall
 */
public class RenderPIDSTianjinWall extends RenderPIDSTianjin<BlockPIDSTianjinWall.TileEntityBlockPIDSTianjinWall> {

    public RenderPIDSTianjinWall(BlockEntityRenderDispatcher dispatcher) {
        // rotate90 = false: the canvas runs along X, i.e. across the two blocks the panel occupies.
        super(dispatcher, 2.5F, 30, false, 1F);
    }

    @Override
    public void render(BlockPIDSTianjinWall.TileEntityBlockPIDSTianjinWall entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockPIDSTianjin pidsBlock)) return;
        if (!pidsBlock.canStoreData(world, pos)) return;

        final Direction facing = IBlock.getStatePropertySafe(world, pos, HorizontalDirectionalBlock.FACING);
        final float scale = 320 / maxHeight * textPadding;

        renderFace(matrices, vertexConsumers, facing, scale, entity, pos, 0, 0);
    }

    @Override
    protected void applyDisplayTransform(PoseStack matrices, Direction facing, float scale, float offsetX, float offsetZ) {
        // Upright panel bolted onto the wall: no backward lean, picture centred on the casing.
        matrices.translate(0.5 + offsetX, 0.949, 0.5 + offsetZ);
        matrices.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-facing.toYRot()));
        matrices.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(180));
        matrices.translate(-0.48, 0, 0.48);
        matrices.scale(1F / scale, 1F / scale, 1F / scale);
    }
}
