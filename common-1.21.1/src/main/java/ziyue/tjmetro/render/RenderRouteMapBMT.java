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
import ziyue.tjmetro.block.BlockRouteMapBMT;
import ziyue.tjmetro.client.ClientCache;

import static mtr.data.IGui.MAX_LIGHT_GLOWING;

/**
 * Render content for <b>Binhai New Area (BMT, Tianjin Metro Line 9) Route Map</b>, ported from the
 * MTR4 Tianjin Metro addon (<i>RenderRouteMapBMT</i>, 1.0.0).
 * <p>
 * Generates a dynamic route map texture via {@link ClientCache#getRouteMapBMT} (Tianjin BMT color
 * scheme, background {@code 0xff00379c}) and draws it as a deferred quad (EXTERIOR layer) on the
 * sign plate, 2.5 blocks wide and 1.66 blocks tall.
 *
 * @author ZiYueCommentary
 * @see BlockRouteMapBMT
 * @see ClientCache#getRouteMapBMT
 * @since 1.1.8
 */
public class RenderRouteMapBMT<T extends BlockRouteMapBMT.TileEntityRouteMapBMT> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing
{
    public RenderRouteMapBMT(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);
        final IBlock.EnumSide side = IBlock.getStatePropertySafe(state, IBlock.SIDE);

        // Do not render anything when the platform is not (or no longer) available.
        if (ClientData.DATA_CACHE.platformIdMap.get(entity.getPlatformId()) == null) return;

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), 0.71 + pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(-0.25, 0, -0.0625 - SMALL_OFFSET * 2);
        });

        RenderTrains.scheduleRender(ClientCache.DATA_CACHE.getRouteMapBMT(entity.getPlatformId(), side == IBlock.EnumSide.LEFT, 2.5F / 1.66F, 0xff00379c).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, 2.5F, 1.66F, 0, 0, 1, 1, facing, -1, MAX_LIGHT_GLOWING);
            matricesNew.popPose();
        });
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }
}
