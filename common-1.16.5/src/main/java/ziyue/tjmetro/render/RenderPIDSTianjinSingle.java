package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.IBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import ziyue.tjmetro.block.BlockPIDSTianjin;
import ziyue.tjmetro.block.BlockPIDSTianjinSingle;

/**
 * Renderer for the single-row (single-face) Tianjin PIDS.
 * Unlike the double-sided main screen it only draws the front face; the dummy half
 * ({@code should_render=false}) is skipped because {@code canStoreData} is false there.
 * The {@code rotate90=false} flag mirrors the upstream registration for this variant.
 *
 * <p>Port of the single-face behaviour from the upstream Mapping API implementation
 * ({@code RenderPIDSTianjin} + {@code BlockPIDSTianjinSingle.SHOULD_RENDER}).</p>
 *
 * @author ZiYueCommentary (original Mapping-API implementation)
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see RenderPIDSTianjin
 * @see BlockPIDSTianjinSingle
 */
public class RenderPIDSTianjinSingle extends RenderPIDSTianjin<BlockPIDSTianjinSingle.TileEntityBlockPIDSTianjinSingle> {

    public RenderPIDSTianjinSingle(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, 2.5F, 30, false, 1F);
    }

    @Override
    public void render(BlockPIDSTianjinSingle.TileEntityBlockPIDSTianjinSingle entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockPIDSTianjin)) return;
        BlockPIDSTianjin pidsBlock = (BlockPIDSTianjin) block;
        if (!pidsBlock.canStoreData(world, pos)) return;

        final Direction facing = IBlock.getStatePropertySafe(world, pos, HorizontalDirectionalBlock.FACING);
        final float scale = 320 / maxHeight * textPadding;

        renderFace(matrices, vertexConsumers, facing, scale, entity, pos, 0, 0);
    }
}
