package ziyue.tjmetro.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.entity.EntitySeat;
import mtr.render.RenderTrains;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ziyue.tjmetro.client.Config;

/**
 * Allows the player to skip train rendering for performance.
 * Ported from MTR4 edition (disable_train_rendering).
 *
 * @since 1.1.2.1
 */

@Mixin(RenderTrains.class)
public abstract class RenderTrainsMixin
{
    @Inject(at = @At("HEAD"), method = "render(Lmtr/entity/EntitySeat;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V", cancellable = true, remap = false, require = 0)
    private static void beforeRender(EntitySeat entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        if (Config.DISABLE_TRAIN_RENDERING.get()) {
            ci.cancel();
        }
    }
}