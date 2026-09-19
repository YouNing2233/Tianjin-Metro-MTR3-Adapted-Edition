package ziyue.tjmetro.fabric.mixin;

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
 * Fabric production build uses intermediary names at runtime, and the method reference of an
 * {@code @Inject} is a string constant that is not remapped by remapJar. This variant therefore
 * references the intermediary descriptor explicitly; on Forge (and in a named dev environment)
 * the equivalent mixin in the common module applies instead.
 *
 * @since 1.1.2.1
 */

@Mixin(RenderTrains.class)
public abstract class RenderTrainsFabricMixin
{
    @Inject(
            at = @At("HEAD"),
            method = "render(Lmtr/entity/EntitySeat;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;)V",
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void beforeRender(EntitySeat entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        if (Config.DISABLE_TRAIN_RENDERING.get()) {
            ci.cancel();
        }
    }
}