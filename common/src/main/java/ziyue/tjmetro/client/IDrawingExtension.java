package ziyue.tjmetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.client.IDrawing;
import mtr.data.IGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

public interface IDrawingExtension {

    static void drawStringWithFont(PoseStack matrices, MultiBufferSource vertexConsumers, String text, float x, float y, float maxWidth, float maxHeight, float scale, int textColorCjk, int textColor, float fontSizeRatio, boolean shadow, int light, boolean forceMinecraftFont) {
        final Font fontRenderer = Minecraft.getInstance().font;

        while (text.contains("||")) {
            text = text.replace("||", "|");
        }
        final String[] stringSplit = text.split("\\|");

        int totalHeight = 0, totalWidth = 0;
        for (final String stringSplitPart : stringSplit) {
            final boolean isCJK = IGui.isCjk(stringSplitPart);
            totalHeight += Math.round((isCJK ? IGui.TEXT_HEIGHT * fontSizeRatio : IGui.LINE_HEIGHT));
            final int width = (int) Math.ceil(fontRenderer.width(stringSplitPart) * (isCJK ? fontSizeRatio : 1));
            if (width > totalWidth) {
                totalWidth = width;
            }
        }

        matrices.pushPose();

        final float totalWidthScaled;
        final float scaleX;
        if (maxWidth >= 0 && totalWidth > maxWidth * scale) {
            totalWidthScaled = maxWidth * scale;
            scaleX = totalWidth / maxWidth;
        } else {
            totalWidthScaled = totalWidth;
            scaleX = scale;
        }
        matrices.scale(1 / scaleX, 1 / scale, 1 / scale);

        float offset = y * scale;
        for (int i = 0; i < stringSplit.length; i++) {
            final String part = stringSplit[i];
            final boolean isCJK = IGui.isCjk(part);
            final float extraScale = isCJK ? fontSizeRatio : 1;

            if (isCJK) {
                matrices.pushPose();
                matrices.scale(extraScale, extraScale, 1);
            }

            final float textWidth = fontRenderer.width(part) * extraScale;
            final float xOffset = (totalWidth - textWidth) / 2 + x * scaleX;
            final int color = isCJK ? textColorCjk : textColor;

            fontRenderer.drawInBatch(part, xOffset / extraScale, offset / extraScale, color, shadow, matrices.last().pose(), vertexConsumers, Font.DisplayMode.NORMAL, 0, light);

            if (isCJK) {
                matrices.popPose();
            }

            offset += (isCJK ? IGui.TEXT_HEIGHT * fontSizeRatio : IGui.LINE_HEIGHT);
        }

        matrices.popPose();
    }

    /**
     * Measures the width and height of text rendered with custom font scaling.
     * Returns a pair of (width, height).
     */
    static float[] stringWidthWithFont(String text, float scale, float fontSizeRatio) {
        final Font fontRenderer = Minecraft.getInstance().font;

        while (text.contains("||")) {
            text = text.replace("||", "|");
        }
        final String[] stringSplit = text.split("\\|");

        int totalHeight = 0, totalWidth = 0;
        for (final String stringSplitPart : stringSplit) {
            final boolean isCJK = IGui.isCjk(stringSplitPart);
            totalHeight += Math.round((isCJK ? IGui.TEXT_HEIGHT * fontSizeRatio : IGui.LINE_HEIGHT));
            final int width = (int) Math.ceil(fontRenderer.width(stringSplitPart) * (isCJK ? fontSizeRatio : 1));
            if (width > totalWidth) {
                totalWidth = width;
            }
        }

        return new float[]{totalWidth / scale, totalHeight / scale};
    }
}