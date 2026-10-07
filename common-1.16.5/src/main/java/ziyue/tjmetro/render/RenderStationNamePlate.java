package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockStationNameBase;
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
import ziyue.tjmetro.block.BlockStationNamePlate;
import ziyue.tjmetro.client.ClientCache;

/**
 * Render content for <b>Station Name Plate Block</b>, ported from the MTR4 Tianjin Metro addon
 * (<i>RenderStationNamePlate</i>, 1.0.0-beta-1).
 * <p>
 * Generates a dynamic texture (station name + directional arrow for the selected platform) via
 * {@link ClientCache#getStationNamePlate} and draws it as a deferred quad (EXTERIOR layer) on the
 * block faces, 4 blocks wide and 0.5 blocks tall.
 *
 * @author ZiYueCommentary
 * @see BlockStationNamePlate
 * @see ClientCache#getStationNamePlate
 * @since 1.1.7
 */
public class RenderStationNamePlate<T extends BlockStationNamePlate.TileEntityStationNamePlate> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing
{
    public RenderStationNamePlate(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);
        final int arrowDirection = IBlock.getStatePropertySafe(state, BlockStationNamePlate.ARROW_DIRECTION);

        // Do not render anything while no platform is selected yet (platformId == 0); an
        // unconfigured plate intentionally shows the bare (light metal) block. Once a platform
        // is configured, always render: when MTR 3.2.2's client-side station/platform data is
        // missing (legacy versions can drop the platform lookup even right after configuring),
        // the never-blank fallback inside generateStationNamePlate still draws at least the
        // station name instead of leaving the plate as a blank white block. The check stays
        // before the pose push, so an early return can never leave the stack unbalanced.
        if (entity.getPlatformId() == 0) return;

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + entity.getBlockPos().getX(), 0.71 + entity.getBlockPos().getY(), 0.5 + entity.getBlockPos().getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(-0.5, 0, -0.0625 - SMALL_OFFSET * 2);
        });

        matrices.pushPose();
        matrices.translate(0.5, 0.53125, 0.5);
        UtilitiesClient.rotateYDegrees(matrices, -facing.toYRot());
        UtilitiesClient.rotateZDegrees(matrices, 180);
        matrices.translate(-0.25, 0, -0.0625 - SMALL_OFFSET * 2);

        final ClientCache.DynamicResource resource = ClientCache.DATA_CACHE.getStationNamePlate(entity.getPlatformId(), arrowDirection, 0xff00379c, 0.25F, 8F, 0xffcc7b32, ARGB_WHITE);
        RenderTrains.scheduleRender(resource.resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, 4, 0.5F, 0, 0, 1, 1, facing, -1, MAX_LIGHT_GLOWING);
            matricesNew.popPose();
        });

        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }
}
