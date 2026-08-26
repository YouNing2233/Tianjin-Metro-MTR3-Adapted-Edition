package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.BlockAPGGlass;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import ziyue.tjmetro.block.BlockAPGGlassTianjinTRT;
import ziyue.tjmetro.client.ClientCache;

import static mtr.render.RenderRouteBase.getShadingColor;

/**
 * Custom renderer for the Tianjin TRT (Line 1) APG glass panel (half-height).
 * <p>
 * Renders the TRT route map texture, matching MTR 4.0.
 *
 * @author ZiYueCommentary
 * @see BlockAPGGlassTianjinTRT
 * @see RenderRouteBase
 * @since 1.1.6 - Ported from 4.0
 */
public class RenderAPGGlassTianjinTRT extends RenderRouteBase<BlockAPGGlassTianjinTRT.TileEntityAPGGlassTianjinTRT>
{
    public RenderAPGGlassTianjinTRT(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, 4, 9, 3, 8, true, BlockAPGGlass.ARROW_DIRECTION);
    }

    @Override
    protected RenderType getRenderType(BlockGetter world, BlockPos pos, BlockState state) {
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.LOWER) {
            return RenderType.NONE;
        } else {
            return RenderType.ROUTE;
        }
    }

    @Override
    public void render(BlockAPGGlassTianjinTRT.TileEntityAPGGlassTianjinTRT entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        final Direction facing = IBlock.getStatePropertySafe(state, HorizontalDirectionalBlock.FACING);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
        });

        renderAdditionalUnmodified(storedMatrixTransformations.copy(), state, facing, light);

        if (!RenderTrains.shouldNotRender(pos, RenderTrains.maxTrainRenderDistance, null)) {
            final long platformId = entity.getPlatformId(ClientData.PLATFORMS, ClientData.DATA_CACHE);

            if (platformId != 0) {
                storedMatrixTransformations.add(matricesNew -> {
                    matricesNew.translate(0, 1, 0);
                    UtilitiesClient.rotateZDegrees(matricesNew, 180);
                    matricesNew.translate(-0.5, -getAdditionalOffset(state), z);
                });

                final int leftBlocks = getTextureNumber(world, pos, facing, true);
                final int rightBlocks = getTextureNumber(world, pos, facing, false);
                final int color = getShadingColor(facing, ARGB_WHITE);
                final RenderType renderType = getRenderType(world, pos.relative(facing.getCounterClockWise(), leftBlocks), state);

                if ((renderType != RenderType.NONE) && IBlock.getStatePropertySafe(state, SIDE_EXTENDED) != IBlock.EnumSide.SINGLE) {
                    final float width = leftBlocks + rightBlocks + 1 - sidePadding * 2;
                    final float height = 1 - topPadding - bottomPadding;
                    final int arrowDirection = IBlock.getStatePropertySafe(state, arrowDirectionProperty);

                    final ResourceLocation resourceLocation = ClientCache.DATA_CACHE.getRouteMapTRT(platformId, false, arrowDirection == 2, width / height, transparentWhite).resourceLocation;

                    RenderTrains.scheduleRender(resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                        storedMatrixTransformations.transform(matricesNew);
                        IDrawing.drawTexture(matricesNew, vertexConsumer, leftBlocks == 0 ? sidePadding : 0, topPadding, 0, 1 - (rightBlocks == 0 ? sidePadding : 0), 1 - bottomPadding, 0, (leftBlocks - (leftBlocks == 0 ? 0 : sidePadding)) / width, 0, (width - rightBlocks + (rightBlocks == 0 ? 0 : sidePadding)) / width, 1, facing.getOpposite(), color, light);
                        matricesNew.popPose();
                    });
                }

                renderAdditional(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing.getOpposite(), color, light);
            }
        }
    }

    @Override
    protected void renderAdditional(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {
    }
}
