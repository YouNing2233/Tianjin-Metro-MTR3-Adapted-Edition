package ziyue.tjmetro.client;

import mtr.data.IGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import ziyue.tjmetro.Reference;

public interface IDrawingExtension {

    static float stringWidthWithFont(String text, float scale, float fontSizeRatio, boolean forceMinecraftFont) {
        while (text.contains("||")) {
            text = text.replace("||", "|");
        }
        final String[] stringSplit = text.split("\\|");

        final Font textRenderer = Minecraft.getInstance().font;
        int totalWidth = 0;
        for (final String stringSplitPart : stringSplit) {
            final boolean isCJK = IGui.isCjk(stringSplitPart);

            net.minecraft.network.chat.Component component;
            if (!forceMinecraftFont) {
                component = net.minecraft.network.chat.Component.literal(stringSplitPart).setStyle(net.minecraft.network.chat.Style.EMPTY.withFont(new ResourceLocation(Reference.MOD_ID, "tjmetro")));
            } else {
                component = net.minecraft.network.chat.Component.literal(stringSplitPart);
            }

            final int width = (int) Math.ceil(textRenderer.width(component) * (isCJK ? fontSizeRatio : 1));
            if (width > totalWidth) {
                totalWidth = width;
            }
        }

        return totalWidth / scale;
    }
}