package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import ziyue.tjmetro.block.BlockAPGDoorTianjinTRT;
import ziyue.tjmetro.block.BlockAPGGlassTianjinTRT;

/**
 * Custom renderer for the Tianjin TRT (Line 1) APG door (half-height).
 * <p>
 * The door light strip is baked into the block model ({@code light} block state
 * property), so this renderer keeps the {@code light} state in sync with the
 * door opening and the neighboring glass panel, then delegates to
 * {@link RenderTianjinAPGDoor} for the door panels.
 *
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.block.BlockAPGDoorTianjinTRT
 * @since 1.1.6 - Ported from 4.0
 */
public class RenderAPGDoorTianjinTRT<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends RenderTianjinAPGDoor<T>
{
    public RenderAPGDoorTianjinTRT(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, "apg_door_tianjin_trt", null, true);
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

        // Traditional lights, baked into the block model.
        if (half) {
            final Block sideBlock = world.getBlockState(pos.relative(side ? facing.getClockWise() : facing.getCounterClockWise())).getBlock();
            if (sideBlock instanceof BlockAPGGlassTianjinTRT) {
                tryUpdateLightState(world, pos, DoorAnimationState.getOpen(entity, world) > 0 ? BlockAPGDoorTianjinTRT.LightProperty.LIGHT_ON : BlockAPGDoorTianjinTRT.LightProperty.LIGHT_OFF);
            } else {
                tryUpdateLightState(world, pos, BlockAPGDoorTianjinTRT.LightProperty.NO_LIGHT);
            }
        }

        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
    }

    private void tryUpdateLightState(Level world, BlockPos pos, BlockAPGDoorTianjinTRT.LightProperty lightProperty) {
        final net.minecraft.world.level.block.state.BlockState state = world.getBlockState(pos);
        // The block may already be replaced by air (e.g. while breaking); air has no LIGHT property.
        if (!(state.getBlock() instanceof BlockAPGDoorTianjinTRT)) {
            return;
        }
        if (IBlock.getStatePropertySafe(state, BlockAPGDoorTianjinTRT.LIGHT) != lightProperty) {
            world.setBlockAndUpdate(pos, state.setValue(BlockAPGDoorTianjinTRT.LIGHT, lightProperty));
        }
    }
}
