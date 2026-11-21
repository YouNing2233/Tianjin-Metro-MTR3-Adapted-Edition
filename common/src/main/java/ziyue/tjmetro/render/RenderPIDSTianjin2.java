package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.data.Route;
import mtr.data.ScheduleEntry;
import mtr.mappings.Text;
import mtr.render.MoreRenderLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ziyue.tjmetro.Reference;
import ziyue.tjmetro.block.BlockPIDSTianjin;
import ziyue.tjmetro.block.BlockPIDSTianjin2;
import ziyue.tjmetro.client.ClientCache;

/**
 * Renderer for the Line 2 layout PIDS (double sided).
 *
 * <p>Canvas is 251 x 115 units (1 unit = 1/128 block) like the original PIDS, texture is
 * {@code pids_tianjin_2.png} (1920 x 883). Layout, measured from the design mock-up:</p>
 *
 * <pre>
 *   +---------------------+------------------------------+
 *   | line name           |                              |
 *   | ------------------- |          advertisement       |
 *   | next train          |                              |
 *   |    2  minute        |                              |
 *   | going to ...        +------------------------------+
 *   |                     |                              |
 *   | clock               |          scrolling text      |
 *   | date                |                              |
 *   +---------------------+------------------------------+
 * </pre>
 *
 * @author __Lingxii__ (Architectury multi-platform port)
 * @see RenderPIDSTianjin
 */
public class RenderPIDSTianjin2<T extends BlockPIDSTianjin.TileEntityBlockPIDSTianjin> extends RenderPIDSTianjin<T> {

    private static final ResourceLocation TEXTURE_BOARD = new ResourceLocation(Reference.MOD_ID, "textures/block/pids_tianjin_2.png");
    private static final ResourceLocation TEXTURE_WHITE = new ResourceLocation("mtr", "textures/block/white.png");

    private static final float BOARD_WIDTH = 251F;
    private static final float BOARD_HEIGHT = 115F;

    private static final float COLUMN_CENTER_X = 35.8F;

    private static final float LINE_NAME_Y = 13.4F;
    private static final float LINE_NAME_SCALE = 1.32F;
    private static final float LINE_NAME_WIDTH = 62F;

    private static final float SEPARATOR_Y = 19.6F;
    private static final float SEPARATOR_X0 = 13.1F;
    private static final float SEPARATOR_X1 = 58.6F;
    private static final float SEPARATOR_THICKNESS = 0.7F;

    private static final float LABEL_Y = 27.0F;
    private static final float LABEL_SCALE = 1.36F;
    private static final float LABEL_WIDTH = 60F;

    private static final float ARRIVAL_Y = 45.5F;
    private static final float ARRIVAL_NUMBER_SCALE = 0.37F;
    private static final float ARRIVAL_UNIT_SCALE = 1.36F;
    private static final float ARRIVAL_WIDTH = 60F;

    private static final float DESTINATION_Y = 64.3F;
    private static final float DESTINATION_SCALE = 1.36F;
    private static final float DESTINATION_WIDTH = 60F;

    private static final float CLOCK_Y = 99.2F;
    private static final float CLOCK_SCALE = 0.72F;
    private static final float CLOCK_WIDTH = 58F;

    private static final float DATE_Y = 106.4F;
    private static final float DATE_SCALE = 1.72F;
    private static final float DATE_WIDTH = 58F;

    private static final float AD_X = 67.4F;
    private static final float AD_Y = 2.6F;
    private static final float AD_WIDTH = 180.7F;
    private static final float AD_HEIGHT = 95.4F;

    private static final float MARQUEE_X = 71.6F;
    private static final float MARQUEE_Y = 89.2F;

    public RenderPIDSTianjin2(BlockEntityRenderDispatcher dispatcher) {
        this(dispatcher, true, true);
    }

    protected RenderPIDSTianjin2(BlockEntityRenderDispatcher dispatcher, boolean doubleSided, boolean rotate90) {
        super(dispatcher, 2.5F, 30, rotate90, 1F);
        this.doubleSided = doubleSided;
    }

    private final boolean doubleSided;

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final net.minecraft.world.level.BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final net.minecraft.world.level.block.Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockPIDSTianjin pidsBlock)) return;
        if (!pidsBlock.canStoreData(world, pos)) return;

        final Direction facing = IBlock.getStatePropertySafe(world, pos, net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
        final float scale = 320 / maxHeight * textPadding;

        renderFace(matrices, vertexConsumers, facing, scale, entity, pos, 0, 0);
        if (doubleSided) {
            renderFace(matrices, vertexConsumers, facing.getOpposite(), scale, entity, pos, facing.getStepX(), facing.getStepZ());
        }
    }

    @Override
    protected void renderFace(PoseStack matrices, MultiBufferSource vertexConsumers, Direction facing, float scale, T entity, BlockPos pos, float offsetX, float offsetZ) {
        matrices.pushPose();
        matrices.translate(0.5 + offsetX, 0.85, 0.5 + offsetZ);
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees((rotate90 ? 90 : 0) - facing.toYRot()));
        matrices.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
        matrices.translate(-0.48, 0, -0.48);
        matrices.scale(1F / scale, 1F / scale, 1F / scale);
        matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(22.5F));
        matrices.translate(0, 0.05, 0);

        final RenderType boardRenderType = MoreRenderLayers.getLight(TEXTURE_BOARD, false);
        IDrawing.drawTexture(matrices, vertexConsumers.getBuffer(boardRenderType), 0, 0, BOARD_WIDTH, BOARD_HEIGHT, facing, MAX_LIGHT_GLOWING);

        matrices.translate(0, 0, -0.1);

        final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        final List<ScheduleEntry> scheduleList = getSchedules(entity, pos, new HashMap<>());
        final ScheduleEntry scheduleEntry = scheduleList.isEmpty() ? null : scheduleList.get(0);
        final Route route = scheduleEntry == null ? null : ClientData.DATA_CACHE.routeIdMap.get(scheduleEntry.routeId);

        renderClock(matrices, immediate, entity);
        renderDate(matrices, immediate, entity);

        if (route != null) {
            final String lineName = toChineseName(route.name);
            if (!lineName.isEmpty()) {
                renderText(matrices, immediate, lineName, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, COLUMN_CENTER_X, LINE_NAME_Y, LINE_NAME_WIDTH, LINE_NAME_SCALE, ARGB_WHITE);
            }

            drawSeparator(matrices, vertexConsumers, facing);

            final String[] destinationSplit = ClientData.DATA_CACHE.getFormattedRouteDestination(route, scheduleEntry.currentStationIndex, "").split("\\|");
            final String destination = destinationSplit.length == 0 ? "" : destinationSplit[0];
            final boolean useChinese = IGui.isCjk(destination);

            renderText(matrices, immediate, Text.translatable(useChinese ? "gui.tjmetro.this_train_cjk" : "gui.tjmetro.this_train").getString(),
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER, COLUMN_CENTER_X, LABEL_Y, LABEL_WIDTH, LABEL_SCALE, ARGB_WHITE);

            renderArrival(matrices, immediate, scheduleEntry, useChinese);

            renderText(matrices, immediate, Text.translatable(useChinese ? "gui.tjmetro.bound_for_pids_cjk" : "gui.tjmetro.bound_for_pids", destination).getString(),
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER, COLUMN_CENTER_X, DESTINATION_Y, DESTINATION_WIDTH, DESTINATION_SCALE, ARGB_WHITE);
        }

        renderAdvertisement(matrices, vertexConsumers, immediate, entity, facing);

        immediate.endBatch();
        matrices.popPose();
    }

    /**
     * Keeps only the Chinese part of a route name.
     *
     * <p>MTR route names are often entered as {@code "1号线|Line 1"} or {@code "1号线 / Line 1"};
     * the Line 2 board only has room for the Chinese name, so the first segment that contains
     * CJK characters wins. Names without any CJK text are returned unchanged.</p>
     */
    private static String toChineseName(String name) {
        if (name == null || name.isEmpty()) return "";
        for (final String part : name.split("[|/]")) {
            final String trimmed = part.trim();
            if (!trimmed.isEmpty() && IGui.isCjk(trimmed)) return trimmed;
        }
        return name.trim();
    }

    /**
     * Draws the thin rule under the line name. Note that the 5-float overload of
     * {@code IDrawing.drawTexture} takes (x, y, width, height), not (x1, y1, x2, y2).
     */
    private void drawSeparator(PoseStack matrices, MultiBufferSource vertexConsumers, Direction facing) {
        final RenderType whiteRenderType = MoreRenderLayers.getLight(TEXTURE_WHITE, false);
        IDrawing.drawTexture(matrices, vertexConsumers.getBuffer(whiteRenderType),
                SEPARATOR_X0, SEPARATOR_Y, SEPARATOR_X1 - SEPARATOR_X0, SEPARATOR_THICKNESS, facing, MAX_LIGHT_GLOWING);
    }

    private void renderArrival(PoseStack matrices, MultiBufferSource.BufferSource immediate, ScheduleEntry scheduleEntry, boolean useChinese) {
        final long arrivalSeconds = (scheduleEntry.arrivalMillis - System.currentTimeMillis()) / 1000;

        if (arrivalSeconds <= 15) {
            final String textKey = (arrivalSeconds < 0 ? "gui.tjmetro.arrived" : "gui.tjmetro.arriving") + (useChinese ? "_cjk" : "");
            renderText(matrices, immediate, Text.translatable(textKey).getString(),
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER, COLUMN_CENTER_X, ARRIVAL_Y, ARRIVAL_WIDTH, 0.9F, 0xFFEFEF00);
            return;
        }

        final boolean isMinute = arrivalSeconds > 60;
        final String number = String.valueOf(isMinute ? arrivalSeconds / 60 : arrivalSeconds);
        final String unit = Text.translatable((isMinute ? "gui.tjmetro.minute" : "gui.tjmetro.second") + (useChinese ? "_cjk" : "")).getString();

        final float numberWidth = getStringWidth(Minecraft.getInstance().font, number, ARRIVAL_NUMBER_SCALE);
        final float unitWidth = getStringWidth(Minecraft.getInstance().font, unit, ARRIVAL_UNIT_SCALE);
        final float halfTotal = (numberWidth + unitWidth) / 2;

        renderText(matrices, immediate, number, HorizontalAlignment.LEFT, VerticalAlignment.CENTER,
                COLUMN_CENTER_X - halfTotal, ARRIVAL_Y, ARRIVAL_WIDTH, ARRIVAL_NUMBER_SCALE, 0xFFEFEF00);
        renderText(matrices, immediate, unit, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER,
                COLUMN_CENTER_X + halfTotal, ARRIVAL_Y, ARRIVAL_WIDTH, ARRIVAL_UNIT_SCALE, ARGB_WHITE);
    }

    private void renderClock(PoseStack matrices, MultiBufferSource.BufferSource immediate, T entity) {
        final Calendar calendar = Calendar.getInstance();
        final String text;
        if (entity.isUseRealTime()) {
            text = String.format(Locale.ROOT, "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } else {
            final net.minecraft.world.level.BlockGetter world = entity.getLevel();
            if (world == null) return;
            final long ticks = ((net.minecraft.world.level.Level) world).getDayTime() % 24000;
            final long totalSeconds = ticks * 36 / 10;
            text = String.format(Locale.ROOT, "%02d:%02d", (totalSeconds / 3600 + 6) % 24, totalSeconds / 60 % 60);
        }
        renderText(matrices, immediate, text, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, COLUMN_CENTER_X, CLOCK_Y, CLOCK_WIDTH, CLOCK_SCALE, ARGB_WHITE);
    }

    private void renderDate(PoseStack matrices, MultiBufferSource.BufferSource immediate, T entity) {
        if (!entity.isUseRealTime()) return;
        final Date now = new Date();
        final String date = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault()).format(now);
        final String weekday = new SimpleDateFormat("EEEE", Locale.getDefault()).format(now);
        renderText(matrices, immediate, date + "  " + weekday, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, COLUMN_CENTER_X, DATE_Y, DATE_WIDTH, DATE_SCALE, ARGB_WHITE);
    }

    private void renderAdvertisement(PoseStack matrices, MultiBufferSource vertexConsumers, MultiBufferSource.BufferSource immediate, T entity, Direction facing) {
        if (entity.getCategories().isEmpty()) return;

        if (entity.getCategoryIndex() >= entity.getCategories().size()
                || !BlockPIDSTianjin.CATEGORIES.containsKey(entity.getCategories().getLong(entity.getCategoryIndex()))) {
            entity.setCategoryIndex((entity.getCategoryIndex() + 1) % entity.getCategories().size());
            entity.setAdvertisementIndex(0);
            if (!BlockPIDSTianjin.CATEGORIES.containsKey(entity.getCategories().getLong(entity.getCategoryIndex()))) {
                entity.getCategories().clear();
                return;
            }
        }

        final BlockPIDSTianjin.Category category = BlockPIDSTianjin.CATEGORIES.get(entity.getCategories().getLong(entity.getCategoryIndex()));
        if (category == null || category.isEmpty()) return;
        if (entity.getAdvertisementIndex() >= category.size()) {
            entity.setAdvertisementIndex(0);
        }
        final BlockPIDSTianjin.Advertisement advertisement = category.get(entity.getAdvertisementIndex());
        if (advertisement == null) return;

        matrices.pushPose();
        matrices.translate(AD_X, AD_Y, 0);
        final RenderType adRenderType = MoreRenderLayers.getLight(advertisement.image(), false);
        IDrawing.drawTexture(matrices, vertexConsumers.getBuffer(adRenderType), 0, 0, AD_WIDTH, AD_HEIGHT, facing, MAX_LIGHT_GLOWING);
        matrices.popPose();

        final String message = advertisement.text() == null ? "" : advertisement.text().getString();

        if (entity.advertisement != advertisement) {
            entity.advertisement = advertisement;
            entity.marqueeText.changeImage(() -> ClientCache.DATA_CACHE.getPlainText(
                    "   " + message + "   ", 0, ARGB_WHITE));
        }

        matrices.pushPose();
        matrices.translate(MARQUEE_X, MARQUEE_Y, 0);
        entity.marqueeText.setVertexConsumer(vertexConsumers);
        final boolean scrolledComplete = entity.marqueeText.scrollText(matrices, facing);
        matrices.popPose();

        if (scrolledComplete && !message.isEmpty()) {
            nextSlide(entity);
        }
    }

    /**
     * Kept for subclasses that want the schedule list without rebuilding the platform map.
     */
    protected List<ScheduleEntry> getSchedulesSafe(T entity, BlockPos pos) {
        return getSchedules(entity, pos, new HashMap<>());
    }

    @SuppressWarnings("unused")
    private static List<String> emptyList() {
        return new ArrayList<>();
    }
}
