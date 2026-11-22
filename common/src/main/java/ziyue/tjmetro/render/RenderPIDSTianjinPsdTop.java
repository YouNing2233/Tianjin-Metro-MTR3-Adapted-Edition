package ziyue.tjmetro.render;

import ziyue.tjmetro.client.PidsConsistPainter;
import ziyue.tjmetro.client.PidsConsistTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.data.Depot;
import mtr.data.RailwayData;
import mtr.data.Route;
import mtr.data.ScheduleEntry;
import mtr.data.Station;
import mtr.render.MoreRenderLayers;
import net.minecraft.client.Minecraft;
import ziyue.tjmetro.client.IDrawingExtends;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import ziyue.tjmetro.Reference;
import ziyue.tjmetro.block.BlockPIDSTianjin;
import ziyue.tjmetro.block.BlockPIDSTianjinPsdTop;
import ziyue.tjmetro.client.ClientCache;
import ziyue.tjmetro.client.RouteMapGenerator;
import ziyue.tjmetro.client.ScrollingText;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Renders the flat screen that hangs in front of a platform screen door top plate.
 * The canvas is 2.9 x 0.9 blocks (371 x 115 px at 128 px per block) and includes the advertisement area
 * on the right, which keeps exactly the same size as the free standing PIDS advertisement.
 */
public class RenderPIDSTianjinPsdTop<T extends BlockPIDSTianjinPsdTop.TileEntityBlockPIDSTianjinPsdTop> extends RenderPIDSTianjin<T>
{
    public static final float CANVAS_W = 371F;
    public static final float CANVAS_H = 115F;

    /** Same size as the advertisement of {@link RenderPIDSTianjin}. */
    public static final float AD_X = 206F;
    public static final float AD_Y = 13.35F;
    public static final float AD_W = 161F;
    public static final float AD_H = 88.3F;

    protected static final float CONTENT_X0 = 4F;
    protected static final float CONTENT_X1 = 202F;
    private static final float MARQUEE_Y0 = 92F;
    private static final float MARQUEE_Y1 = 112F;

    /** Pixels per block of the canvas: 143 px gives a 2.59 x 0.80 block screen. */
    private static final float FRAME_SCALE = 143F;

    /** Distance from the anchor block centre towards the plate, in blocks. */
    private static final float SCREEN_Z = 1.12F;

    /** 滚动条：直接滚广告文字（与 PIDS 同一个 getPlainText 缓存，不额外生成纹理）。 */
    private final ScrollingText marquee = new ScrollingText(CONTENT_X1 - CONTENT_X0 - 6F, 26F, 4, true);
    private String marqueeMessage = "";

    private MultiBufferSource currentConsumers;
    private Direction currentFacing;

    @Override
    protected void renderText(PoseStack matrices, MultiBufferSource.BufferSource immediate, String text, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, float x, float y, float availableWidth, float scale, int color) {
        // 用 MTR/原版字形直接绘制：没有底色贴图，也就不会出现字底黑块与黑边。
        super.renderText(matrices, immediate, text, horizontalAlignment, verticalAlignment, x, y, availableWidth, scale, color);
    }

    private static final int COLOR_BG = 0xFF0537D1;
    private static final int COLOR_CARD = 0xFF232848;
    private static final int COLOR_DARK = 0xFF1A2025;
    private static final int COLOR_RED = 0xFFC81E28;
    private static final int COLOR_CAR = 0xFFECEFF2;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_YELLOW = 0xFFFAD628;
    private static final int COLOR_GREEN = 0xFF9FE8B4;
    // 车厢编组条素材来自 mtr-external-lights（longqiuting），按原样等比绘制。
    private static final ResourceLocation CONSIST_BAND = new ResourceLocation(Reference.MOD_ID, "textures/gui/pids/consist_band.png");
    private static final ResourceLocation CONSIST_CARD = new ResourceLocation(Reference.MOD_ID, "textures/gui/pids/consist_card.png");
    /** band 原图 333x33，左右端盖各 16 px 不拉伸。 */
    private static final float BAND_TEX_W = 333F;
    private static final float BAND_CAP_U = 16F / BAND_TEX_W;
    private static final float CARD_ASPECT = 41F / 20F;

    private static final int COLOR_MAP_BG = 0xFFF2F4F7;
    private static final int COLOR_BAND = 0xFF0000D4;

    private static final long PAGE_MILLIS = 15000L;
    private static final long LOCK_MILLIS = 20000L;
    private static final long DOOR_MILLIS = 15000L;

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation("mtr", "textures/block/white.png");
    private static final ResourceLocation LOGO_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/pids_logo.png");

    private static final int PAGE_ARRIVALS = 0;
    private static final int PAGE_LINE = 1;
    private static final int PAGE_ROUTE_MAP = 2;
    private static final int PAGE_TRAIN = 3;

    public RenderPIDSTianjinPsdTop(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, 2.5F, 30, false, 1F);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) {
            return;
        }

        final BlockPos pos = entity.getBlockPos();
        if (!(world.getBlockState(pos).getBlock() instanceof BlockPIDSTianjinPsdTop pidsBlock) || !pidsBlock.canStoreData(world, pos)) {
            return;
        }

        final Direction facing = IBlock.getStatePropertySafe(world, pos, HorizontalDirectionalBlock.FACING);
        final float scale = 320 / maxHeight * textPadding;
        renderFace(matrices, vertexConsumers, facing, scale, entity, pos, 0, 0);
    }

    @Override
    protected void applyDisplayTransform(PoseStack matrices, Direction facing, float scale, float offsetX, float offsetZ) {
        // Flat screen: no lean back, canvas top 0.975 blocks above the block floor, 0.9 blocks tall.
        matrices.translate(0.5 + offsetX, 0.975 - 1F / 12F, 0.5 + offsetZ);
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-facing.toYRot()));
        matrices.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
        matrices.translate(-CANVAS_W / 2F / FRAME_SCALE, 0, SCREEN_Z);
        matrices.scale(1F / FRAME_SCALE, 1F / FRAME_SCALE, 1F / FRAME_SCALE);
    }

    @Override
    protected void renderFace(PoseStack matrices, MultiBufferSource vertexConsumers, Direction facing, float scale, T entity, BlockPos pos, float offsetX, float offsetZ) {
        currentConsumers = vertexConsumers;
        currentFacing = facing;
        matrices.pushPose();
        applyDisplayTransform(matrices, facing, scale, offsetX, offsetZ);

        final VertexConsumer quadConsumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(WHITE_TEXTURE, false));
        final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        fill(matrices, quadConsumer, facing, 0, 0, CANVAS_W, CANVAS_H, COLOR_BG);
        matrices.translate(0, 0, -0.35F);


        final Map<Long, String> platformIdToName = new HashMap<>();
        final List<ScheduleEntry> scheduleList = getSchedules(entity, pos, platformIdToName);
        final Set<Long> platformIds = getEffectivePlatformIds(entity, pos);
        final Set<Long> routeIds = getRouteIds(platformIds);
        final int page = getPage(scheduleList, routeIds);

        if (page == PAGE_TRAIN) {
            renderTrainPage(matrices, vertexConsumers, quadConsumer, immediate, facing, scheduleList, routeIds, entity);
        } else if (page == PAGE_LINE) {
            renderLinePage(matrices, vertexConsumers, quadConsumer, immediate, facing, platformIds, scheduleList, entity);
        } else if (page == PAGE_ROUTE_MAP) {
            renderRouteMapPage(matrices, vertexConsumers, quadConsumer, immediate, facing, platformIds, scheduleList, entity);

        } else {
            renderArrivalsPage(matrices, vertexConsumers, quadConsumer, immediate, facing, scheduleList, entity.getDisplayPage(), entity);
        }

        renderAd(matrices, vertexConsumers, entity, facing);
        immediate.endBatch();
        matrices.popPose();
    }

    // region pages

    protected void renderArrivalsPage(PoseStack matrices, MultiBufferSource vertexConsumers, VertexConsumer quadConsumer, MultiBufferSource.BufferSource immediate, Direction facing, List<ScheduleEntry> scheduleList, int displayPage, T entity) {
        drawLogo(matrices, vertexConsumers, facing, CONTENT_X0 + 2F, 7F);

        float y = 36F;
        for (int i = 0; i < 3; i++) {
            final int index = i + displayPage * 3;
            if (index >= scheduleList.size()) {
                break;
            }

            final ScheduleEntry entry = scheduleList.get(index);
            final long millisLeft = entry.arrivalMillis - System.currentTimeMillis();
            final boolean showSeconds = millisLeft < 60000L;
            final long amount = showSeconds ? Math.max(0, millisLeft / 1000L) : Math.max(0, millisLeft / 60000L);
            renderText(matrices, immediate, getPidsDestination(entry), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, CONTENT_X0 + 4F, y, 150F, 0.85F, COLOR_WHITE);
            renderText(matrices, immediate, String.valueOf(amount), HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, CONTENT_X0 + 166F, y, 34F, 0.40F, COLOR_YELLOW);
            renderText(matrices, immediate, showSeconds ? "秒/s" : "分钟/min", HorizontalAlignment.LEFT, VerticalAlignment.CENTER, CONTENT_X0 + 170F, y + 1.2F, 40F, 1.15F, COLOR_WHITE);
            y += 26F;
        }

        drawMarquee(matrices, quadConsumer, vertexConsumers, facing, entity);
    }

    protected void renderLinePage(PoseStack matrices, MultiBufferSource vertexConsumers, VertexConsumer quadConsumer, MultiBufferSource.BufferSource immediate, Direction facing, Set<Long> platformIds, List<ScheduleEntry> scheduleList, T entity) {
        drawLogo(matrices, vertexConsumers, facing, CONTENT_X0 + 2F, 6F);
        renderText(matrices, immediate, getLineName(scheduleList), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, CONTENT_X0 + 46F, 11.5F, 150F, 0.85F, COLOR_WHITE);

        final long platformId = platformIds.isEmpty() ? 0 : platformIds.iterator().next();
        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, entity.getBlockPos());
        final float centerX = (CONTENT_X0 + CONTENT_X1) / 2F;
        final float width = CONTENT_X1 - CONTENT_X0 - 8F;

        renderText(matrices, immediate, station == null ? "" : toChineseName(station.name), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, 45F, width, 0.34F, COLOR_WHITE);

        final ScheduleEntry next = scheduleList.isEmpty() ? null : scheduleList.get(0);
        renderText(matrices, immediate, next == null ? "" : "开往 " + getPidsDestination(next), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, 74F, width, 1.05F, COLOR_YELLOW);

        drawMarquee(matrices, quadConsumer, vertexConsumers, facing, entity);
    }

    /** 线路图页：复用 MTR / 本模组的津静线线路图动态纹理（一次生成，之后走缓存）。 */
    protected void renderRouteMapPage(PoseStack matrices, MultiBufferSource vertexConsumers, VertexConsumer quadConsumer, MultiBufferSource.BufferSource immediate, Direction facing, Set<Long> platformIds, List<ScheduleEntry> scheduleList, T entity) {
        final long platformId = platformIds.isEmpty() ? 0 : platformIds.iterator().next();
        final float centerX = (CONTENT_X0 + CONTENT_X1) / 2F;
        final float width = CONTENT_X1 - CONTENT_X0 - 6F;

        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, entity.getBlockPos());
        final String stationName = station == null ? "" : toChineseName(station.name);
        final ScheduleEntry nextEntry = scheduleList.isEmpty() ? null : scheduleList.get(0);
        final String destination = nextEntry == null ? "" : getPidsDestination(nextEntry);


        final float mapTop = 26F;
        final float mapHeight = 54F;
        // 线路图区域垫一块白底，图上的深色站名才看得清。
        fill(matrices, quadConsumer, facing, CONTENT_X0 + 3F, mapTop, width, mapHeight, COLOR_MAP_BG);
        if (platformId == 0) {
            renderText(matrices, immediate, "未绑定站台", HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, mapTop + mapHeight / 2F, width, 0.6F, COLOR_WHITE);
        } else {
            // transparentWhite = true：底图透明，直接叠在深蓝背景上。
            ClientCache.DynamicResource resource;
            RouteMapGenerator.PLAIN_STATION_NAME = true;
            try {
                resource = ClientCache.DATA_CACHE.getRouteMapJinjing(platformId, 0, width / mapHeight, true);
            } finally {
                RouteMapGenerator.PLAIN_STATION_NAME = false;
            }
            if (resource == null || resource.width <= 0 || resource.height <= 0) {
                renderText(matrices, immediate, "线路图生成中", HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, mapTop + mapHeight / 2F, width, 0.6F, COLOR_WHITE);
            } else {
                float drawWidth = width;
                float drawHeight = drawWidth * resource.height / resource.width;
                if (drawHeight > mapHeight) {
                    drawHeight = mapHeight;
                    drawWidth = drawHeight * resource.width / resource.height;
                }
                final float x = CONTENT_X0 + 3F + (width - drawWidth) / 2F;
                final float y = mapTop + (mapHeight - drawHeight) / 2F;
                final VertexConsumer consumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(resource.resourceLocation, true));
                matrices.pushPose();
                matrices.translate(0, 0, -0.25F);
                IDrawing.drawTexture(matrices, consumer, x, y, 0F, x + drawWidth, y + drawHeight, 0F, 0F, 0F, 1F, 1F, facing, COLOR_WHITE, MAX_LIGHT_GLOWING);
                matrices.popPose();
            }
        }

        drawMarquee(matrices, quadConsumer, vertexConsumers, facing, entity);
    }

    /**
     * 车厢编组条：直接贴 mtr-external-lights 那套 4× 叠加层贴图（底带 + 卡片 + 雪花），
     * 「N车」文字按 {@link PidsConsistPainter#layout(int)} 给出的位置另画。整块按 333:33 原比例，不拉伸。
     */
    protected void renderConsist(PoseStack matrices, MultiBufferSource vertexConsumers, MultiBufferSource.BufferSource immediate, Direction facing, int cars, float x, float y, float width) {
        if (cars <= 0) {
            return;
        }
        final ResourceLocation texture = PidsConsistTextures.texture(cars);
        if (texture == null) {
            return;
        }

        final float w = Math.min(width, 190F);
        final float h = w * PidsConsistPainter.STRIP_H / PidsConsistPainter.STRIP_W;
        final float x0 = x + (width - w) / 2F;

        final VertexConsumer consumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(texture, true));
        drawTexturedQuad(matrices, consumer, facing, x0, y, x0 + w, y + h, 0F, 1F);

    }

    private void drawTexturedQuad(PoseStack matrices, VertexConsumer consumer, Direction facing, float x0, float y0, float x1, float y1, float u0, float u1) {
        IDrawing.drawTexture(matrices, consumer, x0, y0, 0F, x1, y1, 0F, u0, 0F, u1, 1F, facing, 0xFFFFFFFF, MAX_LIGHT_GLOWING);
    }

    protected void renderTrainPage(PoseStack matrices, MultiBufferSource vertexConsumers, VertexConsumer quadConsumer, MultiBufferSource.BufferSource immediate, Direction facing, List<ScheduleEntry> scheduleList, Set<Long> routeIds, T entity) {
        final ScheduleEntry entry = getLockedEntry(scheduleList, routeIds);
        if (entry == null) {
            drawMarquee(matrices, quadConsumer, vertexConsumers, facing, entity);
            return;
        }

        drawLogo(matrices, vertexConsumers, facing, CONTENT_X0 + 2F, 7F);

        final float centerX = (CONTENT_X0 + CONTENT_X1) / 2F - 4F;

        final long millisLeft = entry == null ? -1 : entry.arrivalMillis - System.currentTimeMillis();
        final boolean dwelling = entry != null && millisLeft <= 0;

        final String doorHint = dwelling ? "车门开启中 · 请先上后下，谨防夹伤" : "车门即将开启 · 请在黄线外候车";

        final int cars = entry.trainCars;
        renderConsist(matrices, vertexConsumers, immediate, facing, cars, CONTENT_X0 + 6F, 34F, CONTENT_X1 - CONTENT_X0 - 12F);
        if (cars > 0) {
            renderText(matrices, immediate, cars + " 编组", HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, 72F, 190F, 1.25F, COLOR_WHITE);
        }
        renderText(matrices, immediate, doorHint, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, 86F, 190F, 0.85F, COLOR_GREEN);

        drawMarquee(matrices, quadConsumer, vertexConsumers, facing, entity);
    }

    // endregion

    // region helpers

    private void fill(PoseStack matrices, VertexConsumer consumer, Direction facing, float x, float y, float width, float height, int color) {
        IDrawing.drawTexture(matrices, consumer, x, y, 0, x + width, y + height, 0, 0, 0, 1, 1, facing, color, MAX_LIGHT_GLOWING);
    }

    private void drawLogo(PoseStack matrices, MultiBufferSource vertexConsumers, Direction facing, float x, float y) {
        final VertexConsumer logoConsumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(LOGO_TEXTURE, true));
        IDrawing.drawTexture(matrices, logoConsumer, x, y, 0, x + 56F, y + 18.4F, 0, 0, 0, 1, 1, facing, COLOR_WHITE, MAX_LIGHT_GLOWING);
    }

    private void drawBadge(PoseStack matrices, VertexConsumer consumer, MultiBufferSource.BufferSource immediate, Direction facing, float x, float y, String text) {
        fill(matrices, consumer, facing, x, y, 24F, 12F, COLOR_RED);
        renderText(matrices, immediate, text, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, x + 12F, y + 6F, 24F, 0.95F, COLOR_WHITE);
    }

    private void drawMarquee(PoseStack matrices, VertexConsumer consumer, MultiBufferSource vertexConsumers, Direction facing, T entity) {
        // 滚动条不画底色（透明），文字直接叠在屏幕背景上。

        final String message = getAdvertisementMessage(entity);
        if (!message.equals(marqueeMessage)) {
            marqueeMessage = message;
            marquee.changeImage(() -> ClientCache.DATA_CACHE.getPlainText("   " + message + "   ", 0, IGui.ARGB_WHITE));
        }

        matrices.pushPose();
        matrices.translate(CONTENT_X0 + 3F, MARQUEE_Y0 - 4F, -0.22F);
        marquee.setVertexConsumer(vertexConsumers);
        marquee.scrollText(matrices, facing);
        matrices.popPose();
    }

    /** 取当前广告的滚动文案；取不到就留空。 */
    protected String getAdvertisementMessage(T entity) {
        if (entity.getCategories().size() == 0) return "";
        final int categoryIndex = entity.getCategoryIndex();
        if (categoryIndex < 0 || categoryIndex >= entity.getCategories().size()) return "";

        final BlockPIDSTianjin.Category category = BlockPIDSTianjin.CATEGORIES.get(entity.getCategories().getLong(categoryIndex));
        if (category == null || category.isEmpty()) return "";

        final BlockPIDSTianjin.Advertisement advertisement = category.get(Math.min(entity.getAdvertisementIndex(), category.size() - 1));
        return advertisement == null || advertisement.text() == null ? "" : advertisement.text().getString();
    }

    /**
     * 两扇屏蔽门：dwelling 时向两侧滑开，其余时间关着。doorProgress 0 关、1 全开。
     */
    private void drawDoor(PoseStack matrices, VertexConsumer consumer, Direction facing, float centerX, float y, float doorProgress) {
        final float frameW = 130F;
        final float frameH = 52F;
        final float frameX = centerX - frameW / 2F;

        // 门框
        fill(matrices, consumer, facing, frameX, y, frameW, frameH, COLOR_DARK);
        // 门洞（黑色）
        fill(matrices, consumer, facing, frameX + 4F, y + 4F, frameW - 8F, frameH - 8F, COLOR_BG);

        // 两扇门，各自向一侧滑开
        final float leafW = (frameW - 8F) / 2F;
        final float leafH = frameH - 8F;
        final float slide = leafW * Math.max(0F, Math.min(1F, doorProgress));

        fill(matrices, consumer, facing, frameX + 4F - slide, y + 4F, leafW, leafH, COLOR_CAR);
        fill(matrices, consumer, facing, frameX + 4F + leafW + slide, y + 4F, leafW, leafH, COLOR_CAR);

        // 门缝与玻璃条
        fill(matrices, consumer, facing, frameX + 4F - slide, y + 4F + leafH * 0.42F, leafW, leafH * 0.18F, COLOR_BG);
        fill(matrices, consumer, facing, frameX + 4F + leafW + slide, y + 4F + leafH * 0.42F, leafW, leafH * 0.18F, COLOR_BG);
    }

    // endregion

    private void renderAd(PoseStack matrices, MultiBufferSource vertexConsumers, T entity, Direction facing) {
        if (entity.getCategories().isEmpty()) {
            return;
        }

        matrices.pushPose();
        matrices.translate(AD_X, AD_Y, 0);

        if (entity.getCategoryIndex() >= entity.getCategories().size()
                || !BlockPIDSTianjin.CATEGORIES.containsKey(entity.getCategories().getLong(entity.getCategoryIndex()))) {
            entity.setCategoryIndex((entity.getCategoryIndex() + 1) % entity.getCategories().size());
            entity.setAdvertisementIndex(0);
            if (!BlockPIDSTianjin.CATEGORIES.containsKey(entity.getCategories().getLong(entity.getCategoryIndex()))) {
                entity.getCategories().clear();
                matrices.popPose();
                return;
            }
        }

        final BlockPIDSTianjin.Category category = BlockPIDSTianjin.CATEGORIES.get(entity.getCategories().getLong(entity.getCategoryIndex()));
        if (entity.getAdvertisementIndex() >= category.size()) {
            entity.setAdvertisementIndex(0);
        }

        final BlockPIDSTianjin.Advertisement advertisement = category.get(entity.getAdvertisementIndex());
        if (advertisement != null) {
            if (entity.advertisement != advertisement) {
                entity.advertisement = advertisement;
                entity.scrollingText.changeImage(() -> ClientCache.DATA_CACHE.getPlainText(
                        "   " + advertisement.text().getString() + "   ", 0xFF1A1D46, IGui.ARGB_WHITE));
            }

            final RenderType adRenderType = MoreRenderLayers.getLight(advertisement.image(), false);
            final VertexConsumer adVertexConsumer = vertexConsumers.getBuffer(adRenderType);
            IDrawing.drawTexture(matrices, adVertexConsumer, 0, 0, AD_W, AD_H, facing, MAX_LIGHT_GLOWING);

            if (advertisement.url() != null) {
                matrices.translate(0, 0, -0.1F);
                final RenderType clickRenderType = MoreRenderLayers.getLight(new ResourceLocation(Reference.MOD_ID, "textures/sign/click.png"), false);
                final VertexConsumer clickVertexConsumer = vertexConsumers.getBuffer(clickRenderType);
                IDrawing.drawTexture(matrices, clickVertexConsumer, 0, 0, 15F, 15F, facing, MAX_LIGHT_GLOWING);
                matrices.translate(0, 0, 0.1F);
            }
        }

        matrices.popPose();
    }

    // region data

    /** 本站台所属线路，用来过滤掉不属于这里的班次。 */
    protected static Set<Long> getRouteIds(Set<Long> platformIds) {
        final Set<Long> routeIds = new HashSet<>();
        if (platformIds == null || platformIds.isEmpty()) {
            return routeIds;
        }
        for (final Route route : ClientData.ROUTES) {
            for (final long platformId : platformIds) {
                if (route.containsPlatformId(platformId)) {
                    routeIds.add(route.id);
                    break;
                }
            }
        }
        return routeIds;
    }

    protected static ScheduleEntry getLockedEntry(List<ScheduleEntry> scheduleList, Set<Long> routeIds) {
        final long now = System.currentTimeMillis();
        for (final ScheduleEntry entry : scheduleList) {
            if (!routeIds.isEmpty() && !routeIds.contains(entry.routeId)) {
                continue;
            }
            final long delta = entry.arrivalMillis - now;
            if (delta <= LOCK_MILLIS && delta >= -DOOR_MILLIS) {
                return entry;
            }
        }

        return null;
    }

    protected static int getPage(List<ScheduleEntry> scheduleList, Set<Long> routeIds) {
        final long now = System.currentTimeMillis();
        for (final ScheduleEntry entry : scheduleList) {
            if (!routeIds.isEmpty() && !routeIds.contains(entry.routeId)) {
                continue;
            }
            final long delta = entry.arrivalMillis - now;
            if (delta <= LOCK_MILLIS && delta >= -DOOR_MILLIS) {
                return PAGE_TRAIN;
            }
        }

        return (int) ((now / PAGE_MILLIS) % 2) == 0 ? PAGE_ARRIVALS : PAGE_ROUTE_MAP;
    }

    protected static String getRouteName(long routeId) {
        final Route route = ClientData.DATA_CACHE.routeIdMap.get(routeId);
        return route == null || route.name == null ? "" : toChineseName(route.name);
    }

    /** 线路名可能是「1号线|Line 1」这种双语串，只保留中文段。 */
    protected static String toChineseName(String name) {
        if (name == null || name.isEmpty()) return "";
        for (final String part : name.split("[|/]")) {
            final String trimmed = part.trim();
            if (!trimmed.isEmpty() && IGui.isCjk(trimmed)) return trimmed;
        }
        return name.trim();
    }

    protected static String getLineName(List<ScheduleEntry> scheduleList) {
        return scheduleList.isEmpty() ? "" : getRouteName(scheduleList.get(0).routeId);
    }

    // endregion

    @Override
    public int getViewDistance() {
        return 96;
    }
}
