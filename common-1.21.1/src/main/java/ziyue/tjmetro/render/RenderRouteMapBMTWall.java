package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockStationNameBase;
import mtr.block.IBlock;
import mtr.client.ClientData;
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
import ziyue.tjmetro.block.BlockRouteMapBMTWall;
import ziyue.tjmetro.client.ClientCache;

import static mtr.data.IGui.MAX_LIGHT_GLOWING;

/**
 * Render content for the <b>wall-mounted BMT (Tianjin Metro Line 9) route map</b>.
 * <p>
 * Identical to {@link RenderRouteMapBMT} except for the depth of the drawn quad. The hanging sign
 * sits in the middle of its block (model {@code z} 7..9), so its texture is drawn one pixel in front
 * of the block centre; the wall sign is one pixel thick and flush against the supporting wall, so
 * its texture belongs on the front face at {@code z} = 1/16.
 * <p>
 * The transform chain mirrors {@link mtr.block.BlockStationNameWallBase}: the position is the block
 * centre and the Y rotation by {@code -FACING.toYRot()} maps local {@code +z} onto world {@code -z}
 * for {@code FACING == NORTH}, so a local {@code z} offset of {@code 0.4375} lands on world
 * {@code z} = 0.0625, exactly the visible face of the plate.
 *
 * @author ZiYueCommentary
 * @see BlockRouteMapBMTWall
 * @see RenderRouteMapBMT
 * @since 1.1.9
 */
public class RenderRouteMapBMTWall<T extends BlockRouteMapBMTWall.TileEntityRouteMapBMTWall> extends BlockEntityRendererMapper<T> implements IBlock, IGui, IDrawing
{
    /**
     * Local z offset that places the quad on the front face of the one-pixel-thick plate.
     */
    private static final float WALL_Z_OFFSET = 0.4375F;

    public RenderRouteMapBMTWall(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStationNameBase.FACING);
        final IBlock.EnumSide side = IBlock.getStatePropertySafe(state, IBlock.SIDE);

        // Do not render anything when the platform is not (or no longer) available.
        if (ClientData.DATA_CACHE.platformIdMap.get(entity.getPlatformId()) == null) return;

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), 0.71 + pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(-0.25, 0, WALL_Z_OFFSET - SMALL_OFFSET);
        });

        RenderTrains.scheduleRender(ClientCache.DATA_CACHE.getRouteMapBMT(entity.getPlatformId(), side == IBlock.EnumSide.LEFT, 2.5F / 1.66F, 0xff00379c).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, 0, 2.5F, 1.66F, 0, 0, 1, 1, facing, -1, MAX_LIGHT_GLOWING);
            matricesNew.popPose();
        });
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }
}
