package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.MTRClient;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.*;
import ziyue.tjmetro.client.IDrawingExtends;
import ziyue.tjmetro.client.IDrawingExtension;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.Text;
import mtr.render.MoreRenderLayers;
import mtr.render.RenderTrains;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import ziyue.tjmetro.Reference;
import ziyue.tjmetro.block.BlockPIDSTianjin;
import ziyue.tjmetro.client.ClientCache;

import java.util.*;

public class RenderPIDSTianjin<T extends BlockPIDSTianjin.TileEntityBlockPIDSTianjin> extends BlockEntityRendererMapper<T> implements IGui {

    protected final float maxHeight;
    protected final int maxWidth;
    protected final boolean rotate90;
    protected final float textPadding;

    public static final float LEFT_TEXT_X_CENTER = 42.45F;
    public static final float LEFT_TEXT_MAX_WIDTH = 78.55F;

    private static final int SWITCH_LANGUAGE_TICKS = 60;

    public RenderPIDSTianjin(BlockEntityRenderDispatcher dispatcher) {
        this(dispatcher, 2.5F, 30, true, 1F);
    }

    public RenderPIDSTianjin(BlockEntityRenderDispatcher dispatcher, float maxHeight, int maxWidth, boolean rotate90, float textPadding) {
        super(dispatcher);
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.rotate90 = rotate90;
        this.textPadding = textPadding;
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockPIDSTianjin)) return;
        BlockPIDSTianjin pidsBlock = (BlockPIDSTianjin) block;
        if (!pidsBlock.canStoreData(world, pos)) return;

        final Direction facing = IBlock.getStatePropertySafe(world, pos, HorizontalDirectionalBlock.FACING);
        final float scale = 320 / maxHeight * textPadding;

        renderFace(matrices, vertexConsumers, facing, scale, entity, pos, 0, 0);
        renderFace(matrices, vertexConsumers, facing.getOpposite(), scale, entity, pos, facing.getStepX(), facing.getStepZ());
    }

    /**
     * 屏幕相对方块本体的摆放变换。默认实现即原来的立牌摆放（离地 0.85 格、前倾 22.5 度）；
     * 墙上版与吊板版覆盖它，改成贴墙正立的位置与缩放。
     */
    protected void applyDisplayTransform(PoseStack matrices, Direction facing, float scale, float offsetX, float offsetZ) {
        matrices.translate(0.5 + offsetX, 0.85, 0.5 + offsetZ); // 立牌默认摆放
        matrices.mulPose(Vector3f.YP.rotationDegrees((rotate90 ? 90 : 0) - facing.toYRot()));
        matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
        matrices.translate(-0.48, 0, -0.48);
        matrices.scale(1F / scale, 1F / scale, 1F / scale);
        matrices.mulPose(Vector3f.XP.rotationDegrees(22.5F));
        matrices.translate(0, 0.05, 0);
    }

    protected void renderFace(PoseStack matrices, MultiBufferSource vertexConsumers, Direction facing, float scale, T entity, BlockPos pos, float offsetX, float offsetZ) {
        matrices.pushPose();
        applyDisplayTransform(matrices, facing, scale, offsetX, offsetZ);

        final RenderType bgRenderType = MoreRenderLayers.getLight(new ResourceLocation(Reference.MOD_ID, "textures/block/pids_tianjin.png"), false);
        final VertexConsumer bgConsumer = vertexConsumers.getBuffer(bgRenderType);
        IDrawing.drawTexture(matrices, bgConsumer, 0, 0, 251F, 115F, facing, MAX_LIGHT_GLOWING);

        matrices.translate(0, 0, -0.1);

        final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        renderText(matrices, immediate, getFormattedTime(entity), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER, 106.5F, maxWidth * scale / 16, 0.5F, ARGB_WHITE);

        final Map<Long, String> platformIdToName = new HashMap<>();
        final List<ScheduleEntry> scheduleList = getSchedules(entity, pos, platformIdToName);
        if (entity.isOnlyFirstLastTrain()) {
            renderFirstLastTrain(matrices, immediate, entity, pos, scheduleList);
        } else {
            renderArrivals(matrices, immediate, scheduleList, entity.getDisplayPage());
        }

        renderAdvertisement(matrices, vertexConsumers, entity, facing);

        immediate.endBatch();
        matrices.popPose();
    }

    private String getFormattedTime(T entity) {
        if (entity.isUseRealTime()) {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            return String.format("%02d:%02d",
                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                    calendar.get(java.util.Calendar.MINUTE));
        } else {
            final BlockGetter world = entity.getLevel();
            if (world == null) return "";
            final long dayTime = ((net.minecraft.world.level.Level) world).getDayTime();
            final long ticks = dayTime % 24000;
            final long totalSeconds = ticks * 36 / 10;
            final long hours = (totalSeconds / 3600 + 6) % 24;
            final long minutes = totalSeconds / 60 % 60;
            return String.format("%02d:%02d", hours, minutes);
        }
    }

    private static final float FIRST_LAST_LABEL_Y = 15.5F;
    private static final float FIRST_LAST_TIME_Y = 26.5F;
    private static final float FIRST_LAST_DEST_Y = 35.5F;
    private static final float FIRST_LAST_LABEL_Y_2 = 62F;
    private static final float FIRST_LAST_TIME_Y_2 = 73F;
    private static final float FIRST_LAST_DEST_Y_2 = 82F;
    private static final float FIRST_LAST_LABEL_SCALE = 1.35F;
    private static final float FIRST_LAST_TIME_SCALE = 0.62F;
    private static final float FIRST_LAST_DEST_SCALE = 1.35F;

    protected String[] getFirstLastDepartures(T entity, BlockPos pos, List<ScheduleEntry> scheduleList) {
        final Set<Long> platformIds = getEffectivePlatformIds(entity, pos);
        final long[] range = getDepotDepartureRange(platformIds);
        if (range != null) {
            return new String[]{formatMillisOfDay(range[0]), formatMillisOfDay(range[1]), getDestinationForPlatforms(platformIds)};
        }

        final ScheduleEntry[] firstAndLast = getFirstAndLastTrain(scheduleList);
        if (firstAndLast == null) return null;
        return new String[]{formatScheduleTime(firstAndLast[0].arrivalMillis), formatScheduleTime(firstAndLast[1].arrivalMillis), getPidsDestination(firstAndLast[0])};
    }

    protected Set<Long> getEffectivePlatformIds(T entity, BlockPos pos) {
        final Set<Long> platformIds = entity.getPlatformIds();
        if (!platformIds.isEmpty()) return platformIds;

        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);
        if (station == null) return Collections.emptySet();
        final Map<Long, Platform> platforms = ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id);
        if (platforms.isEmpty()) return Collections.emptySet();

        final long nearestPlatformId = RailwayData.getClosePlatformId(new HashSet<>(platforms.values()), ClientData.DATA_CACHE, pos);
        return nearestPlatformId != 0 ? Collections.singleton(nearestPlatformId) : Collections.emptySet();
    }

    protected static long[] getDepotDepartureRange(Set<Long> platformIds) {
        if (platformIds == null || platformIds.isEmpty()) return null;

        long first = Long.MAX_VALUE;
        long last = Long.MIN_VALUE;

        for (final Route route : ClientData.ROUTES) {
            boolean servesPlatform = false;
            for (final long platformId : platformIds) {
                if (route.containsPlatformId(platformId)) {
                    servesPlatform = true;
                    break;
                }
            }
            if (!servesPlatform) continue;

            final Depot depot = ClientData.DATA_CACHE.routeIdToOneDepot.get(route.id);
            if (depot == null) continue;

            final long[] depotRange = getDepotDepartureRange(depot);
            if (depotRange == null) continue;

            first = Math.min(first, depotRange[0]);
            last = Math.max(last, depotRange[1]);
        }

        return first == Long.MAX_VALUE ? null : new long[]{first, last};
    }

    protected static long[] getDepotDepartureRange(Depot depot) {
        if (depot.departures != null && !depot.departures.isEmpty()) {
            long first = Long.MAX_VALUE;
            long last = Long.MIN_VALUE;
            for (final int departure : depot.departures) {
                final long localMillis = toLocalMillisOfDay(departure);
                first = Math.min(first, localMillis);
                last = Math.max(last, localMillis);
            }
            return new long[]{first, last};
        }

        int firstHour = -1;
        int lastHour = -1;
        for (int hour = 0; hour < Depot.HOURS_IN_DAY; hour++) {
            if (depot.getFrequency(hour) > 0) {
                if (firstHour < 0) firstHour = hour;
                lastHour = hour;
            }
        }
        if (firstHour < 0) return null;
        return new long[]{(long) firstHour * 3600000L, ((long) lastHour + 1) * 3600000L - 1L};
    }

    protected static String getDestinationForPlatforms(Set<Long> platformIds) {
        if (platformIds == null || platformIds.isEmpty()) return "";

        for (final Route route : ClientData.ROUTES) {
            for (final long platformId : platformIds) {
                if (!route.containsPlatformId(platformId)) continue;
                final String[] destinationSplit = ClientData.DATA_CACHE.getFormattedRouteDestination(route, route.getPlatformIdIndex(platformId), "").split("\\|");
                for (final String destination : destinationSplit) {
                    if (!destination.isEmpty()) return destination;
                }
            }
        }
        return "";
    }

    protected static long toLocalMillisOfDay(long utcMillisOfDay) {
        final long offset = java.util.TimeZone.getDefault().getOffset(System.currentTimeMillis());
        final long value = (utcMillisOfDay + offset) % 86400000L;
        return value < 0 ? value + 86400000L : value;
    }

    protected static String formatMillisOfDay(long millisOfDay) {
        final java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm", Locale.ROOT);
        format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return format.format(new java.util.Date(millisOfDay));
    }

    protected void renderFirstLastTrain(PoseStack matrices, MultiBufferSource.BufferSource immediate, T entity, BlockPos pos, List<ScheduleEntry> scheduleList) {
        final String[] values = getFirstLastDepartures(entity, pos, scheduleList);
        if (values == null) return;

        drawFirstLastRow(matrices, immediate, "gui.tjmetro.first_train", values[0], values[2], LEFT_TEXT_X_CENTER, LEFT_TEXT_MAX_WIDTH, FIRST_LAST_LABEL_Y, FIRST_LAST_TIME_Y, FIRST_LAST_DEST_Y);
        drawFirstLastRow(matrices, immediate, "gui.tjmetro.last_train", values[1], values[2], LEFT_TEXT_X_CENTER, LEFT_TEXT_MAX_WIDTH, FIRST_LAST_LABEL_Y_2, FIRST_LAST_TIME_Y_2, FIRST_LAST_DEST_Y_2);
    }

    protected void drawFirstLastRow(PoseStack matrices, MultiBufferSource.BufferSource immediate, String labelKey, String time, String destination, float centerX, float availableWidth, float labelY, float timeY, float destinationY) {
        final boolean useChinese = IGui.isCjk(destination);

        renderText(matrices, immediate, Text.translatable(labelKey + (useChinese ? "_cjk" : "")).getString(),
                HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, labelY, availableWidth, FIRST_LAST_LABEL_SCALE, ARGB_WHITE);
        renderText(matrices, immediate, time,
                HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, timeY, availableWidth, FIRST_LAST_TIME_SCALE, 0xFFEFEF00);
        if (!destination.isEmpty()) {
            renderText(matrices, immediate, Text.translatable(useChinese ? "gui.tjmetro.bound_for_pids_cjk" : "gui.tjmetro.bound_for_pids", destination).getString(),
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER, centerX, destinationY, availableWidth, FIRST_LAST_DEST_SCALE, ARGB_WHITE);
        }
    }

    protected static ScheduleEntry[] getFirstAndLastTrain(List<ScheduleEntry> scheduleList) {
        if (scheduleList == null || scheduleList.isEmpty()) return null;

        final Calendar today = Calendar.getInstance();
        ScheduleEntry first = null;
        ScheduleEntry last = null;

        for (final ScheduleEntry scheduleEntry : scheduleList) {
            final Calendar entryDay = Calendar.getInstance();
            entryDay.setTimeInMillis(scheduleEntry.arrivalMillis);
            if (entryDay.get(Calendar.YEAR) != today.get(Calendar.YEAR) || entryDay.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)) {
                continue;
            }
            if (first == null || scheduleEntry.arrivalMillis < first.arrivalMillis) first = scheduleEntry;
            if (last == null || scheduleEntry.arrivalMillis > last.arrivalMillis) last = scheduleEntry;
        }

        if (first == null) {
            for (final ScheduleEntry scheduleEntry : scheduleList) {
                if (first == null || scheduleEntry.arrivalMillis < first.arrivalMillis) first = scheduleEntry;
                if (last == null || scheduleEntry.arrivalMillis > last.arrivalMillis) last = scheduleEntry;
            }
        }

        return new ScheduleEntry[]{first, last};
    }

    protected static String formatScheduleTime(long millis) {
        return new java.text.SimpleDateFormat("HH:mm", Locale.ROOT).format(new java.util.Date(millis));
    }

    protected static String getPidsDestination(ScheduleEntry scheduleEntry) {
        final Route route = ClientData.DATA_CACHE.routeIdMap.get(scheduleEntry.routeId);
        if (route == null) return "";
        final String[] destinationSplit = ClientData.DATA_CACHE.getFormattedRouteDestination(route, scheduleEntry.currentStationIndex, "").split("\\|");
        for (final String destination : destinationSplit) {
            if (!destination.isEmpty()) return destination;
        }
        return "";
    }

    private void renderArrivals(PoseStack matrices, MultiBufferSource.BufferSource immediate, List<ScheduleEntry> scheduleList, int displayPage) {
        final int languageTicks = (int) Math.floor(MTRClient.getGameTick()) / SWITCH_LANGUAGE_TICKS;
        int arrivalIndex = displayPage * 2;

        for (int i = 0; i < 2; i++) {
            final ScheduleEntry scheduleEntry = i + arrivalIndex < scheduleList.size() ? scheduleList.get(i + arrivalIndex) : null;
            if (scheduleEntry == null) continue;

            final Route route = ClientData.DATA_CACHE.routeIdMap.get(scheduleEntry.routeId);
            if (route == null) continue;

            final String[] destinationSplit = ClientData.DATA_CACHE.getFormattedRouteDestination(route, scheduleEntry.currentStationIndex, "").split("\\|");
            final String[] routeNumberSplit = route.lightRailRouteNumber.split("\\|");

            String[] finalDestinationSplit;
            if (routeNumberSplit.length == 0 || routeNumberSplit[0].isEmpty()) {
                finalDestinationSplit = destinationSplit;
            } else {
                final List<String> newDestinations = new ArrayList<>();
                int destinationIndex = 0;
                int numberIndex = 0;
                while (true) {
                    final String newDestination = String.format("%s %s",
                            routeNumberSplit[numberIndex % routeNumberSplit.length],
                            destinationSplit[destinationIndex % destinationSplit.length]);
                    if (newDestinations.contains(newDestination)) break;
                    newDestinations.add(newDestination);
                    destinationIndex++;
                    numberIndex++;
                }
                finalDestinationSplit = newDestinations.toArray(new String[0]);
            }

            final int languageIndex = languageTicks % finalDestinationSplit.length;
            final String destination = finalDestinationSplit[languageIndex];
            final boolean isCjk = IGui.isCjk(destination);
            final float yOffset = i * 45F;

            final boolean useChinese = isCjk || (finalDestinationSplit.length == 1 && languageTicks % 2 == 0);

            final String destinationFormatted = Text.translatable(
                    useChinese ? "gui.tjmetro.bound_for_pids_cjk" : "gui.tjmetro.bound_for_pids",
                    destination).getString();

            final String trainLabel = Text.translatable(i == 0
                    ? (useChinese ? "gui.tjmetro.this_train_cjk" : "gui.tjmetro.this_train")
                    : (useChinese ? "gui.tjmetro.next_train_cjk" : "gui.tjmetro.next_train")).getString();

            renderText(matrices, immediate, trainLabel, HorizontalAlignment.CENTER, VerticalAlignment.TOP, LEFT_TEXT_X_CENTER, 8F + yOffset, LEFT_TEXT_MAX_WIDTH, 1.5F, ARGB_WHITE);
            renderText(matrices, immediate, destinationFormatted, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, LEFT_TEXT_X_CENTER, 45F + yOffset, LEFT_TEXT_MAX_WIDTH, 1.3F, ARGB_WHITE);

            final long arrivalSeconds = (scheduleEntry.arrivalMillis - System.currentTimeMillis()) / 1000;
            if (arrivalSeconds <= 15) {
                final String textKey = (arrivalSeconds < 0 ? "gui.tjmetro.arrived" : "gui.tjmetro.arriving") + (useChinese ? "_cjk" : "");
                renderText(matrices, immediate, Text.translatable(textKey).getString(), HorizontalAlignment.CENTER, VerticalAlignment.TOP, LEFT_TEXT_X_CENTER, 21.5F + yOffset, LEFT_TEXT_MAX_WIDTH, 0.6F, 0xFFEFEF00);
            } else {
                final boolean isMinute = arrivalSeconds > 60;
                final String arrivalTime = String.valueOf(isMinute ? arrivalSeconds / 60 : arrivalSeconds);
                final String arrivalUnit = Text.translatable((isMinute ? "gui.tjmetro.minute" : "gui.tjmetro.second") + (useChinese ? "_cjk" : "")).getString();

                final float arrivalTimeWidth = getStringWidth(Minecraft.getInstance().font, arrivalTime, 0.4F);
                final float arrivalUnitWidth = getStringWidth(Minecraft.getInstance().font, arrivalUnit, 1.5F);

                renderText(matrices, immediate, arrivalTime, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER - (arrivalTimeWidth + arrivalUnitWidth) / 2, 30F + yOffset, LEFT_TEXT_MAX_WIDTH, 0.4F, 0xFFEFEF00);
                renderText(matrices, immediate, arrivalUnit, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER + (arrivalTimeWidth + arrivalUnitWidth) / 2, 30F + yOffset, LEFT_TEXT_MAX_WIDTH, 1.5F, ARGB_WHITE);
            }
        }
    }

    protected float getStringWidth(Font textRenderer, String text, float scale) {
        return IDrawingExtension.stringWidthWithFont(text, scale, 1, false);
    }

    protected void renderText(PoseStack matrices, MultiBufferSource.BufferSource immediate, String text, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, float x, float y, float availableWidth, float scale, int color) {
        matrices.pushPose();
        IDrawingExtends.drawStringWithFont(matrices, Minecraft.getInstance().font, immediate, text, horizontalAlignment, verticalAlignment, horizontalAlignment, x, y, availableWidth, -1, scale, color, color, 1, false, MAX_LIGHT_GLOWING, null);
        matrices.popPose();
    }

    private void renderAdvertisement(PoseStack matrices, MultiBufferSource vertexConsumers, T entity, Direction facing) {
        if (entity.getCategories().isEmpty()) return;

        matrices.translate(86.7, 3.38, 0);

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
        if (entity.getAdvertisementIndex() >= category.size()) {
            entity.setAdvertisementIndex(0);
        }
        final BlockPIDSTianjin.Advertisement advertisement = category.get(entity.getAdvertisementIndex());
        if (advertisement == null) return;

        if (entity.advertisement != advertisement) {
            entity.advertisement = advertisement;
            entity.scrollingText.changeImage(() -> ClientCache.DATA_CACHE.getPlainText(
                    "   " + advertisement.text().getString() + "   ", 0xFF1A1D46, ARGB_WHITE));
        }

        final RenderType adRenderType = MoreRenderLayers.getLight(advertisement.image(), false);
        final VertexConsumer adVertexConsumer = vertexConsumers.getBuffer(adRenderType);
        IDrawing.drawTexture(matrices, adVertexConsumer, 0, 0, 161F, 88.3F, facing, MAX_LIGHT_GLOWING);

        if (advertisement.url() != null) {
            matrices.translate(0, 0, -0.1F);
            final RenderType clickRenderType = MoreRenderLayers.getLight(new ResourceLocation(Reference.MOD_ID, "textures/sign/click.png"), false);
            final VertexConsumer clickVertexConsumer = vertexConsumers.getBuffer(clickRenderType);
            IDrawing.drawTexture(matrices, clickVertexConsumer, 0, 0, 15F, 15F, facing, MAX_LIGHT_GLOWING);
            matrices.translate(0, 0, 0.1F);
        }

        matrices.translate(1.5F, 75.3F, 0);
        entity.scrollingText.setVertexConsumer(vertexConsumers);
        final boolean scrolledComplete = entity.scrollingText.scrollText(matrices, facing);
        if (scrolledComplete && advertisement.text().getString() != null && !advertisement.text().getString().isEmpty()) {
            nextSlide(entity);
        }
    }

    protected void nextSlide(T entity) {
        final int categoryIndex = entity.getCategoryIndex();
        final int advertisementIndex = entity.getAdvertisementIndex();
        final BlockPIDSTianjin.Category category = BlockPIDSTianjin.CATEGORIES.get(entity.getCategories().getLong(categoryIndex));

        if (advertisementIndex + 1 >= category.size()) {
            entity.setCategoryIndex((categoryIndex + 1) % entity.getCategories().size());
            entity.setAdvertisementIndex(0);
        } else {
            entity.setAdvertisementIndex(advertisementIndex + 1);
        }
    }

    public List<ScheduleEntry> getSchedules(T entity, BlockPos pos, final Map<Long, String> platformIdToName) {
        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);
        if (station == null) {
            return new ArrayList<>();
        }

        final Map<Long, Platform> platforms = ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id);
        if (platforms.isEmpty()) {
            return new ArrayList<>();
        }

        final Set<Long> platformIds = entity.getPlatformIds();
        final Set<Long> effectivePlatformIds;
        if (platformIds.isEmpty()) {
            // Auto mode: only show the schedules of the single nearest platform.
            final long nearestPlatformId = RailwayData.getClosePlatformId(new HashSet<>(platforms.values()), ClientData.DATA_CACHE, pos);
            effectivePlatformIds = nearestPlatformId != 0 ? Collections.singleton(nearestPlatformId) : Collections.emptySet();
        } else {
            effectivePlatformIds = platformIds;
        }
        final Set<ScheduleEntry> schedules = new HashSet<>();

        platforms.values().forEach(platform -> {
            if (effectivePlatformIds.contains(platform.id)) {
                final Set<ScheduleEntry> scheduleForPlatform = ClientData.SCHEDULES_FOR_PLATFORM.get(platform.id);
                if (scheduleForPlatform != null) {
                    scheduleForPlatform.forEach(scheduleEntry -> {
                        final Route route = ClientData.DATA_CACHE.routeIdMap.get(scheduleEntry.routeId);
                        if (route != null && scheduleEntry.currentStationIndex < route.platformIds.size() - 1) {
                            schedules.add(scheduleEntry);
                            platformIdToName.put(platform.id, platform.name);
                        }
                    });
                }
            }
        });

        final List<ScheduleEntry> scheduleList = new ArrayList<>(schedules);
        Collections.sort(scheduleList);
        return scheduleList;
    }

    @Override
    public boolean shouldRenderOffScreen(T entity) {
        return true;
    }

    // 1.16.5 的 BlockEntityRenderer 没有 getViewDistance()，该覆写在 1.18+ 才存在，故此处省略。
}
