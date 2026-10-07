package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.BlockPSDAPGGlassEndBase;
import mtr.block.BlockPSDTop;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.block.BlockPSDTopTianjinBMT;
import ziyue.tjmetro.client.ClientCache;

import static mtr.render.RenderRouteBase.getShadingColor;

/**
 * Custom renderer for the Tianjin BMT-line PSD top plate.
 * <p>
 * Texture selection matches MTR 4.0: {@code getStationNameBMT} for the station
 * name and {@code getRouteMapPSDBMT} for the route map.
 *
 * @author ZiYueCommentary
 * @see BlockPSDTopTianjinBMT
 * @see RenderRouteBase
 * @since 1.1.6 - Ported from 4.0
 */
public class RenderPSDTopTianjinBMT extends RenderRouteBase<BlockPSDTopTianjinBMT.TileEntityPSDTopTianjinBMT>
{
    protected static final float END_FRONT_OFFSET = 1 / (Mth.SQRT_OF_TWO * 16);
    protected static final float BOTTOM_DIAGONAL_OFFSET = ((float) Math.sqrt(3) - 1) / 32;
    protected static final float ROOT_TWO_SCALED = Mth.SQRT_OF_TWO / 16;
    protected static final float BOTTOM_END_DIAGONAL_OFFSET = END_FRONT_OFFSET - BOTTOM_DIAGONAL_OFFSET / Mth.SQRT_OF_TWO;
    protected static final float COLOR_STRIP_START = 14.5F / 16;
    protected static final float COLOR_STRIP_END = 15 / 16F;

    public RenderPSDTopTianjinBMT(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, 2 - SMALL_OFFSET_16, 4.5F, 1.5F, 0.125F, true, BlockPSDTopTianjinBMT.ARROW_DIRECTION);
    }

    @Override
    public void render(BlockPSDTopTianjinBMT.TileEntityPSDTopTianjinBMT entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
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

                if ((renderType == RenderType.ARROW || renderType == RenderType.ROUTE) && IBlock.getStatePropertySafe(state, SIDE_EXTENDED) != IBlock.EnumSide.SINGLE) {
                    final float width = leftBlocks + rightBlocks + 1 - sidePadding * 2;
                    final float height = 1 - topPadding - bottomPadding;
                    final int arrowDirection = IBlock.getStatePropertySafe(state, arrowDirectionProperty);

                    final ResourceLocation resourceLocation;
                    if (renderType == RenderType.ARROW) {
                        resourceLocation = ClientCache.DATA_CACHE.getStationNameBMT(platformId, arrowDirection == 1, IGui.HorizontalAlignment.CENTER, 0.25F, width / height, ARGB_WHITE, ARGB_BLACK, transparentWhite ? ARGB_WHITE : 0).resourceLocation;
                    } else {
                        resourceLocation = ClientCache.DATA_CACHE.getRouteMapPSDBMT(platformId, arrowDirection == 1, width / height, transparentWhite).resourceLocation;
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
    protected RenderType getRenderType(BlockGetter world, BlockPos pos, BlockState state) {
        final Block blockBelow = world.getBlockState(pos.below()).getBlock();
        if (blockBelow instanceof BlockPSDAPGDoorBase) {
            return RenderType.ARROW;
        } else if (!(blockBelow instanceof BlockPSDAPGGlassEndBase)) {
            return RenderType.ROUTE;
        } else {
            return RenderType.NONE;
        }
    }

    @Override
    protected void renderAdditionalUnmodified(StoredMatrixTransformations storedMatrixTransformations, BlockState state, Direction facing, int light) {
        final boolean airLeft = IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_LEFT);
        final boolean airRight = IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_RIGHT);
        if (!airLeft && !airRight) return;
        RenderTrains.scheduleRender(new ResourceLocation("tjmetro:textures/block/psd_tianjin_bmt_top.png"), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            if (airLeft) {
                // back
                IDrawing.drawTexture(matricesNew, vertexConsumer, -0.125F, 0, 0.5F, 0.5F, 0, -0.125F, 0.5F, 1, -0.125F, -0.125F, 1, 0.5F, 0, 0, 1, 1, facing, -1, light);
                // front
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.25F - END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, -0.25F - END_FRONT_OFFSET, 1, 0.25F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 1, -0.5F - END_FRONT_OFFSET, 0, 0, 1, 0.9375F, facing.getOpposite(), -1, light);
                // top curve
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.25F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, -0.25F - END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0, 0.9375F, 1, 0.96875F, facing.getOpposite(), -1, light);
                // bottom curve
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.5F, 0, -0.5F, -0.25F, 0, 0.25F, -0.25F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0, 0.96875F, 1, 1, facing.getOpposite(), -1, light);
                // bottom
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.5F, SMALL_OFFSET, -0.125F, -0.125F, SMALL_OFFSET, 0.5F, -0.125F, SMALL_OFFSET, 0.125F, 0.5F, SMALL_OFFSET, -0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, facing, -1, light);
                // top
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.5F, 1 - SMALL_OFFSET, -0.5F, -0.125F, 1 - SMALL_OFFSET, 0.125F, -0.125F, 1 - SMALL_OFFSET, 0.5F, 0.5F, 1 - SMALL_OFFSET, -0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
                // top front
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.5F - END_FRONT_OFFSET, 1 - SMALL_OFFSET, -0.5F - END_FRONT_OFFSET, -0.125F - ROOT_TWO_SCALED, 1 - SMALL_OFFSET, 0.125F, -0.125F, 1 - SMALL_OFFSET, 0.125F, 0.5F, 1 - SMALL_OFFSET, -0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
                // left side diagonal
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.5F, 0.0625F, -0.5F, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 1, -0.5F - END_FRONT_OFFSET, 0.5F, 1, -0.5F, 0.9375F, 0, 1, 0.9375F, facing, -1, light);
                // left side diagonal square
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.5F, 0, -0.5F, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.5F, 0.0625F, -0.5F, 0.9375F, 0.9375F, 1, 1, facing, -1, light);
            }
            if (airRight) {
                // back
                IDrawing.drawTexture(matricesNew, vertexConsumer, -0.5F, 0, -0.125F, 0.125F, 0, 0.5F, 0.125F, 1, 0.5F, -0.5F, 1, -0.125F, 0, 0, 1, 1, facing, -1, light);
                // front
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.25F + END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.5F + END_FRONT_OFFSET, 1, -0.5F - END_FRONT_OFFSET, 0.25F + END_FRONT_OFFSET, 1, 0.25F - END_FRONT_OFFSET, 0, 0, 1, 0.9375F, facing.getOpposite(), -1, light);
                // top curve
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.25F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.25F + END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, 0, 0.9375F, 1, 0.96875F, facing.getOpposite(), -1, light);
                // bottom curve
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.25F, 0, 0.25F, -0.5F, 0, -0.5F, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0.25F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, 0, 0.96875F, 1, 1, facing.getOpposite(), -1, light);
                // bottom
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.125F, SMALL_OFFSET, 0.5F, -0.5F, SMALL_OFFSET, -0.125F, -0.5F, SMALL_OFFSET, -0.5F, 0.125F, SMALL_OFFSET, 0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, facing, -1, light);
                // top
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.125F, 1 - SMALL_OFFSET, 0.125F, -0.5F, 1 - SMALL_OFFSET, -0.5F, -0.5F, 1 - SMALL_OFFSET, -0.125F, 0.125F, 1 - SMALL_OFFSET, 0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
                // top front
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.125F + ROOT_TWO_SCALED, 1 - SMALL_OFFSET, 0.125F, -0.5F + END_FRONT_OFFSET, 1 - SMALL_OFFSET, -0.5F - END_FRONT_OFFSET, -0.5F, 1 - SMALL_OFFSET, -0.5F, 0.125F, 1 - SMALL_OFFSET, 0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
                // left side diagonal
                IDrawing.drawTexture(matricesNew, vertexConsumer, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.5F, 0.0625F, -0.5F, -0.5F, 1, -0.5F, -0.5F + END_FRONT_OFFSET, 1, -0.5F - END_FRONT_OFFSET, 0, 0, 0.0625F, 0.9375F, facing, -1, light);
                // left side diagonal square
                IDrawing.drawTexture(matricesNew, vertexConsumer, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F, 0, -0.5F, -0.5F, 0.0625F, -0.5F, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0, 0.9375F, 0.0625F, 1, facing, -1, light);
            }
            matricesNew.popPose();
        });
    }

    @Override
    protected void renderAdditional(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {
        final boolean airLeft = IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_LEFT);
        final boolean airRight = IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_RIGHT);
        RenderTrains.scheduleRender(ClientData.DATA_CACHE.getColorStrip(platformId).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, airLeft ? 0.625F : 0, COLOR_STRIP_START, 0, airRight ? 0.375F : 1, COLOR_STRIP_END, 0, facing, color, light);
            if (airLeft) {
                IDrawing.drawTexture(matricesNew, vertexConsumer, END_FRONT_OFFSET, COLOR_STRIP_START, -0.625F - END_FRONT_OFFSET, 0.75F + END_FRONT_OFFSET, COLOR_STRIP_END, 0.125F - END_FRONT_OFFSET, facing, -1, light);
            }
            if (airRight) {
                IDrawing.drawTexture(matricesNew, vertexConsumer, 0.25F - END_FRONT_OFFSET, COLOR_STRIP_START, 0.125F - END_FRONT_OFFSET, 1 - END_FRONT_OFFSET, COLOR_STRIP_END, -0.625F - END_FRONT_OFFSET, facing, -1, light);
            }
            matricesNew.popPose();
        });
    }
}
