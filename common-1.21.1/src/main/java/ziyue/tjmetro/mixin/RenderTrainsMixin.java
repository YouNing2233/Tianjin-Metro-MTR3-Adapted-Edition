package ziyue.tjmetro.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
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
 * <p>
 * NeoMTR 把列车渲染从「每个 EntitySeat 一个 BlockEntityRenderer」改成了单个全局静态入口
 * {@code RenderTrains.render(float, PoseStack, MultiBufferSource)}，原先的
 * {@code mtr.entity.EntitySeat} 已被移除，因此这里改为直接拦这个全局入口，
 * 语义反而更干净：一次 cancel 就跳过整趟列车渲染。
 * <p>
 * {@code RenderTrains} 中只有这一个 {@code render} 方法（不存在重载），所以按方法名注入即可，
 * 不必写描述符——描述符在各平台的命名空间不一样（Fabric 生产环境是 intermediary）。
 *
 * @since 1.1.2.1
 */

@Mixin(RenderTrains.class)
public abstract class RenderTrainsMixin
{
    @Inject(at = @At("HEAD"), method = "render", cancellable = true, remap = false, require = 0)
    private static void beforeRender(float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        if (Config.DISABLE_TRAIN_RENDERING.get()) {
            ci.cancel();
        }
    }
}