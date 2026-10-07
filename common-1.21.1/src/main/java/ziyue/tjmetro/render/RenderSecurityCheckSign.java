package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import mtr.block.BlockStationNameBase;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.Text;
import mtr.render.RenderTrains;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.block.BlockSecurityCheckSign;
import ziyue.tjmetro.client.IDrawingExtends;

/**
 * Render contents for <b>Security Check Sign</b>.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.block.BlockSecurityCheckSign
 * @since 1.1.8
 */

public class RenderSecurityCheckSign<T extends BlockSecurityCheckSign.TileEntitySecurityCheckSign> extends BlockEntityRendererMapper<T> implements IGui, IDrawing
{
    public RenderSecurityCheckSign(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (!entity.shouldRender()) return;

        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);
        if (RenderTrains.shouldNotRender(pos, RenderTrains.maxTrainRenderDistance, null)) return;

        matrices.pushPose();
        matrices.translate(0.5, 0.5 + entity.yOffset, 0.5);
        matrices.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        matrices.mulPose(Axis.ZP.rotationDegrees(180));
        matrices.translate(0, 0.18, 0.5 - entity.zOffset - SMALL_OFFSET);
        final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(new com.mojang.blaze3d.vertex.ByteBufferBuilder(1536));
        IDrawingExtends.drawStringWithFont(matrices, Minecraft.getInstance().font, immediate, Text.translatable("sign.tjmetro.security_check").getString(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -0.105f, 0.85f, 1f, 140, ARGB_WHITE, false, light, null);
        IDrawingExtends.drawStringWithFont(matrices, Minecraft.getInstance().font, immediate, Text.translatable("gui.tjmetro.accept_security_check_cjk").getString(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, 0.043f, 0.85f, 1f, 270, ARGB_WHITE, false, light, null);
        immediate.endBatch();
        matrices.popPose();
    }
}
