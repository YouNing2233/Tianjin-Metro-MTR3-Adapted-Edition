package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.MTRClient;
import mtr.block.BlockAPGGlass;
import mtr.block.BlockAPGGlassEnd;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * Custom renderer for Tianjin APG doors.
 * Supports different texture prefixes and lighting modes (blockstate for TRT, texture for others).
 *
 * @since 1.1.6
 */
public class RenderTianjinAPGDoor<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends BlockEntityRendererMapper<T> {

    private final String doorTexturePrefix;
    private final String lightTexturePrefix;
    private final boolean useBlockStateLight;

    private static final ModelSingleCube MODEL_APG_TOP = new ModelSingleCube(34, 9, 0, 8, 1, 16, 8, 1);
    private static final ModelAPGDoorBottom MODEL_APG_BOTTOM = new ModelAPGDoorBottom();
    private static final ModelSingleCube MODEL_APG_DOOR_LOCKED = new ModelSingleCube(6, 6, 5, 10, 1, 6, 6, 0);
    private static final ModelAPGDoorLight MODEL_APG_LIGHT = new ModelAPGDoorLight();

    public RenderTianjinAPGDoor(BlockEntityRenderDispatcher dispatcher, String doorTexturePrefix, String lightTexturePrefix, boolean useBlockStateLight) {
        super(dispatcher);
        this.doorTexturePrefix = doorTexturePrefix;
        this.lightTexturePrefix = lightTexturePrefix;
        this.useBlockStateLight = useBlockStateLight;
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
        final boolean unlocked = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.UNLOCKED);
        final float open = Math.min(entity.getOpen(MTRClient.getLastFrameDuration()), 1);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateXDegrees(matricesNew, 180);
        });
        final StoredMatrixTransformations storedMatrixTransformationsLight = storedMatrixTransformations.copy();

        if (half) {
            final Block sideBlock = world.getBlockState(pos.relative(side ? facing.getClockWise() : facing.getCounterClockWise())).getBlock();
            final boolean hasGlassNeighbor = sideBlock instanceof BlockAPGGlass || sideBlock instanceof BlockAPGGlassEnd;

            if (!useBlockStateLight && lightTexturePrefix != null && hasGlassNeighbor) {
                RenderTrains.scheduleRender(new ResourceLocation(String.format("tjmetro:textures/block/%s_light_%s.png", lightTexturePrefix, open > 0 ? "on" : "off")), false, open > 0 ? RenderTrains.QueuedRenderLayer.LIGHT_TRANSLUCENT : RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                    storedMatrixTransformationsLight.transform(matricesNew);
                    matricesNew.translate(side ? -0.515625 : 0.515625, 0, 0);
                    matricesNew.scale(0.5F, 1, 1);
                    MODEL_APG_LIGHT.renderToBuffer(matricesNew, vertexConsumer, light, overlay, 1, 1, 1, 1);
                    matricesNew.popPose();
                });
            }
        }

        storedMatrixTransformations.add(matricesNew -> matricesNew.translate(open * (side ? -1 : 1), 0, 0));

        RenderTrains.scheduleRender(new ResourceLocation(String.format("tjmetro:textures/block/%s_%s_%s.png", doorTexturePrefix, half ? "top" : "bottom", side ? "right" : "left")), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            (half ? MODEL_APG_TOP : MODEL_APG_BOTTOM).renderToBuffer(matricesNew, vertexConsumer, light, overlay, 1, 1, 1, 1);
            matricesNew.popPose();
        });

        if (half && !unlocked) {
            RenderTrains.scheduleRender(new ResourceLocation("mtr:textures/block/sign/door_not_in_use.png"), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                MODEL_APG_DOOR_LOCKED.renderToBuffer(matricesNew, vertexConsumer, light, overlay, 1, 1, 1, 1);
                matricesNew.popPose();
            });
        }
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }


}
