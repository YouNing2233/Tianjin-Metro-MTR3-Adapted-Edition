package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.render.RenderTrains;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.block.BlockTimeDisplayEven;
import ziyue.tjmetro.client.IDrawingExtends;

/**
 * Renderer for the even (double-width) time display.
 * <p>
 * Unlike the normal {@link RenderTimeDisplay}, which draws the time on both the front and the back,
 * each half of the even display only draws a single text, offset toward its own screen half
 * (x = 0.516 instead of the centered 0).
 *
 * @author ZiYueCommentary
 * @see BlockTimeDisplayEven
 * @see RenderTimeDisplay
 * @since 1.1.8 - Ported from 4.0
 */
public class RenderTimeDisplayEven<T extends BlockTimeDisplayEven.TileEntityTimeDisplayEven> extends BlockEntityRendererMapper<T> implements IGui, IDrawing
{
    public RenderTimeDisplayEven(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float f, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (!entity.shouldRender()) return;

        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        final Direction facing = IBlock.getStatePropertySafe(state, BlockTimeDisplayEven.FACING);
        if (RenderTrains.shouldNotRender(pos, RenderTrains.maxTrainRenderDistance, null)) return;

        matrices.pushPose();
        matrices.translate(0.5, 0.5 + entity.yOffset, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
        matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
        final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        matrices.pushPose();
        matrices.translate(0.516, 0, 0.5 - entity.zOffset - SMALL_OFFSET);
        IDrawingExtends.drawStringWithFont(matrices, Minecraft.getInstance().font, immediate, RenderTimeDisplay.getFormattedTime(entity.getLevel().getDayTime()), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, HorizontalAlignment.CENTER, 0, 0, -1, -1, 30, -3276781, false, light, true, null);
        matrices.popPose();
        immediate.endBatch();
        matrices.popPose();
    }
}
