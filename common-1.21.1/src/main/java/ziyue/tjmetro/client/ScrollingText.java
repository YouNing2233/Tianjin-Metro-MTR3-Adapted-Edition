package ziyue.tjmetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.MTRClient;
import mtr.client.IDrawing;
import mtr.render.MoreRenderLayers;
import mtr.data.IGui;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

import java.util.function.Supplier;

/**
 * @author ZiYueCommentary
 * @see mtr.client.ScrollingText
 * @since 1.0.0
 */
public class ScrollingText implements IGui {

    protected float ticksOffset;
    protected Supplier<ClientCache.DynamicResource> imageSupplier;
    protected VertexConsumer vertexConsumer;

    protected final float availableWidth;
    protected final float availableHeight;
    protected final int scrollSpeed;
    protected final boolean isFullPixel;
    public static final float EPSILON = 0.03F;

    public ScrollingText(float availableWidth, float availableHeight, int scrollSpeed, boolean isFullPixel) {
        this.availableWidth = availableWidth;
        this.availableHeight = availableHeight;
        this.scrollSpeed = scrollSpeed;
        this.isFullPixel = isFullPixel;
    }

    public void changeImage(Supplier<ClientCache.DynamicResource> imageSupplier) {
        if (this.imageSupplier != imageSupplier) {
            this.imageSupplier = imageSupplier;
            ticksOffset = (float) MTRClient.getGameTick();
        }
    }

    public void setVertexConsumer(MultiBufferSource vertexConsumers) {
        if (imageSupplier != null && imageSupplier.get() != null) {
            vertexConsumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(imageSupplier.get().resourceLocation, true));
        } else {
            vertexConsumer = null;
        }
    }

    /**
     * Renders scrolling text. Returns true if the scroll has completed a full cycle,
     * indicating the slide should switch to the next advertisement.
     */
    public boolean scrollText(PoseStack matrices, Direction facing) {
        if (imageSupplier != null && imageSupplier.get() != null && vertexConsumer != null) {
            final ClientCache.DynamicResource dynamicResource = imageSupplier.get();
            final int pixelScale = isFullPixel ? 1 : mtr.client.RouteMapGenerator.PIXEL_SCALE;
            final float scale = availableHeight / dynamicResource.height;
            final int widthSteps = (int) Math.floor(availableWidth / scale / pixelScale);
            final int imageSteps = dynamicResource.width / pixelScale;
            final int totalSteps = widthSteps + imageSteps;
            final int step = (int) (Math.round((MTRClient.getGameTick() - ticksOffset) * scrollSpeed) % totalSteps);
            final float width = Math.min(Math.min(availableWidth, dynamicResource.width * scale), Math.min(step * pixelScale * scale, (totalSteps - step) * pixelScale * scale));
            final float u1 = Math.max((float) (step - widthSteps) / imageSteps, 0);
            final float u2 = Math.min((float) step / imageSteps, 1);
            IDrawing.drawTexture(matrices, vertexConsumer, Math.max(widthSteps - step, 0) * scale * pixelScale, 0, width, availableHeight, u1, 0, u2, 1, facing, ARGB_WHITE, MAX_LIGHT_GLOWING);
            return (1.0F - u1) <= EPSILON && (1.0F - u2) <= EPSILON;
        }
        return true;
    }
}