package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockStationNameBase;
import mtr.block.IBlock;
import mtr.client.ClientData;
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
import ziyue.tjmetro.block.BlockStationNameSignTianjin;
import ziyue.tjmetro.client.ClientCache;

import static mtr.data.IGui.ARGB_WHITE;

/**
 * Render content for <b>Tianjin Station Name Sign Block</b>, ported from MTR4
 * {@code ziyue.tjmetro.mod.render.RenderStationNameSignTianjin}.
 * Only the LEFT half renders; the texture is drawn 1.5 blocks wide across both halves.
 *
 * @author ZiYueCommentary
 * @see BlockStationNameSignTianjin
 * @since 1.1.8
 */

public class RenderStationNameSignTianjin<T extends BlockStationNameSignTianjin.TileEntityStationNameSignTianjin> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing
{
    public RenderStationNameSignTianjin(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);
        if (IBlock.getStatePropertySafe(state, SIDE) == IBlock.EnumSide.RIGHT) return;

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5F + pos.getX(), 1F + pos.getY(), 0.5F + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(-0.25F, 0, 0.43F + SMALL_OFFSET * 2);
        });

        if (ClientData.DATA_CACHE.platformIdMap.get(entity.getPlatformId()) == null) return;

        // MTR4 passes aspectRatio 1.5F here; the MTR3 wrapper ziyue.tjmetro.client.ClientCache#getStationNameSignTianjin
        // currently only accepts int aspectRatio (a porting quirk). Once that parameter is changed to float,
        // pass 1.5F for the correct 1.5:1 texture (see port report, item "待主代理处理").
        final ClientCache.DynamicResource dynamicResource = ClientCache.DATA_CACHE.getStationNameSignTianjin(entity.getPlatformId(), 1.5F, ARGB_WHITE);

        RenderTrains.scheduleRender(dynamicResource.resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, 1.5F, 1F, 0, 0, 1, 1, facing, -1, light);
            matricesNew.popPose();
        });
    }
}
