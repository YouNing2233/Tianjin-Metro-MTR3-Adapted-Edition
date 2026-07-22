package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.block.BlockStationNavigator;
import ziyue.tjmetro.client.ClientCache;

import static mtr.data.IGui.ARGB_BLACK;
import static mtr.data.IGui.MAX_LIGHT_GLOWING;

/**
 * @author ZiYueCommentary
 * @see BlockStationNavigator
 * @since 1.0.0-beta-4
 */

public class RenderStationNavigator<T extends BlockStationNavigator.BlockEntity> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing
{
    public RenderStationNavigator(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockStationNavigator block)) return;

        final Direction facing = IBlock.getStatePropertySafe(state, net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
        final boolean arrowLeft = IBlock.getStatePropertySafe(state, BlockStationNavigator.ARROW_LEFT);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), 0.53125 + pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(block.getXStart() / 16F - (arrowLeft ? 0.51 : 0.49), 0, -0.0625 - SMALL_OFFSET * 2);
        });

        matrices.pushPose();
        matrices.translate(0.5, 0.53125, 0.5);
        UtilitiesClient.rotateYDegrees(matrices, -facing.toYRot());
        UtilitiesClient.rotateZDegrees(matrices, 180);
        matrices.translate(block.getXStart() / 16F - 0.51, 0, -0.0625 - SMALL_OFFSET * 2);

        RenderTrains.scheduleRender(ClientCache.DATA_CACHE.getStationNavigator(entity.getSelectedIds(), arrowLeft, ARGB_BLACK, entity.length).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, entity.length / 2F, 0.5F, 0, 0, 1, 1, facing, -1, MAX_LIGHT_GLOWING);
            matricesNew.popPose();
        });

        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }
}
