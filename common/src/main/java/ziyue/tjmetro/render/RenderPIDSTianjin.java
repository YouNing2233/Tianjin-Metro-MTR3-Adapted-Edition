package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.MTRClient;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.*;
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
import ziyue.tjmetro.block.BlockPIDSTianjinSingle;
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
        if (!(block instanceof BlockPIDSTianjin pidsBlock)) return;
        if (!pidsBlock.canStoreData(world, pos)) return;

        final Direction facing = IBlock.getStatePropertySafe(world, pos, HorizontalDirectionalBlock.FACING);
        final float scale = 320 / maxHeight * textPadding;

        if (entity.renderSingleFace) {
            if (IBlock.getStatePropertySafe(world, pos, BlockPIDSTianjinSingle.SHOULD_RENDER)) {
                renderFace(matrices, vertexConsumers, facing, scale, entity, pos, 0, 0);
            }
        } else {
            renderFace(matrices, vertexConsumers, facing, scale, entity, pos, 0, 0);
            renderFace(matrices, vertexConsumers, facing.getOpposite(), scale, entity, pos, facing.getStepX(), facing.getStepZ());
        }
    }

    private void renderFace(PoseStack matrices, MultiBufferSource vertexConsumers, Direction facing, float scale, T entity, BlockPos pos, float offsetX, float offsetZ) {
        matrices.pushPose();
        matrices.translate(0.5 + offsetX, 0.85, 0.5 + offsetZ);
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees((rotate90 ? 90 : 0) - facing.toYRot()));
        matrices.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
        matrices.translate(-0.48, 0, -0.48);
        matrices.scale(1F / scale, 1F / scale, 1F / scale);
        matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(22.5F));
        matrices.translate(0, 0.05, 0);

        final RenderType bgRenderType = MoreRenderLayers.getExterior(new ResourceLocation(Reference.MOD_ID, "textures/block/pids_tianjin.png"));
        final VertexConsumer bgConsumer = vertexConsumers.getBuffer(bgRenderType);
        IDrawing.drawTexture(matrices, bgConsumer, 0, 0, 251F, 115F, facing, MAX_LIGHT_GLOWING);

        matrices.translate(0, 0, -0.1);

        final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        renderText(matrices, immediate, getFormattedTime(entity), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER, 100F, maxWidth * scale / 16, 0.5F, ARGB_WHITE);

        final Map<Long, String> platformIdToName = new HashMap<>();
        final List<ScheduleEntry> scheduleList = getSchedules(entity, pos, platformIdToName);
        renderArrivals(matrices, immediate, scheduleList, entity.getDisplayPage());

        renderAdvertisement(matrices, vertexConsumers, entity, facing);

        immediate.endBatch();
        matrices.popPose();
    }

    private String getFormattedTime(T entity) {
        if (entity.isUseRealTime()) {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            return String.format("%02d:%02d:%02d",
                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                    calendar.get(java.util.Calendar.MINUTE),
                    calendar.get(java.util.Calendar.SECOND));
        } else {
            final BlockGetter world = entity.getLevel();
            if (world == null) return "";
            final long dayTime = ((net.minecraft.world.level.Level) world).getDayTime();
            final long ticks = dayTime % 24000;
            final long totalSeconds = ticks * 36 / 10;
            final long hours = (totalSeconds / 3600 + 6) % 24;
            final long minutes = totalSeconds / 60 % 60;
            final long seconds = totalSeconds % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
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
                final String arrivalUnit = Text.translatable(
                        (isMinute ? "gui.tjmetro.minute" : "gui.tjmetro.second") + (useChinese ? "_cjk" : "")).getString();

                final float arrivalTimeWidth = getStringWidth(Minecraft.getInstance().font, arrivalTime, 0.4F);
                final float arrivalUnitWidth = getStringWidth(Minecraft.getInstance().font, arrivalUnit, 1.5F);

                renderText(matrices, immediate, arrivalTime, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER - (arrivalTimeWidth + arrivalUnitWidth) / 2, 30F + yOffset, LEFT_TEXT_MAX_WIDTH, 0.4F, 0xFFEFEF00);
                renderText(matrices, immediate, arrivalUnit, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER + (arrivalTimeWidth + arrivalUnitWidth) / 2, 30F + yOffset, LEFT_TEXT_MAX_WIDTH, 1.5F, ARGB_WHITE);
            }
        }
    }

    private float getStringWidth(Font textRenderer, String text, float scale) {
        return IDrawingExtension.stringWidthWithFont(text, scale, 1, false);
    }

    private void renderText(PoseStack matrices, MultiBufferSource.BufferSource immediate, String text, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, float x, float y, float availableWidth, float scale, int color) {
        matrices.pushPose();
        IDrawing.drawStringWithFont(matrices, Minecraft.getInstance().font, immediate, text, horizontalAlignment, verticalAlignment, horizontalAlignment, x, y, availableWidth, -1, scale, color, color, 1, false, MAX_LIGHT_GLOWING, null);
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

        final RenderType adRenderType = MoreRenderLayers.getExterior(advertisement.image());
        final VertexConsumer adVertexConsumer = vertexConsumers.getBuffer(adRenderType);
        IDrawing.drawTexture(matrices, adVertexConsumer, 0, 0, 161F, 88.3F, facing, MAX_LIGHT_GLOWING);

        if (advertisement.url() != null) {
            matrices.translate(0, 0, -0.1F);
            final RenderType clickRenderType = MoreRenderLayers.getExterior(new ResourceLocation(Reference.MOD_ID, "textures/sign/click.png"));
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
        final Set<ScheduleEntry> schedules = new HashSet<>();

        platforms.values().forEach(platform -> {
            if (platformIds.isEmpty() || platformIds.contains(platform.id)) {
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

    @Override
    public int getViewDistance() {
        return Math.min(RenderTrains.maxTrainRenderDistance, 64);
    }
}
