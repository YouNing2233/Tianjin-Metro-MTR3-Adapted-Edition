package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.data.Route;
import mtr.data.ScheduleEntry;
import mtr.data.Station;
import mtr.mappings.Text;
import mtr.render.MoreRenderLayers;
import mtr.render.RenderTrains;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.Reference;
import ziyue.tjmetro.block.BlockPIDSTianjin;
import ziyue.tjmetro.block.BlockPSDGlassScreenTianjin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Renders the passenger information board embedded in
 * {@link BlockPSDGlassScreenTianjin}.
 *
 * <p>The board spans three glass panels and two blocks in height; only the
 * bottom-left panel of the group draws it. Typography copies the exact unit
 * system, scales, alignments and colours of {@link RenderPIDSTianjin}.</p>
 *
 * @author ZiYueCommentary
 * @since 1.1.8
 */
public class RenderPSDGlassScreenTianjin extends RenderPIDSTianjin<BlockPSDGlassScreenTianjin.TileEntityPSDGlassScreenTianjin>
{
    /** 128 drawing units per block, identical to the PIDS renderer. */
    private static final float UNITS_PER_BLOCK = 128F;

    /** Distance of the glass plate centre from the block centre, in blocks. 8/16 - 3/16 = 5/16. */
    private static final float PLATE_FROM_CENTER = 0.3125F;

    private static final float BOARD_WIDTH = 368F;
    private static final float BOARD_HEIGHT = 224F;

    private static final float ROW_X_CENTER = 309.5F;
    private static final float ROW_MAX_WIDTH = 108F;
    private static final float[] ROW_TOP = {5F, 61F, 120F};

    private static final float AD_X = 3F;
    private static final float AD_Y = 3F;
    private static final float AD_WIDTH = 243F;
    private static final float AD_HEIGHT = 170F;

    private static final float CLOCK_X = 309.5F;
    private static final float CLOCK_Y = 200F;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int YELLOW = 0xFFEFEF00;

    private static final ResourceLocation TEXTURE_BOARD = new ResourceLocation(Reference.MOD_ID, "textures/block/psd_glass_screen_tianjin.png");
    private static final ResourceLocation TEXTURE_ERROR = new ResourceLocation(Reference.MOD_ID, "textures/block/psd_glass_screen_tianjin_error.png");

    private static final int PANEL_COUNT = 3;
    private static final int SCAN_LIMIT = 16;

    public RenderPSDGlassScreenTianjin(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockPSDGlassScreenTianjin.TileEntityPSDGlassScreenTianjin entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos blockPos = entity.getBlockPos();
        final BlockState blockState = world.getBlockState(blockPos);
        if (!(blockState.getBlock() instanceof BlockPSDGlassScreenTianjin)) return;
        if (!isLower(blockState)) return;

        final Direction facing = IBlock.getStatePropertySafe(blockState, HorizontalDirectionalBlock.FACING);
        final BlockPos startPos = findStart(world, blockPos, facing);
        if (!startPos.equals(blockPos)) return;

        final int panelCount = countPanels(world, startPos, facing);

        final Direction right = facing.getCounterClockWise();
        final float originX = startPos.getX() + 0.5F - right.getStepX() * 0.5F + facing.getStepX() * PLATE_FROM_CENTER;
        final float originY = startPos.getY() + 2F;
        final float originZ = startPos.getZ() + 0.5F - right.getStepZ() * 0.5F + facing.getStepZ() * PLATE_FROM_CENTER;

        matrices.pushPose();
        matrices.translate(originX, originY, originZ);
        // +X = the viewer's right hand side, +Y = downwards (font friendly), one unit = 1/128 block
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180F - facing.toYRot()));
        matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180F));
        matrices.scale(1F / UNITS_PER_BLOCK, 1F / UNITS_PER_BLOCK, 1F / UNITS_PER_BLOCK);

        if (panelCount != PANEL_COUNT) {
            final VertexConsumer errorConsumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(TEXTURE_ERROR, false));
            IDrawing.drawTexture(matrices, errorConsumer, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, facing, MAX_LIGHT_GLOWING);
            matrices.popPose();
            return;
        }

        final VertexConsumer boardConsumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(TEXTURE_BOARD, false));
        IDrawing.drawTexture(matrices, boardConsumer, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, facing, MAX_LIGHT_GLOWING);

        renderAdvertisement(matrices, vertexConsumers, entity, facing);

        final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        renderClock(matrices, immediate, entity);
        renderArrivals(matrices, immediate, entity, blockPos);
        immediate.endBatch();

        matrices.popPose();
    }

    private void renderClock(PoseStack matrices, MultiBufferSource.BufferSource immediate, BlockPSDGlassScreenTianjin.TileEntityPSDGlassScreenTianjin entity) {
        renderText(matrices, immediate, getFormattedTime(entity), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, CLOCK_X, CLOCK_Y, ROW_MAX_WIDTH, 0.5F, WHITE);
    }

    private void renderArrivals(PoseStack matrices, MultiBufferSource.BufferSource immediate, BlockPSDGlassScreenTianjin.TileEntityPSDGlassScreenTianjin entity, BlockPos pos) {
        final List<ScheduleEntry> scheduleList = getSchedules(entity, pos, new HashMap<>());
        final int languageTicks = (int) Math.floor(mtr.MTRClient.getGameTick()) / 60;

        for (int i = 0; i < ROW_TOP.length; i++) {
            final ScheduleEntry scheduleEntry = i < scheduleList.size() ? scheduleList.get(i) : null;
            if (scheduleEntry == null) continue;

            final Route route = ClientData.DATA_CACHE.routeIdMap.get(scheduleEntry.routeId);
            if (route == null) continue;

            final float rowTop = ROW_TOP[i];
            final String destination = getDestination(route, scheduleEntry, languageTicks);
            final boolean useChinese = IGui.isCjk(destination) || languageTicks % 2 == 0;

            renderText(matrices, immediate, Text.translatable(getTrainLabelKey(i, useChinese)).getString(), HorizontalAlignment.CENTER, VerticalAlignment.TOP, ROW_X_CENTER, rowTop + 6F, ROW_MAX_WIDTH, 1.5F, WHITE);

            final String destinationFormatted = Text.translatable(useChinese ? "gui.tjmetro.bound_for_pids_cjk" : "gui.tjmetro.bound_for_pids", destination).getString();
            renderText(matrices, immediate, destinationFormatted, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, ROW_X_CENTER, rowTop + 51F, ROW_MAX_WIDTH, 1.3F, WHITE);

            final long arrivalSeconds = (scheduleEntry.arrivalMillis - System.currentTimeMillis()) / 1000L;
            if (arrivalSeconds <= 15) {
                final String textKey = (arrivalSeconds < 0 ? "gui.tjmetro.arrived" : "gui.tjmetro.arriving") + (useChinese ? "_cjk" : "");
                renderText(matrices, immediate, Text.translatable(textKey).getString(), HorizontalAlignment.CENTER, VerticalAlignment.TOP, ROW_X_CENTER, rowTop + 20F, ROW_MAX_WIDTH, 0.6F, YELLOW);
            } else {
                final boolean isMinute = arrivalSeconds > 60;
                final String arrivalTime = String.valueOf(isMinute ? arrivalSeconds / 60 : arrivalSeconds);
                final String arrivalUnit = Text.translatable((isMinute ? "gui.tjmetro.minute" : "gui.tjmetro.second") + (useChinese ? "_cjk" : "")).getString();
                final float arrivalTimeWidth = getStringWidth(net.minecraft.client.Minecraft.getInstance().font, arrivalTime, 0.4F);
                final float arrivalUnitWidth = getStringWidth(net.minecraft.client.Minecraft.getInstance().font, arrivalUnit, 1.5F);

                renderText(matrices, immediate, arrivalTime, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, ROW_X_CENTER - (arrivalTimeWidth + arrivalUnitWidth) / 2, rowTop + 30F, ROW_MAX_WIDTH, 0.4F, YELLOW);
                renderText(matrices, immediate, arrivalUnit, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, ROW_X_CENTER + (arrivalTimeWidth + arrivalUnitWidth) / 2, rowTop + 30F, ROW_MAX_WIDTH, 1.5F, WHITE);
            }
        }
    }

    private static String getTrainLabelKey(int index, boolean useChinese) {
        final String suffix = useChinese ? "_cjk" : "";
        switch (index) {
            case 0:
                return "gui.tjmetro.this_train" + suffix;
            case 1:
                return "gui.tjmetro.next_train" + suffix;
            default:
                return "gui.tjmetro.following_train" + suffix;
        }
    }

    private void renderAdvertisement(PoseStack matrices, MultiBufferSource vertexConsumers, BlockPSDGlassScreenTianjin.TileEntityPSDGlassScreenTianjin entity, Direction facing) {
        if (entity.getCategories().isEmpty()) return;

        if (entity.getCategoryIndex() >= entity.getCategories().size() || !BlockPIDSTianjin.CATEGORIES.containsKey(entity.getCategories().getLong(entity.getCategoryIndex()))) {
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

        entity.advertisement = advertisement;

        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(MoreRenderLayers.getLight(advertisement.image(), false));
        IDrawing.drawTexture(matrices, vertexConsumer, AD_X, AD_Y, AD_WIDTH, AD_HEIGHT, facing, MAX_LIGHT_GLOWING);
    }

    private static String getDestination(Route route, ScheduleEntry scheduleEntry, int languageTicks) {
        final String[] destinationSplit = ClientData.DATA_CACHE.getFormattedRouteDestination(route, scheduleEntry.currentStationIndex, "").split("\\|");
        final String[] routeNumberSplit = route.lightRailRouteNumber.split("\\|");

        final List<String> destinations = new ArrayList<>();
        if (routeNumberSplit.length == 0 || routeNumberSplit[0].isEmpty()) {
            Collections.addAll(destinations, destinationSplit);
        } else {
            int destinationIndex = 0;
            int numberIndex = 0;
            while (true) {
                final String newDestination = String.format("%s %s", routeNumberSplit[numberIndex % routeNumberSplit.length], destinationSplit[destinationIndex % destinationSplit.length]);
                if (destinations.contains(newDestination)) break;
                destinations.add(newDestination);
                destinationIndex++;
                numberIndex++;
            }
        }

        return destinations.isEmpty() ? "" : destinations.get(languageTicks % destinations.size());
    }

    /**
     * @return true when the given state is the lower half of the double block.
     * The property type is {@code Half} before 1.20.1 and {@code DoubleBlockHalf}
     * afterwards, so the value is compared by name to stay source compatible.
     */
    private static boolean isLower(BlockState blockState) {
        final Object half = IBlock.getStatePropertySafe(blockState, IBlock.HALF);
        return half instanceof Enum<?> && ((Enum<?>) half).name().equals("LOWER");
    }

    private static boolean isScreenGlass(Level world, BlockPos pos, Direction facing) {
        final BlockState blockState = world.getBlockState(pos);
        return blockState.getBlock() instanceof BlockPSDGlassScreenTianjin
                && IBlock.getStatePropertySafe(blockState, HorizontalDirectionalBlock.FACING) == facing
                && isLower(blockState);
    }

    private static BlockPos findStart(Level world, BlockPos pos, Direction facing) {
        final Direction wallNegative = facing.getCounterClockWise();
        BlockPos startPos = pos;
        for (int i = 0; i < SCAN_LIMIT; i++) {
            final BlockPos nextPos = startPos.relative(wallNegative);
            if (!isScreenGlass(world, nextPos, facing)) break;
            startPos = nextPos;
        }
        return startPos;
    }

    private static int countPanels(Level world, BlockPos startPos, Direction facing) {
        final Direction wallPositive = facing.getClockWise();
        int count = 0;
        BlockPos currentPos = startPos;
        while (count <= SCAN_LIMIT && isScreenGlass(world, currentPos, facing)) {
            count++;
            currentPos = currentPos.relative(wallPositive);
        }
        return count;
    }

    private String getFormattedTime(BlockPSDGlassScreenTianjin.TileEntityPSDGlassScreenTianjin entity) {
        if (entity.isUseRealTime()) {
            final Calendar calendar = Calendar.getInstance();
            return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } else {
            final Level world = entity.getLevel();
            if (world == null) return "";
            final long ticks = world.getDayTime() % 24000;
            final long totalSeconds = ticks * 36 / 10;
            return String.format("%02d:%02d", (totalSeconds / 3600 + 6) % 24, totalSeconds / 60 % 60);
        }
    }

    public List<ScheduleEntry> getSchedules(BlockPSDGlassScreenTianjin.TileEntityPSDGlassScreenTianjin entity, BlockPos pos, Map<Long, String> platformIdToName) {
        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);
        if (station == null) return new ArrayList<>();

        final Map<Long, Platform> platforms = ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id);
        if (platforms.isEmpty()) return new ArrayList<>();

        final Set<Long> platformIds = entity.getPlatformIds();
        final Set<Long> effectivePlatformIds;
        if (platformIds.isEmpty()) {
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

}
