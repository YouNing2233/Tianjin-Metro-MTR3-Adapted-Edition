package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.MTR;
import mtr.block.BlockStationNameBase;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.block.BlockRailwaySignSingle;

import java.util.HashSet;

import static mtr.data.IGui.MAX_LIGHT_GLOWING;

/**
 * Render content for <b>Railway Sign (Single)</b>, ported from the MTR4 Tianjin Metro addon
 * (<i>RenderRailwaySignSingle</i>, 1.1.3).
 * <p>
 * Draws a dark backing plate plus up to two sign faces: slot 0 on the face matching
 * {@code FACING}, slot 1 on the opposite face (mirrored through the block centre).
 *
 * @author ZiYueCommentary
 * @see BlockRailwaySignSingle
 * @since 1.1.3
 */
public class RenderRailwaySignSingle<T extends BlockRailwaySignSingle.BlockEntity> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing
{
    public RenderRailwaySignSingle(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);

        final StoredMatrixTransformations frontTransformations = new StoredMatrixTransformations();
        frontTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), 0.5 + pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(-0.25, -0.218, -0.1);
        });
        renderSign(matrices, vertexConsumers, frontTransformations, facing, pos, entity.getSignId(0));

        final StoredMatrixTransformations backTransformations = frontTransformations.copy();
        backTransformations.add(matricesNew -> {
            UtilitiesClient.rotateYDegrees(matricesNew, 180);
            matricesNew.translate(-0.5, 0, -0.2);
        });
        renderSign(matrices, vertexConsumers, backTransformations, facing, pos, entity.getSignId(1));
    }

    protected void renderSign(PoseStack matrices, MultiBufferSource vertexConsumers, StoredMatrixTransformations storedMatrixTransformations, Direction facing, BlockPos pos, String signId) {
        RenderTrains.scheduleRender(new ResourceLocation(MTR.MOD_ID, "textures/block/white.png"), false, RenderTrains.QueuedRenderLayer.LIGHT, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, SMALL_OFFSET, 0.5F, 0.5F, SMALL_OFFSET, facing, ARGB_BLACK, MAX_LIGHT_GLOWING);
            matricesNew.popPose();
        });

        if (signId != null) {
            RenderRailwaySignWall.drawSign(matrices, vertexConsumers, storedMatrixTransformations, Minecraft.getInstance().font, pos, signId, 0, 0, 0.5F, 0.5F, 0.5F, new HashSet<>(), facing, ARGB_BLACK, (textureId, x, y, size, flipTexture) -> RenderTrains.scheduleRender(new ResourceLocation(textureId.toString()), true, RenderTrains.QueuedRenderLayer.LIGHT_TRANSLUCENT, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                IDrawing.drawTexture(matricesNew, vertexConsumer, x, y, size, size, flipTexture ? 1 : 0, 0, flipTexture ? 0 : 1, 1, facing, -1, MAX_LIGHT_GLOWING);
                matricesNew.popPose();
            }));
        }
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }
}
