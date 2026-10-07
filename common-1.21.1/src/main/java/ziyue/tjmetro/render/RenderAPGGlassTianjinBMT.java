package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.BlockAPGGlass;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
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
import ziyue.tjmetro.block.BlockAPGGlassTianjinBMT;
import ziyue.tjmetro.client.ClientCache;

import static mtr.render.RenderRouteBase.getShadingColor;
import static ziyue.tjmetro.block.BlockAPGGlassTianjinBMT.STYLE;

/**
 * Custom renderer for the Tianjin BMT-line APG glass panel (half-height).
 * <p>
 * Renders the route map / station name / next station texture selected by the
 * {@code style} block state, plus the color strip and the single-row station
 * name, matching MTR 4.0.
 *
 * @author ZiYueCommentary
 * @see BlockAPGGlassTianjinBMT
 * @see RenderRouteBase
 * @since 1.1.6 - Ported from 4.0
 */
public class RenderAPGGlassTianjinBMT extends RenderRouteBase<BlockAPGGlassTianjinBMT.TileEntityAPGGlassTianjinBMT>
{
    public static final float COLOR_STRIP_START = 15F / 16;
    public static final float COLOR_STRIP_END = 1F;

    public RenderAPGGlassTianjinBMT(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, 4, 8F, 1F, 8, false, BlockAPGGlass.ARROW_DIRECTION);
    }

    @Override
    protected RenderType getRenderType(BlockGetter world, BlockPos pos, BlockState state) {
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.LOWER) {
            return RenderType.NONE;
        } else {
            return RenderType.ARROW;
        }
    }

    @Override
    public void render(BlockAPGGlassTianjinBMT.TileEntityAPGGlassTianjinBMT entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
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
                final BlockAPGGlassTianjinBMT.EnumDoorType style = IBlock.getStatePropertySafe(state, STYLE);

                if ((renderType != RenderType.NONE) && IBlock.getStatePropertySafe(state, SIDE_EXTENDED) != IBlock.EnumSide.SINGLE) {
                    final float width = leftBlocks + rightBlocks + 1 - sidePadding * 2;
                    final float height = 1 - topPadding - bottomPadding;
                    final int arrowDirection = IBlock.getStatePropertySafe(state, arrowDirectionProperty);

                    final ResourceLocation resourceLocation;
                    switch (style) {
                        case ROUTE:
                            resourceLocation = ClientCache.DATA_CACHE.getRouteMap(platformId, false, arrowDirection == 2, width / height, transparentWhite).resourceLocation;
                            break;
                        case STATION_NAME:
                            resourceLocation = ClientCache.DATA_CACHE.getStationName(platformId, true, IGui.HorizontalAlignment.CENTER, 0.25F, width / height, ARGB_WHITE, ARGB_BLACK, transparentWhite ? ARGB_WHITE : 0).resourceLocation;
                            break;
                        default:
                            resourceLocation = ClientCache.DATA_CACHE.getNextStation(platformId, arrowDirection, 0.25F, width / height, ARGB_WHITE, ARGB_BLACK, transparentWhite ? ARGB_WHITE : 0).resourceLocation;
                            break;
                    }

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
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER && IBlock.getStatePropertySafe(state, SIDE_EXTENDED) != IBlock.EnumSide.SINGLE) {
            final boolean isLeft = isLeft(state);
            final boolean isRight = isRight(state);
            RenderTrains.scheduleRender(ClientData.DATA_CACHE.getColorStrip(platformId).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                IDrawing.drawTexture(matricesNew, vertexConsumer, isLeft ? sidePadding : 0, COLOR_STRIP_START, 0, isRight ? 1 - sidePadding : 1, COLOR_STRIP_END, 0, facing, color, light);
                IDrawing.drawTexture(matricesNew, vertexConsumer, isRight ? 1 - sidePadding : 1, COLOR_STRIP_START, 0.125F, isLeft ? sidePadding : 0, COLOR_STRIP_END, 0.125F, facing, color, light);
                matricesNew.popPose();
            });

            final float width = leftBlocks + rightBlocks + 1 - sidePadding * 2;
            final float height = 1 - topPadding - bottomPadding;
            RenderTrains.scheduleRender(ClientCache.DATA_CACHE.getSingleRowStationName(platformId, width / height).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                storedMatrixTransformations.transform(matricesNew);
                IDrawing.drawTexture(matricesNew, vertexConsumer, 1 - (rightBlocks == 0 ? sidePadding : 0), topPadding, 0.125F, leftBlocks == 0 ? sidePadding : 0, 1 - bottomPadding, 0.125F, (rightBlocks - (rightBlocks == 0 ? 0 : sidePadding)) / width, 0, (width - leftBlocks + (leftBlocks == 0 ? 0 : sidePadding)) / width, 1, facing, color, light);
                matricesNew.popPose();
            });
        }
    }
}
