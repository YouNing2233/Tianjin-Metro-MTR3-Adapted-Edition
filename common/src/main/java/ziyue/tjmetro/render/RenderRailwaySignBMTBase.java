package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.MTR;
import mtr.block.BlockStationNameBase;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.block.base.BlockRailwaySignBase;

import java.util.Set;

import static mtr.render.RenderRailwaySign.getMaxWidth;
import static mtr.render.RenderRailwaySign.getSign;

/**
 * Renderer for BMT railway signs, station navigators, and route maps.
 * Reuses the same rendering logic as RenderRailwaySignTianjin
 * but accepts the base TileEntityRailwaySign type.
 *
 * @author ZiYueCommentary
 * @since 1.1.1
 */
public class RenderRailwaySignBMTBase<T extends BlockRailwaySignBase.TileEntityRailwaySign> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing
{
    public static final int BACKGROUND_COLOR = 0xff00379c;

    public RenderRailwaySignBMTBase(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockRailwaySignBase block)) return;

        final String[] signIds = entity.getSignIds();
        if (signIds.length != block.length) return;

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), 0.53125 + pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2);
        });

        matrices.pushPose();
        matrices.translate(0.5, 0.53125, 0.5);
        UtilitiesClient.rotateYDegrees(matrices, -facing.toYRot());
        UtilitiesClient.rotateZDegrees(matrices, 180);
        matrices.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2);

        // Render background
        RenderTrains.scheduleRender(new ResourceLocation(MTR.MOD_ID, "textures/block/white.png"), false, RenderTrains.QueuedRenderLayer.LIGHT, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, SMALL_OFFSET, 0.5F * signIds.length, 0.5F, SMALL_OFFSET, facing, BACKGROUND_COLOR, MAX_LIGHT_GLOWING);
            matricesNew.popPose();
        });

        // Render each sign using the same logic as RenderRailwaySignTianjin
        for (int i = 0; i < signIds.length; i++) {
            if (signIds[i] != null) {
                RenderRailwaySignTianjin.drawSign(matrices, vertexConsumers, storedMatrixTransformations, Minecraft.getInstance().font, pos, signIds[i], 0.5F * i, 0, 0.5F, getMaxWidth(signIds, i, false), getMaxWidth(signIds, i, true), entity.getSelectedIds(), facing, BACKGROUND_COLOR, (textureId, x, y, size, flipTexture) -> RenderTrains.scheduleRender(new ResourceLocation(textureId.toString()), true, RenderTrains.QueuedRenderLayer.LIGHT_TRANSLUCENT, (matricesNew, vertexConsumer) -> {
                    storedMatrixTransformations.transform(matricesNew);
                    IDrawing.drawTexture(matricesNew, vertexConsumer, x, y, size, size, flipTexture ? 1 : 0, 0, flipTexture ? 0 : 1, 1, facing, -1, MAX_LIGHT_GLOWING);
                    matricesNew.popPose();
                }));
            }
        }

        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }
}
