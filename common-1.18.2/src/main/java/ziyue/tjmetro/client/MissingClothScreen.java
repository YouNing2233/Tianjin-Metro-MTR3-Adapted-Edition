package ziyue.tjmetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Shown instead of the config screen when Cloth Config API is missing.
 * Ported from MTR4 edition (MissingClothConfigScreen).
 *
 * @since 1.1.2.1
 */

public class MissingClothScreen extends ScreenMapper
{
    private final Screen parent;
    private final Component message;

    public MissingClothScreen(Screen parent) {
        super(Text.translatable("config.tjmetro.cloth_config_not_found"));
        this.parent = parent;
        this.message = Text.translatable("config.tjmetro.cloth_config_required");
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, width, height, 0xD0101010);
        drawCenteredString(matrices, font, message, width / 2, height / 2 - 24, 0xFFFFFF);
        drawCenteredString(matrices, font, Text.translatable("config.tjmetro.cloth_config_hint"), width / 2, height / 2 + 12, 0xAAAAAA);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}