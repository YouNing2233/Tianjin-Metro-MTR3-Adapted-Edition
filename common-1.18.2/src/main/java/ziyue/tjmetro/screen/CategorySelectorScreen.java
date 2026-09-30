package ziyue.tjmetro.screen;

import io.netty.buffer.Unpooled;
import mtr.client.IDrawing;
import mtr.RegistryClient;
import mtr.data.IGui;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import mtr.screen.WidgetBetterCheckbox;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import ziyue.tjmetro.block.BlockPIDSTianjin;

import java.util.*;

import static ziyue.tjmetro.packet.IPacket.PACKET_UPDATE_PIDS_ADS_CONFIG;

public class CategorySelectorScreen extends ScreenMapper implements IGui {

    private final BlockPIDSTianjin.TileEntityBlockPIDSTianjin entity;
    private final ScreenMapper previousScreen;
    private final List<WidgetBetterCheckbox> categoryCheckboxes = new ArrayList<>();
    private final List<BlockPIDSTianjin.Category> allCategories = new ArrayList<>();

    public CategorySelectorScreen(BlockPIDSTianjin.TileEntityBlockPIDSTianjin entity, ScreenMapper previousScreen) {
        super(Text.literal(""));
        this.entity = entity;
        this.previousScreen = previousScreen;
        this.allCategories.addAll(BlockPIDSTianjin.CATEGORIES.values());
    }

    @Override
    protected void init() {
        super.init();

        final Set<Long> selectedCategories = new HashSet<>();
        for (int i = 0; i < entity.getCategories().size(); i++) {
            selectedCategories.add(entity.getCategories().getLong(i));
        }

        int y = SQUARE_SIZE;
        for (final BlockPIDSTianjin.Category category : allCategories) {
            final WidgetBetterCheckbox checkbox = new WidgetBetterCheckbox(0, 0, width - SQUARE_SIZE * 2, SQUARE_SIZE, category.name, button -> {});
            checkbox.setChecked(selectedCategories.contains(category.id));
            IDrawing.setPositionAndWidth(checkbox, SQUARE_SIZE, y, width - SQUARE_SIZE * 2);
            categoryCheckboxes.add(checkbox);
            addDrawableChild(checkbox);
            y += SQUARE_SIZE + 4;
        }

        final Button buttonDone = UtilitiesClient.newButton(Text.translatable("gui.done"), button -> onDone());
        IDrawing.setPositionAndWidth(buttonDone, (width - PANEL_WIDTH) / 2, height - SQUARE_SIZE * 2, PANEL_WIDTH);
        addDrawableChild(buttonDone);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        final String selectText = Text.translatable("gui.tjmetro.select_advertisements").getString();
        font.draw(matrices, selectText, width / 2 - font.width(selectText) / 2, SQUARE_SIZE / 2, ARGB_WHITE);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void onDone() {
        final BlockPIDSTianjin.LongAdderList newCategories = new BlockPIDSTianjin.LongAdderList();
        for (int i = 0; i < categoryCheckboxes.size(); i++) {
            if (categoryCheckboxes.get(i).selected()) {
                newCategories.add(allCategories.get(i).id);
            }
        }
        sendCategoriesToServer(newCategories);
        minecraft.setScreen(previousScreen);
    }

    private void sendCategoriesToServer(BlockPIDSTianjin.LongAdderList categories) {
        final net.minecraft.network.FriendlyByteBuf packet = new net.minecraft.network.FriendlyByteBuf(Unpooled.buffer());
        packet.writeBlockPos(entity.getBlockPos());
        packet.writeVarInt(categories.size());
        for (int i = 0; i < categories.size(); i++) {
            packet.writeLong(categories.getLong(i));
        }
        RegistryClient.sendToServer(PACKET_UPDATE_PIDS_ADS_CONFIG, packet);
    }
}