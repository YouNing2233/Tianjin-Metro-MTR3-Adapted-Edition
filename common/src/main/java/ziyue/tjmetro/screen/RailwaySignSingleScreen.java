package ziyue.tjmetro.screen;

import mtr.block.BlockRailwaySign;
import mtr.client.CustomResources;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.ScreenMapper;
import mtr.mappings.Text;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderRailwaySign;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import ziyue.tjmetro.TianjinMetro;
import ziyue.tjmetro.block.BlockRailwaySignSingle;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Sign selection screen for {@link BlockRailwaySignSingle}, ported from the MTR4 Tianjin Metro
 * addon (<i>RailwaySignSingleScreen</i>, 1.1.3).
 * <p>
 * Shows a paged grid of every available sign and writes the picked one into the requested slot
 * ({@code index}) of the block entity. Layout follows {@link RailwaySignScreen} so both screens
 * behave the same way, but this one only needs the grid: there is nothing else to configure.
 *
 * @author ZiYueCommentary
 * @see BlockRailwaySignSingle
 * @since 1.1.3
 */
public class RailwaySignSingleScreen extends ScreenMapper implements IGui
{
    protected final BlockPos signPos;
    protected final int index;
    protected String signId;
    protected final List<String> allSignIds = new ArrayList<>();

    protected int page;
    protected int totalPages;
    protected int columns;
    protected int rows;

    protected final Button[] buttonsSelection;
    protected final Button buttonClear;
    protected final Button buttonPrevPage;
    protected final Button buttonNextPage;

    protected static final int SIGN_SIZE = 32;
    protected static final int SIGN_BUTTON_SIZE = 16;
    protected static final int BUTTON_Y_START = SIGN_SIZE + SQUARE_SIZE + SQUARE_SIZE / 2;

    public RailwaySignSingleScreen(BlockPos signPos, int index) {
        super(Text.literal(""));
        this.signPos = signPos;
        this.index = index;

        for (final BlockRailwaySign.SignType signType : BlockRailwaySign.SignType.values()) {
            allSignIds.add(signType.toString());
        }
        final List<String> sortedKeys = new ArrayList<>(CustomResources.CUSTOM_SIGNS.keySet());
        Collections.sort(sortedKeys);
        allSignIds.addAll(sortedKeys);

        // The inherited level field is still null while the constructor runs, so read the client
        // instance directly (same as the MTR4 original).
        final net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
        if (client.level != null) {
            final BlockEntity entity = client.level.getBlockEntity(signPos);
            if (entity instanceof BlockRailwaySignSingle.BlockEntity entity1) {
                signId = entity1.getSignId(index);
            }
        }

        buttonsSelection = new Button[allSignIds.size()];
        for (int i = 0; i < allSignIds.size(); i++) {
            final int slot = i;
            buttonsSelection[i] = UtilitiesClient.newButton(SIGN_BUTTON_SIZE, Text.literal(""), button -> setNewSignId(allSignIds.get(slot)));
        }

        buttonClear = UtilitiesClient.newButton(Text.translatable("gui.mtr.reset_sign"), button -> setNewSignId(null));
        buttonPrevPage = UtilitiesClient.newButton(Text.literal("<"), button -> setPage(page - 1));
        buttonNextPage = UtilitiesClient.newButton(Text.literal(">"), button -> setPage(page + 1));
    }

    @Override
    protected void init() {
        super.init();

        columns = Math.max((width - SIGN_BUTTON_SIZE * 3) / (SIGN_BUTTON_SIZE * 8) * 2, 1);
        rows = Math.max((height - SIGN_SIZE - SQUARE_SIZE * 4) / SIGN_BUTTON_SIZE, 1);

        final int xOffsetSmall = (width - SIGN_BUTTON_SIZE * (columns * 4 + 3)) / 2 + SIGN_BUTTON_SIZE;
        final int xOffsetBig = xOffsetSmall + SIGN_BUTTON_SIZE * (columns + 1);

        totalPages = loopSigns((index, x, y, isBig) -> IDrawing.setPositionAndWidth(buttonsSelection[index], (isBig ? xOffsetBig : xOffsetSmall) + x, BUTTON_Y_START + y, isBig ? SIGN_BUTTON_SIZE * 3 : SIGN_BUTTON_SIZE), false);
        for (final Button button : buttonsSelection) {
            addDrawableChild(button);
        }

        final int buttonClearX = (width - PANEL_WIDTH - SQUARE_SIZE * 4) / 2;
        final int buttonY = height - SQUARE_SIZE * 2;

        IDrawing.setPositionAndWidth(buttonClear, buttonClearX, buttonY, PANEL_WIDTH);
        addDrawableChild(buttonClear);

        IDrawing.setPositionAndWidth(buttonPrevPage, buttonClearX + PANEL_WIDTH, buttonY, SQUARE_SIZE);
        addDrawableChild(buttonPrevPage);
        IDrawing.setPositionAndWidth(buttonNextPage, buttonClearX + PANEL_WIDTH + SQUARE_SIZE * 3, buttonY, SQUARE_SIZE);
        addDrawableChild(buttonNextPage);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        try {
            renderBackground(guiGraphics);
            super.render(guiGraphics, mouseX, mouseY, delta);
            if (minecraft == null) return;

            if (signId != null) {
                RenderRailwaySign.drawSign(guiGraphics.pose(), null, null, font, signPos, signId, (width - SIGN_SIZE) / 2F, 0, SIGN_SIZE, 6, 6, new HashSet<>(), Direction.UP, 0, (textureId, x, y, size, flipTexture) -> {
                    UtilitiesClient.beginDrawingTexture(textureId);
                    guiGraphics.blit(textureId, (int) x, (int) y, 0, 0, (int) size, (int) size, (int) (flipTexture ? -size : size), (int) size);
                });
            }

            final int xOffsetSmall = (width - SIGN_BUTTON_SIZE * (columns * 4 + 3)) / 2 + SIGN_BUTTON_SIZE;
            final int xOffsetBig = xOffsetSmall + SIGN_BUTTON_SIZE * (columns + 1);

            loopSigns((index, x, y, isBig) -> {
                final String loopSignId = allSignIds.get(index);
                final CustomResources.CustomSign sign = RenderRailwaySign.getSign(loopSignId);
                if (sign != null) {
                    final boolean moveRight = sign.hasCustomText() && sign.flipCustomText;
                    UtilitiesClient.beginDrawingTexture(sign.textureId);
                    RenderRailwaySign.drawSign(guiGraphics.pose(), null, null, font, signPos, loopSignId, (isBig ? xOffsetBig : xOffsetSmall) + x + (moveRight ? SIGN_BUTTON_SIZE * 2 : 0), BUTTON_Y_START + y, SIGN_BUTTON_SIZE, 6, 6, new HashSet<>(), Direction.UP, 0, (textureId, x1, y1, size, flipTexture) -> guiGraphics.blit(textureId, (int) x1, (int) y1, 0, 0, (int) size, (int) size, (int) (flipTexture ? -size : size), (int) size));
                }
            }, false);

            guiGraphics.drawCenteredString(font, String.format("%s/%s", page + 1, totalPages), (width - PANEL_WIDTH - SQUARE_SIZE * 4) / 2 + PANEL_WIDTH + SQUARE_SIZE * 2, height - SQUARE_SIZE * 2 + TEXT_PADDING, ARGB_WHITE);
        } catch (Exception e) {
            TianjinMetro.LOGGER.error(e);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        setPage(page + (int) Math.signum(-amount));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    protected void setPage(int newPage) {
        page = Math.max(0, Math.min(newPage, totalPages - 1));
        final int xOffsetSmall = (width - SIGN_BUTTON_SIZE * (columns * 4 + 3)) / 2 + SIGN_BUTTON_SIZE;
        final int xOffsetBig = xOffsetSmall + SIGN_BUTTON_SIZE * (columns + 1);
        // loopSigns() also refreshes buttonsSelection[i].visible for the new page.
        loopSigns((index, x, y, isBig) -> IDrawing.setPositionAndWidth(buttonsSelection[index], (isBig ? xOffsetBig : xOffsetSmall) + x, BUTTON_Y_START + y, isBig ? SIGN_BUTTON_SIZE * 3 : SIGN_BUTTON_SIZE), false);
    }

    protected void setNewSignId(String newSignId) {
        signId = newSignId;
        PacketGuiServer.sendRailwaySignSingleConfigC2S(signPos, index, newSignId);
    }

    @Override
    public void onClose() {
        PacketGuiServer.sendRailwaySignSingleConfigC2S(signPos, index, signId);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Walks every sign id, placing small signs on the left half of the grid and signs with custom
     * text (three buttons wide) on the right half, exactly like {@link RailwaySignScreen}. Returns
     * the number of pages.
     */
    protected int loopSigns(LoopSignsCallback loopSignsCallback, boolean ignorePage) {
        final int pageCount = rows * columns;
        int indexSmall = 0;
        int indexBig = 0;
        int columnSmall = 0;
        int columnBig = 0;
        int rowSmall = 0;
        int rowBig = 0;
        int totalPagesSmallCount = 1;
        int totalPagesBigCount = 1;

        for (int i = 0; i < allSignIds.size(); i++) {
            final CustomResources.CustomSign sign = RenderRailwaySign.getSign(allSignIds.get(i));
            final boolean isBig = sign != null && sign.hasCustomText();

            final boolean onPage = (isBig ? indexBig : indexSmall) / pageCount == page;
            buttonsSelection[i].visible = onPage;
            if (ignorePage || onPage) {
                loopSignsCallback.loopSignsCallback(i, (isBig ? columnBig * 3 : columnSmall) * SIGN_BUTTON_SIZE, (isBig ? rowBig : rowSmall) * SIGN_BUTTON_SIZE, isBig);
            }

            if (isBig) {
                columnBig++;
                if (totalPagesBigCount < 0) totalPagesBigCount = -totalPagesBigCount + 1;
                if (columnBig >= columns) {
                    columnBig = 0;
                    rowBig++;
                    if (rowBig >= rows) {
                        rowBig = 0;
                        totalPagesBigCount = -totalPagesBigCount;
                    }
                }
                indexBig++;
            } else {
                columnSmall++;
                if (totalPagesSmallCount < 0) totalPagesSmallCount = -totalPagesSmallCount + 1;
                if (columnSmall >= columns) {
                    columnSmall = 0;
                    rowSmall++;
                    if (rowSmall >= rows) {
                        rowSmall = 0;
                        totalPagesSmallCount = -totalPagesSmallCount;
                    }
                }
                indexSmall++;
            }
        }

        return Math.max(Math.max((indexSmall - 1) / pageCount + 1, (indexBig - 1) / pageCount + 1), 1);
    }

    @FunctionalInterface
    protected interface LoopSignsCallback
    {
        void loopSignsCallback(int index, int x, int y, boolean isBig);
    }
}
