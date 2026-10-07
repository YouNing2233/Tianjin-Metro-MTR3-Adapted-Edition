package ziyue.tjmetro.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 按「纹理区域」绘制的图标按钮。
 * <p>
 * MC 1.21 把 {@code ImageButton} 改成了基于 GUI 图集精灵（{@code WidgetSprites} / {@code blitSprite}）的实现，
 * 只能引用 {@code textures/gui/sprites/**} 下的整张图集精灵。而 MTR 的翻页图标
 * （{@code mtr:textures/gui/icon_left.png} / {@code icon_right.png}）是 20×40 的普通纹理，
 * 上半 20×20 是常态、下半 20×20 是悬停态——直接塞给 {@code WidgetSprites} 会找不到精灵，
 * 渲染成黑紫块。因此这里退回到 1.18.2 时期的做法：直接按 UV 区域 blit 原纹理。
 *
 * @since 1.1.2.7
 */

public class TianjinImageButton extends Button
{
    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int hoverVOffset;
    private final int textureWidth;
    private final int textureHeight;

    public TianjinImageButton(int x, int y, int width, int height, int u, int v, int hoverVOffset, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.hoverVOffset = hoverVOffset;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 不调用 super：那会先画一遍原版的按钮底图。
        final int vOffset = this.v + (isHoveredOrFocused() ? this.hoverVOffset : 0);
        guiGraphics.blit(this.texture, getX(), getY(), (float) this.u, (float) vOffset, getWidth(), getHeight(), this.textureWidth, this.textureHeight);
    }
}
