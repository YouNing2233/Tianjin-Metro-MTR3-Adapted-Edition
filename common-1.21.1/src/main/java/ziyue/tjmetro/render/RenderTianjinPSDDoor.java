package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * Custom renderer for Tianjin PSD doors.
 * Uses tjmetro namespace textures instead of MTR defaults.
 *
 * @since 1.1.6
 */
public class RenderTianjinPSDDoor<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends BlockEntityRendererMapper<T> {

    private static final ModelSingleCube MODEL_PSD = new ModelSingleCube(36, 18, 0, 0, 0, 16, 16, 2);
    private static final ModelSingleCube MODEL_PSD_END_LEFT_1 = new ModelSingleCube(20, 18, 0, 0, 0, 8, 16, 2);
    private static final ModelSingleCube MODEL_PSD_END_RIGHT_1 = new ModelSingleCube(20, 18, 8, 0, 0, 8, 16, 2);
    private static final ModelSingleCube MODEL_PSD_END_LEFT_2 = new ModelSingleCube(20, 18, 8, 0, 2, 8, 16, 2);
    private static final ModelSingleCube MODEL_PSD_END_RIGHT_2 = new ModelSingleCube(20, 18, 0, 0, 2, 8, 16, 2);
    private static final ModelSingleCube MODEL_PSD_LIGHT_LEFT = new ModelSingleCube(16, 16, 0, -1, 5, 1, 1, 1);
    private static final ModelSingleCube MODEL_PSD_LIGHT_RIGHT = new ModelSingleCube(16, 16, 15, -1, 5, 1, 1, 1);
    private static final ModelSingleCube MODEL_PSD_DOOR_LOCKED = new ModelSingleCube(6, 6, 5, 6, 1, 6, 6, 0);

    public RenderTianjinPSDDoor(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) {
            return;
        }

        final BlockPos pos = entity.getBlockPos();
        final Direction facing = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.FACING);
        final boolean side = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.SIDE) == BlockPSDAPGDoorBase.EnumSide.RIGHT;
        final boolean half = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.HALF) == DoubleBlockHalf.UPPER;
        final boolean end = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.END);
        final boolean unlocked = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.UNLOCKED);
        final float open = DoorAnimationState.getInterpolatedOpen(entity, world, tickDelta);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateXDegrees(matricesNew, 180);
        });
        final StoredMatrixTransformations storedMatrixTransformationsLight = storedMatrixTransformations.copy();

        if (half) {
            RenderTrains.scheduleRender(DoorTextureCache.psdLight(open > 0), false, open > 0 ? RenderTrains.QueuedRenderLayer.LIGHT : RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformationsLight.transform(matricesNew);
                (side ? MODEL_PSD_LIGHT_RIGHT : MODEL_PSD_LIGHT_LEFT).renderToBuffer(matricesNew, vertexConsumer, light, overlay, 0xFFFFFFFF);
                matricesNew.popPose();
            });
        }
        if (end) {
            RenderTrains.scheduleRender(DoorTextureCache.psdEnd(half, side, 2), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformationsLight.transform(matricesNew);
                matricesNew.translate(open / 2 * (side ? -1 : 1), 0, 0);
                (side ? MODEL_PSD_END_RIGHT_2 : MODEL_PSD_END_LEFT_2).renderToBuffer(matricesNew, vertexConsumer, light, overlay, 0xFFFFFFFF);
                matricesNew.popPose();
            });
        }

        storedMatrixTransformations.add(matricesNew -> matricesNew.translate(open * (side ? -1 : 1), 0, 0));

        if (end) {
            RenderTrains.scheduleRender(DoorTextureCache.psdEnd(half, side, 1), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                (side ? MODEL_PSD_END_RIGHT_1 : MODEL_PSD_END_LEFT_1).renderToBuffer(matricesNew, vertexConsumer, light, overlay, 0xFFFFFFFF);
                matricesNew.popPose();
            });
        } else {
            RenderTrains.scheduleRender(DoorTextureCache.psd(half, side), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                MODEL_PSD.renderToBuffer(matricesNew, vertexConsumer, light, overlay, 0xFFFFFFFF);
                matricesNew.popPose();
            });
        }
        if (half && !unlocked) {
            RenderTrains.scheduleRender(DoorTextureCache.LOCKED, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                if (end) {
                    matricesNew.translate(side ? 0.25 : -0.25, 0, 0);
                }
                MODEL_PSD_DOOR_LOCKED.renderToBuffer(matricesNew, vertexConsumer, light, overlay, 0xFFFFFFFF);
                matricesNew.popPose();
            });
        }
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }
}
