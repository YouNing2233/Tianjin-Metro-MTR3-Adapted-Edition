package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.data.RailwayData;
import mtr.data.Station;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.block.BlockStationNameProjector;
import ziyue.tjmetro.client.ClientCache;

import static mtr.data.IGui.ARGB_BLACK;
import static mtr.data.IGui.MAX_LIGHT_GLOWING;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-4
 */

public class RenderStationNameProjector<T extends BlockEntityMapper> extends BlockEntityRendererMapper<T>
{
    public RenderStationNameProjector(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        final Direction facing = IBlock.getStatePropertySafe(state, HorizontalDirectionalBlock.FACING);
        final int scale = IBlock.getStatePropertySafe(state, BlockStationNameProjector.SCALE);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), 0.5 + pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(0, 0, 0.5 - IGui.SMALL_OFFSET);
            matricesNew.scale(scale, scale, scale);
        });

        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);
        RenderTrains.scheduleRender(ClientCache.DATA_CACHE.getStationNameProjector(station == null ? net.minecraft.network.chat.Component.translatable("gui.mtr.untitled").getString() : station.name, 10).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, -5F, -0.5F, 10, 1, 0, 0, 1, 1, facing, ((BlockStationNameProjector.BlockEntity) entity).color == -1 ? ((BlockStationNameProjector.BlockEntity) entity).getDefaultColor(pos) : ARGB_BLACK | ((BlockStationNameProjector.BlockEntity) entity).color, MAX_LIGHT_GLOWING);
            matricesNew.popPose();
        });
    }
}
