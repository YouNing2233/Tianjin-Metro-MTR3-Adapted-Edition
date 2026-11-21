package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockPSDTop;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.block.BlockPSDTopTianjin;
import ziyue.tjmetro.block.BlockPSDTopTianjinWide;
import ziyue.tjmetro.client.ClientCache;

import static mtr.render.RenderRouteBase.getShadingColor;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

/**
 * Renderer for {@link BlockPSDTopTianjinWide}.
 * <p>
 * Every plate of a group draws its own slice of one single texture, so a run of three
 * plates shows one continuous board (station name centered, arrows at both ends) instead
 * of three independent boards.
 *
 * @author ZiYueCommentary
 * @see BlockPSDTopTianjinWide
 * @since 1.1.6
 */
public class RenderPSDTopTianjinWide extends BlockEntityRendererMapper<BlockPSDTopTianjinWide.TileEntityPSDTopTianjinWide> implements IGui
{
    protected static final float Z = 1.9995F / 16F;
    protected static final float FRONT_OFFSET = -0.004F;
    protected static final float TOP_PADDING = 7.5F / 16F;
    protected static final float BOTTOM_PADDING = 1.5F / 16F;
    protected static final float COLOR_STRIP_START = 14.5F / 16F;
    protected static final float COLOR_STRIP_END = 15F / 16F;
    protected static final int MAX_GROUP_SIZE = 8;

    public RenderPSDTopTianjinWide(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockPSDTopTianjinWide.TileEntityPSDTopTianjinWide entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) {
            return;
        }

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockPSDTopTianjinWide)) {
            return;
        }

        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final int index = countPlates(world, pos, facing.getCounterClockWise());
        final int after = countPlates(world, pos, facing.getClockWise());
        final int total = index + after + 1;

        if (RenderTrains.shouldNotRender(pos, RenderTrains.maxTrainRenderDistance, null)) {
            return;
        }

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations();
        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0.5 + pos.getX(), pos.getY(), 0.5 + pos.getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
        });

        final long platformId = entity.getPlatformId(ClientData.PLATFORMS, ClientData.DATA_CACHE);
        if (platformId == 0) {
            return;
        }

        storedMatrixTransformations.add(matricesNew -> {
            matricesNew.translate(0, 1, 0);
            UtilitiesClient.rotateZDegrees(matricesNew, 180);
            matricesNew.translate(-0.5, -getAdditionalOffset(state), Z);
        });

        final float height = 1 - TOP_PADDING - BOTTOM_PADDING;
        final float width = total * 0.95F;
        final float aspectRatio = width / height;
        final int color = getShadingColor(facing, ARGB_WHITE);

        final BlockPSDTopTianjin.EnumPSDType style = IBlock.getStatePropertySafe(state, BlockPSDTopTianjin.STYLE);
        final int arrowDirection = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
        final ResourceLocation texture;
        if (style == BlockPSDTopTianjin.EnumPSDType.NEXT_STATION) {
            texture = ClientCache.DATA_CACHE.getNextStationJinjing(platformId, arrowDirection, 0.25F, aspectRatio, ARGB_WHITE, ARGB_BLACK, ARGB_WHITE).resourceLocation;
        } else {
            texture = ClientCache.DATA_CACHE.getStationName(platformId, false, IGui.HorizontalAlignment.CENTER, 0.25F, aspectRatio, ARGB_WHITE, ARGB_BLACK, ARGB_WHITE).resourceLocation;
        }

        final float u0 = index / (float) total;
        final float u1 = (index + 1F) / total;
        RenderTrains.scheduleRender(texture, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, TOP_PADDING, FRONT_OFFSET, 1, 1 - BOTTOM_PADDING, FRONT_OFFSET, u0, 0, u1, 1, facing.getOpposite(), color, light);
            matricesNew.popPose();
        });

        RenderTrains.scheduleRender(ClientData.DATA_CACHE.getColorStrip(platformId).resourceLocation, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            storedMatrixTransformations.transform(matricesNew);
            IDrawing.drawTexture(matricesNew, vertexConsumer, 0, COLOR_STRIP_START, FRONT_OFFSET, 1, COLOR_STRIP_END, FRONT_OFFSET, u0, 0, u1, 1, facing.getOpposite(), color, light);
            matricesNew.popPose();
        });
    }

    /**
     * Counts consecutive wide plates from {@code pos} towards {@code direction}.
     */
    private static int countPlates(Level world, BlockPos pos, Direction direction) {
        for (int i = 1; i <= MAX_GROUP_SIZE; i++) {
            if (!(world.getBlockState(pos.relative(direction, i)).getBlock() instanceof BlockPSDTopTianjinWide)) {
                return i - 1;
            }
        }
        return MAX_GROUP_SIZE;
    }

    private static float getAdditionalOffset(BlockState state) {
        return IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == BlockPSDTop.EnumPersistent.NONE ? 0 : BlockPSDTop.PERSISTENT_OFFSET_SMALL;
    }
}
