package ziyue.tjmetro;

import mtr.CreativeModeTabs;
import mtr.RegistryObject;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import ziyue.filters.Filter;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.function.BiConsumer;

import static ziyue.tjmetro.client.Filters.*;
import static ziyue.tjmetro.packet.IPacket.*;

/**
 * Entry of <b>Tianjin Metro Mod</b>.
 *
 * @since beta-1
 */

public class TianjinMetro
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);

    public static final CreativeModeTabs.Wrapper CREATIVE_MODE_TAB = new CreativeModeTabs.Wrapper(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "tjmetro_tab"), () -> new ItemStack(BlockList.LOGO.get()));
    public static final GameRules.Key<GameRules.BooleanValue> PREVENT_BLOCK_FALLING = Registry.registerBooleanGameRule("preventBlockFalling", false);
    public static final GameRules.Key<GameRules.IntegerValue> PLAYER_DETECT_RANGE = Registry.registerIntegerGameRule("playerDetectRange", 3);
    public static final GameRules.Key<GameRules.IntegerValue> SMOKE_ALARM_RANGE = Registry.registerIntegerGameRule("smokeAlarmRange", 8);


    public static void init(
            BiConsumer<String, RegistryObject<Block>> registerBlock,
            RegisterBlockItem registerBlockItem,
            RegisterItem registerItem,
            RegisterBlockItem registerEnchantedBlockItem,
            BiConsumer<String, RegistryObject<? extends BlockEntityType<? extends BlockEntityMapper>>> registerBlockEntityType,
            BiConsumer<String, RegistryObject<? extends EntityType<? extends Entity>>> registerEntityType
    ) {
        /* MISCELLANEOUS */
        registerItem.accept("wrench", ItemList.WRENCH, MISCELLANEOUS);
        registerBlockItem.accept("high_speed_repeater", BlockList.HIGH_SPEED_REPEATER, MISCELLANEOUS);
        registerBlockItem.accept("player_detector", BlockList.PLAYER_DETECTOR, MISCELLANEOUS);
        /* BUILDING - platforms, marbles, custom color blocks, then functional blocks */
        registerBlockItem.accept("rolling", BlockList.ROLLING, BUILDING);
        registerBlockItem.accept("platform_tj_1", BlockList.PLATFORM_TJ_1, BUILDING);
        registerBlockItem.accept("platform_tj_1_indented", BlockList.PLATFORM_TJ_1_INDENTED, BUILDING);
        registerBlockItem.accept("platform_tj_1_slab", BlockList.PLATFORM_TJ_1_SLAB, BUILDING);
        registerBlockItem.accept("platform_tj_line_11_1", BlockList.PLATFORM_TJ_LINE_11_1, BUILDING);
        registerBlockItem.accept("platform_tj_line_11_1_indented", BlockList.PLATFORM_TJ_LINE_11_1_INDENTED, BUILDING);
        registerBlockItem.accept("platform_tj_line_11_1_slab", BlockList.PLATFORM_TJ_LINE_11_1_SLAB, BUILDING);
        registerBlockItem.accept("platform_tj_2", BlockList.PLATFORM_TJ_2, BUILDING);
        registerBlockItem.accept("platform_tj_2_indented", BlockList.PLATFORM_TJ_2_INDENTED, BUILDING);
        registerBlockItem.accept("platform_tj_2_slab", BlockList.PLATFORM_TJ_2_SLAB, BUILDING);
        registerBlockItem.accept("platform_tj_line_11_2", BlockList.PLATFORM_TJ_LINE_11_2, BUILDING);
        registerBlockItem.accept("platform_tj_line_11_2_indented", BlockList.PLATFORM_TJ_LINE_11_2_INDENTED, BUILDING);
        registerBlockItem.accept("platform_tj_line_11_2_slab", BlockList.PLATFORM_TJ_LINE_11_2_SLAB, BUILDING);
        registerBlockItem.accept("marble_gray", BlockList.MARBLE_GRAY, BUILDING);
        registerBlockItem.accept("marble_gray_slab", BlockList.MARBLE_GRAY_SLAB, BUILDING);
        registerBlockItem.accept("marble_gray_stairs", BlockList.MARBLE_GRAY_STAIRS, BUILDING);
        registerBlockItem.accept("marble_yellow", BlockList.MARBLE_YELLOW, BUILDING);
        registerBlockItem.accept("marble_yellow_slab", BlockList.MARBLE_YELLOW_SLAB, BUILDING);
        registerBlockItem.accept("marble_yellow_stairs", BlockList.MARBLE_YELLOW_STAIRS, BUILDING);
        registerEnchantedBlockItem.accept("custom_color_concrete", BlockList.CUSTOM_COLOR_CONCRETE, BUILDING);
        registerEnchantedBlockItem.accept("custom_color_concrete_stairs", BlockList.CUSTOM_COLOR_CONCRETE_STAIRS, BUILDING);
        registerEnchantedBlockItem.accept("custom_color_concrete_slab", BlockList.CUSTOM_COLOR_CONCRETE_SLAB, BUILDING);
        /* 1.1.0 - Ported from 4.0 */
        registerEnchantedBlockItem.accept("custom_color_mosaic_tile", BlockList.CUSTOM_COLOR_MOSAIC_TILE, BUILDING);
        registerEnchantedBlockItem.accept("custom_color_mosaic_tile_stairs", BlockList.CUSTOM_COLOR_MOSAIC_TILE_STAIRS, BUILDING);
        registerEnchantedBlockItem.accept("custom_color_mosaic_tile_slab", BlockList.CUSTOM_COLOR_MOSAIC_TILE_SLAB, BUILDING);
        registerBlockItem.accept("mosaic_tile_white", BlockList.MOSAIC_TILE_WHITE, BUILDING);
        registerBlockItem.accept("mosaic_tile_gray", BlockList.MOSAIC_TILE_GRAY, BUILDING);
        /* 1.1.1 - Ported from 4.0 */
        registerEnchantedBlockItem.accept("metal_pole_bmt", BlockList.METAL_POLE_BMT, BUILDING);
        registerBlockItem.accept("metal_detection_door", BlockList.METAL_DETECTION_DOOR, BUILDING);
        registerBlockItem.accept("roadblock", BlockList.ROADBLOCK, BUILDING);
        registerBlockItem.accept("roadblock_sign", BlockList.ROADBLOCK_SIGN, BUILDING);
        /* 1.1.8 - Ported from 4.0 - Station name plate */
        registerBlockItem.accept("station_name_plate", BlockList.STATION_NAME_PLATE, SIGNS);
        registerBlock.accept("station_name_plate_middle", BlockList.STATION_NAME_PLATE_MIDDLE);
        /* 1.1.8 - Ported from 4.0 - Tianjin station name sign */
        registerBlockItem.accept("station_name_sign_tianjin", BlockList.STATION_NAME_SIGN_TIANJIN, SIGNS);
        /* 1.1.8 - Ported from 4.0 - BMT route map */
        registerBlockItem.accept("route_map_bmt", BlockList.ROUTE_MAP_BMT, SIGNS);
        registerBlock.accept("route_map_bmt_middle", BlockList.ROUTE_MAP_BMT_MIDDLE);
        /* 1.1.9 - Wall-mounted BMT route map */
        registerBlockItem.accept("route_map_bmt_wall", BlockList.ROUTE_MAP_BMT_WALL, SIGNS);
        registerBlock.accept("route_map_bmt_wall_middle", BlockList.ROUTE_MAP_BMT_WALL_MIDDLE);
        /* 1.1.3 - Ported from 1.1.3 - Single-block railway sign */
        registerBlockItem.accept("railway_sign_single", BlockList.RAILWAY_SIGN_SINGLE, SIGNS);
        /* 1.1.8 - Ported from 4.0 - Security check sign */
        registerBlockItem.accept("security_check_sign", BlockList.SECURITY_CHECK_SIGN, SIGNS);
        /* SIGNS */
        registerBlockItem.accept("station_name_sign_2", BlockList.STATION_NAME_SIGN_2, SIGNS);
        registerBlockItem.accept("station_name_wall_legacy", BlockList.STATION_NAME_WALL_LEGACY, SIGNS);
        registerBlockItem.accept("station_name_entrance_tianjin", BlockList.STATION_NAME_ENTRANCE_TIANJIN, SIGNS);
        registerBlockItem.accept("station_name_entrance_tianjin_pinyin", BlockList.STATION_NAME_ENTRANCE_TIANJIN_PINYIN, SIGNS);
        registerBlockItem.accept("station_name_entrance_tianjin_bmt", BlockList.STATION_NAME_ENTRANCE_TIANJIN_BMT, SIGNS);
        registerBlockItem.accept("station_name_entrance_tianjin_bmt_pinyin", BlockList.STATION_NAME_ENTRANCE_TIANJIN_BMT_PINYIN, SIGNS);
        registerBlockItem.accept("station_name_entrance_tianjin_jinjing", BlockList.STATION_NAME_ENTRANCE_TIANJIN_JINJING, SIGNS);
        registerBlockItem.accept("station_name_entrance_tianjin_jinjing_pinyin", BlockList.STATION_NAME_ENTRANCE_TIANJIN_JINJING_PINYIN, SIGNS);
        registerEnchantedBlockItem.accept("station_name_sign_1", BlockList.STATION_NAME_SIGN_1, SIGNS);
        /* 1.1.0 - Ported from 4.0 */
        registerBlockItem.accept("station_sign", BlockList.STATION_SIGN, SIGNS);
        registerBlockItem.accept("station_sign_bmt", BlockList.STATION_SIGN_BMT, SIGNS);
        registerBlockItem.accept("no_entering_sign", BlockList.NO_ENTERING_SIGN, SIGNS);
        /* 1.1.1 - Ported from 4.0 */
        registerBlockItem.accept("railway_sign_tianjin_bmt_2_odd", BlockList.RAILWAY_SIGN_TIANJIN_BMT_2_ODD, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_3_odd", BlockList.RAILWAY_SIGN_TIANJIN_BMT_3_ODD, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_4_odd", BlockList.RAILWAY_SIGN_TIANJIN_BMT_4_ODD, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_5_odd", BlockList.RAILWAY_SIGN_TIANJIN_BMT_5_ODD, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_6_odd", BlockList.RAILWAY_SIGN_TIANJIN_BMT_6_ODD, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_7_odd", BlockList.RAILWAY_SIGN_TIANJIN_BMT_7_ODD, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_2_even", BlockList.RAILWAY_SIGN_TIANJIN_BMT_2_EVEN, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_3_even", BlockList.RAILWAY_SIGN_TIANJIN_BMT_3_EVEN, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_4_even", BlockList.RAILWAY_SIGN_TIANJIN_BMT_4_EVEN, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_5_even", BlockList.RAILWAY_SIGN_TIANJIN_BMT_5_EVEN, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_6_even", BlockList.RAILWAY_SIGN_TIANJIN_BMT_6_EVEN, SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_bmt_7_even", BlockList.RAILWAY_SIGN_TIANJIN_BMT_7_EVEN, SIGNS);
        registerBlockItem.accept("station_navigator_3", BlockList.STATION_NAVIGATOR_3, SIGNS);
        registerBlockItem.accept("station_navigator_4", BlockList.STATION_NAVIGATOR_4, SIGNS);
        registerBlockItem.accept("station_navigator_5", BlockList.STATION_NAVIGATOR_5, SIGNS);
        /* PLATFORM GATES - grouped by line style (TRT/default, BMT, Jinjing) */
        /* APG (half-height platform doors) */
        /* APG - TRT/default */
        registerBlock.accept("apg_door_tianjin", BlockList.APG_DOOR_TIANJIN);
        registerItem.accept("apg_door_tianjin", ItemList.APG_DOOR_TIANJIN, GATES);
        registerBlock.accept("apg_door_tianjin_trt", BlockList.APG_DOOR_TIANJIN_TRT);
        registerItem.accept("apg_door_tianjin_trt", ItemList.APG_DOOR_TIANJIN_TRT, GATES);
        registerBlock.accept("apg_glass_tianjin", BlockList.APG_GLASS_TIANJIN);
        registerItem.accept("apg_glass_tianjin", ItemList.APG_GLASS_TIANJIN, GATES);
        registerBlock.accept("apg_glass_tianjin_trt", BlockList.APG_GLASS_TIANJIN_TRT);
        registerItem.accept("apg_glass_tianjin_trt", ItemList.APG_GLASS_TIANJIN_TRT, GATES);
        registerBlock.accept("apg_glass_end_tianjin", BlockList.APG_GLASS_END_TIANJIN);
        registerItem.accept("apg_glass_end_tianjin", ItemList.APG_GLASS_END_TIANJIN, GATES);
        registerBlock.accept("apg_glass_end_tianjin_trt", BlockList.APG_GLASS_END_TIANJIN_TRT);
        registerItem.accept("apg_glass_end_tianjin_trt", ItemList.APG_GLASS_END_TIANJIN_TRT, GATES);
        /* APG - Single doors */
        registerBlock.accept("apg_door_single_left", BlockList.APG_DOOR_SINGLE_LEFT);
        registerItem.accept("apg_door_single_left", ItemList.APG_DOOR_SINGLE_LEFT, GATES);
        registerBlock.accept("apg_door_single_right", BlockList.APG_DOOR_SINGLE_RIGHT);
        registerItem.accept("apg_door_single_right", ItemList.APG_DOOR_SINGLE_RIGHT, GATES);
        /* APG - BMT */
        registerBlock.accept("apg_door_tianjin_bmt", BlockList.APG_DOOR_TIANJIN_BMT);
        registerItem.accept("apg_door_tianjin_bmt", ItemList.APG_DOOR_TIANJIN_BMT, GATES);
        registerBlock.accept("apg_glass_tianjin_bmt", BlockList.APG_GLASS_TIANJIN_BMT);
        registerItem.accept("apg_glass_tianjin_bmt", ItemList.APG_GLASS_TIANJIN_BMT, GATES);
        registerBlock.accept("apg_glass_end_tianjin_bmt", BlockList.APG_GLASS_END_TIANJIN_BMT);
        registerItem.accept("apg_glass_end_tianjin_bmt", ItemList.APG_GLASS_END_TIANJIN_BMT, GATES);
        /* APG - Jinjing */
        registerBlock.accept("apg_door_tianjin_jinjing", BlockList.APG_DOOR_TIANJIN_JINJING);
        registerItem.accept("apg_door_tianjin_jinjing", ItemList.APG_DOOR_TIANJIN_JINJING, GATES);
        registerBlock.accept("apg_glass_tianjin_jinjing", BlockList.APG_GLASS_TIANJIN_JINJING);
        registerItem.accept("apg_glass_tianjin_jinjing", ItemList.APG_GLASS_TIANJIN_JINJING, GATES);
        registerBlock.accept("apg_glass_end_tianjin_jinjing", BlockList.APG_GLASS_END_TIANJIN_JINJING);
        registerItem.accept("apg_glass_end_tianjin_jinjing", ItemList.APG_GLASS_END_TIANJIN_JINJING, GATES);

        /* PSD (full-height platform doors) */
        /* PSD - TRT/default */
        registerBlock.accept("psd_door_tianjin", BlockList.PSD_DOOR_TIANJIN);
        registerItem.accept("psd_door_tianjin", ItemList.PSD_DOOR_TIANJIN, GATES);
        registerBlock.accept("psd_glass_tianjin", BlockList.PSD_GLASS_TIANJIN);
        registerItem.accept("psd_glass_tianjin", ItemList.PSD_GLASS_TIANJIN, GATES);
        registerBlock.accept("psd_glass_end_tianjin", BlockList.PSD_GLASS_END_TIANJIN);
        registerItem.accept("psd_glass_end_tianjin", ItemList.PSD_GLASS_END_TIANJIN, GATES);
        registerBlock.accept("psd_top_tianjin", BlockList.PSD_TOP_TIANJIN);
        /* PSD - Single doors */
        registerBlock.accept("psd_door_single_left", BlockList.PSD_DOOR_SINGLE_LEFT);
        registerItem.accept("psd_door_single_left", ItemList.PSD_DOOR_SINGLE_LEFT, GATES);
        registerBlock.accept("psd_door_single_right", BlockList.PSD_DOOR_SINGLE_RIGHT);
        registerItem.accept("psd_door_single_right", ItemList.PSD_DOOR_SINGLE_RIGHT, GATES);
        /* PSD - BMT */
        registerBlock.accept("psd_door_tianjin_bmt", BlockList.PSD_DOOR_TIANJIN_BMT);
        registerItem.accept("psd_door_tianjin_bmt", ItemList.PSD_DOOR_TIANJIN_BMT, GATES);
        registerBlock.accept("psd_glass_tianjin_bmt", BlockList.PSD_GLASS_TIANJIN_BMT);
        registerItem.accept("psd_glass_tianjin_bmt", ItemList.PSD_GLASS_TIANJIN_BMT, GATES);
        registerBlock.accept("psd_glass_end_tianjin_bmt", BlockList.PSD_GLASS_END_TIANJIN_BMT);
        registerItem.accept("psd_glass_end_tianjin_bmt", ItemList.PSD_GLASS_END_TIANJIN_BMT, GATES);
        registerBlock.accept("psd_top_tianjin_bmt", BlockList.PSD_TOP_TIANJIN_BMT);
        /* PSD - Jinjing */
        registerBlock.accept("psd_door_tianjin_jinjing", BlockList.PSD_DOOR_TIANJIN_JINJING);
        registerItem.accept("psd_door_tianjin_jinjing", ItemList.PSD_DOOR_TIANJIN_JINJING, GATES);
        registerBlock.accept("psd_glass_tianjin_jinjing", BlockList.PSD_GLASS_TIANJIN_JINJING);
        registerItem.accept("psd_glass_tianjin_jinjing", ItemList.PSD_GLASS_TIANJIN_JINJING, GATES);
        registerBlock.accept("psd_glass_screen_tianjin", BlockList.PSD_GLASS_SCREEN_TIANJIN);
        registerItem.accept("psd_glass_screen_tianjin", ItemList.PSD_GLASS_SCREEN_TIANJIN, GATES);
        registerBlock.accept("psd_glass_end_tianjin_jinjing", BlockList.PSD_GLASS_END_TIANJIN_JINJING);
        registerItem.accept("psd_glass_end_tianjin_jinjing", ItemList.PSD_GLASS_END_TIANJIN_JINJING, GATES);
        registerBlock.accept("psd_top_tianjin_jinjing", BlockList.PSD_TOP_TIANJIN_JINJING);

        /* 1.1.6 - Ported from 4.0 - Station Name Projector */
        registerBlockItem.accept("station_name_projector", BlockList.STATION_NAME_PROJECTOR, SIGNS);
        /* 1.1.7 - Ported from 4.0 - PIDS (Passenger Information Display System) */
        registerBlockItem.accept("pids_tianjin", BlockList.PIDS_TIANJIN, DECORATION);
        registerBlockItem.accept("pids_tianjin_single", BlockList.PIDS_TIANJIN_SINGLE, DECORATION);
        /* DECORATION - logo, furniture, signs, barriers, misc */
        registerBlockItem.accept("logo", BlockList.LOGO, DECORATION);
        registerBlockItem.accept("bench", BlockList.BENCH, DECORATION);
        registerBlockItem.accept("time_display", BlockList.TIME_DISPLAY, DECORATION);
        registerBlockItem.accept("time_display_even", BlockList.TIME_DISPLAY_EVEN, DECORATION);
        registerBlockItem.accept("smoke_alarm", BlockList.SMOKE_ALARM, DECORATION);
        registerBlockItem.accept("apg_corner", BlockList.APG_CORNER, DECORATION);
        registerBlockItem.accept("emergency_exit_sign", BlockList.EMERGENCY_EXIT_SIGN, DECORATION);
        registerBlockItem.accept("service_corridor_sign", BlockList.SERVICE_CORRIDOR_SIGN, DECORATION);
        /* 1.1.4 - Ported from 4.0 */
        registerBlockItem.accept("ticket_barrier_tianjin_entrance", BlockList.TICKET_BARRIER_TIANJIN_ENTRANCE, DECORATION);
        registerBlockItem.accept("ticket_barrier_tianjin_exit", BlockList.TICKET_BARRIER_TIANJIN_EXIT, DECORATION);
        /* 1.1.3 - Ported from 1.1.3 - Ticket barrier for outbound transfer */
        registerBlockItem.accept("ticket_barrier_outbound_transfer_exit", BlockList.TICKET_BARRIER_OUTBOUND_TRANSFER_EXIT, DECORATION);
        registerBlockItem.accept("ticket_barrier_outbound_transfer_tianjin", BlockList.TICKET_BARRIER_OUTBOUND_TRANSFER_TIANJIN, DECORATION);
//        registerEnchantedBlockItem.accept("decoration_light", BlockList.DECORATION_LIGHT, DECORATION);
        /* 1.1.0 - Ported from 4.0 */
        registerBlockItem.accept("station_navigator_pole", BlockList.STATION_NAVIGATOR_POLE, DECORATION);
        /* 1.1.1 - Ported from 4.0 */
        registerBlockItem.accept("trash_can", BlockList.TRASH_CAN, DECORATION);
        /* CEILINGS */
        registerBlockItem.accept("ceiling_not_lit", BlockList.CEILING_NOT_LIT, CEILINGS);
        registerEnchantedBlockItem.accept("ceiling", BlockList.STATION_COLOR_CEILING, CEILINGS);
        registerEnchantedBlockItem.accept("ceiling_light", BlockList.STATION_COLOR_CEILING_LIGHT, CEILINGS);
        registerEnchantedBlockItem.accept("ceiling_no_light", BlockList.STATION_COLOR_CEILING_NO_LIGHT, CEILINGS);
        registerEnchantedBlockItem.accept("station_color_ceiling_not_lit", BlockList.STATION_COLOR_CEILING_NOT_LIT, CEILINGS);
        /* 1.1.1 - Ported from 4.0 */
        registerBlockItem.accept("ceiling_tianjin", BlockList.CEILING_TIANJIN, CEILINGS);
        registerBlockItem.accept("ceiling_tianjin_light", BlockList.CEILING_TIANJIN_LIGHT, CEILINGS);
        registerBlockItem.accept("ceiling_tianjin_no_light", BlockList.CEILING_TIANJIN_NO_LIGHT, CEILINGS);
        registerBlockItem.accept("ceiling_tianjin_not_lit", BlockList.CEILING_TIANJIN_NOT_LIT, CEILINGS);
        /* RAILWAYS */
        registerBlockItem.accept("railway_sign_wall_4", BlockList.RAILWAY_SIGN_WALL_4, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_6", BlockList.RAILWAY_SIGN_WALL_6, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_8", BlockList.RAILWAY_SIGN_WALL_8, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_10", BlockList.RAILWAY_SIGN_WALL_10, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_double_4", BlockList.RAILWAY_SIGN_WALL_DOUBLE_4, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_double_6", BlockList.RAILWAY_SIGN_WALL_DOUBLE_6, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_double_8", BlockList.RAILWAY_SIGN_WALL_DOUBLE_8, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_double_10", BlockList.RAILWAY_SIGN_WALL_DOUBLE_10, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_2", BlockList.RAILWAY_SIGN_WALL_BIG_2, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_3", BlockList.RAILWAY_SIGN_WALL_BIG_3, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_4", BlockList.RAILWAY_SIGN_WALL_BIG_4, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_5", BlockList.RAILWAY_SIGN_WALL_BIG_5, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_6", BlockList.RAILWAY_SIGN_WALL_BIG_6, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_7", BlockList.RAILWAY_SIGN_WALL_BIG_7, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_8", BlockList.RAILWAY_SIGN_WALL_BIG_8, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_9", BlockList.RAILWAY_SIGN_WALL_BIG_9, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_wall_big_10", BlockList.RAILWAY_SIGN_WALL_BIG_10, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_3_odd", BlockList.RAILWAY_SIGN_TIANJIN_3_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_4_odd", BlockList.RAILWAY_SIGN_TIANJIN_4_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_5_odd", BlockList.RAILWAY_SIGN_TIANJIN_5_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_6_odd", BlockList.RAILWAY_SIGN_TIANJIN_6_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_7_odd", BlockList.RAILWAY_SIGN_TIANJIN_7_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_2_even", BlockList.RAILWAY_SIGN_TIANJIN_2_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_3_even", BlockList.RAILWAY_SIGN_TIANJIN_3_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_4_even", BlockList.RAILWAY_SIGN_TIANJIN_4_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_5_even", BlockList.RAILWAY_SIGN_TIANJIN_5_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_6_even", BlockList.RAILWAY_SIGN_TIANJIN_6_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_7_even", BlockList.RAILWAY_SIGN_TIANJIN_7_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_8_odd", BlockList.RAILWAY_SIGN_TIANJIN_8_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_9_odd", BlockList.RAILWAY_SIGN_TIANJIN_9_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_10_odd", BlockList.RAILWAY_SIGN_TIANJIN_10_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_11_odd", BlockList.RAILWAY_SIGN_TIANJIN_11_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_12_odd", BlockList.RAILWAY_SIGN_TIANJIN_12_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_13_odd", BlockList.RAILWAY_SIGN_TIANJIN_13_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_8_even", BlockList.RAILWAY_SIGN_TIANJIN_8_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_9_even", BlockList.RAILWAY_SIGN_TIANJIN_9_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_10_even", BlockList.RAILWAY_SIGN_TIANJIN_10_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_11_even", BlockList.RAILWAY_SIGN_TIANJIN_11_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_12_even", BlockList.RAILWAY_SIGN_TIANJIN_12_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_13_even", BlockList.RAILWAY_SIGN_TIANJIN_13_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_3_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_3_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_4_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_4_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_5_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_5_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_6_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_6_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_7_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_7_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_2_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_2_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_3_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_3_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_4_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_4_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_5_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_5_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_6_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_6_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_7_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_7_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_8_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_8_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_9_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_9_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_10_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_10_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_11_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_11_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_12_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_12_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_13_odd", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_13_ODD, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_8_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_8_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_9_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_9_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_10_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_10_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_11_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_11_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_12_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_12_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_double_13_even", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_13_EVEN, RAILWAY_SIGNS);
        registerBlockItem.accept("railway_sign_tianjin_pole", BlockList.RAILWAY_SIGN_TIANJIN_POLE, RAILWAY_SIGNS);

        registerBlock.accept("railway_sign_wall_middle", BlockList.RAILWAY_SIGN_WALL_MIDDLE);
        registerBlock.accept("railway_sign_wall_big_middle", BlockList.RAILWAY_SIGN_WALL_BIG_MIDDLE);
        registerBlock.accept("railway_sign_wall_double_middle", BlockList.RAILWAY_SIGN_WALL_DOUBLE_MIDDLE);
        registerBlock.accept("railway_sign_tianjin_middle", BlockList.RAILWAY_SIGN_TIANJIN_MIDDLE);
        registerBlock.accept("railway_sign_tianjin_double_middle", BlockList.RAILWAY_SIGN_TIANJIN_DOUBLE_MIDDLE);
        /* 1.1.1 - Ported from 4.0 */
        registerBlock.accept("railway_sign_tianjin_bmt_middle", BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE);
        registerBlock.accept("station_navigator_middle", BlockList.STATION_NAVIGATOR_MIDDLE);


        registerBlockEntityType.accept("station_name_sign_2", BlockEntityTypes.STATION_NAME_SIGN_ENTITY_2);
        registerBlockEntityType.accept("station_name_sign_1", BlockEntityTypes.STATION_NAME_SIGN_ENTITY_1);
        registerBlockEntityType.accept("roadblock_sign", BlockEntityTypes.ROADBLOCK_SIGN_ENTITY);
        registerBlockEntityType.accept("player_detector", BlockEntityTypes.PLAYER_DETECTOR_TILE_ENTITY);
        registerBlockEntityType.accept("custom_color_concrete", BlockEntityTypes.STATION_COLOR_CONCRETE_TILE_ENTITY);
        registerBlockEntityType.accept("custom_color_concrete_stairs", BlockEntityTypes.STATION_COLOR_CONCRETE_STAIRS_TILE_ENTITY);
        registerBlockEntityType.accept("custom_color_concrete_slab", BlockEntityTypes.STATION_COLOR_CONCRETE_SLAB_TILE_ENTITY);
        registerBlockEntityType.accept("station_name_wall_legacy", BlockEntityTypes.STATION_NAME_WALL_LEGACY_TILE_ENTITY);
//        registerBlockEntityType.accept("decoration_light", BlockEntityTypes.DECORATION_LIGHT_TILE_ENTITY);
        registerBlockEntityType.accept("time_display", BlockEntityTypes.TIME_DISPLAY_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_4", BlockEntityTypes.RAILWAY_SIGN_WALL_4_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_6", BlockEntityTypes.RAILWAY_SIGN_WALL_6_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_8", BlockEntityTypes.RAILWAY_SIGN_WALL_8_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_10", BlockEntityTypes.RAILWAY_SIGN_WALL_10_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_double_4", BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_4_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_double_6", BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_6_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_double_8", BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_8_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_double_10", BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_10_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_2", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_2_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_3", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_3_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_4", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_4_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_5", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_5_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_6", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_6_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_7", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_7_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_8", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_8_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_9", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_9_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_wall_big_10", BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_10_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_3_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_3_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_4_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_4_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_5_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_5_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_6_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_6_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_7_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_7_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_2_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_2_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_3_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_3_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_4_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_4_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_5_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_5_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_6_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_6_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_7_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_7_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_8_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_8_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_9_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_9_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_10_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_10_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_11_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_11_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_12_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_12_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_13_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_13_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_8_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_8_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_9_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_9_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_10_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_10_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_11_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_11_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_12_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_12_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_13_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_13_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_3_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_3_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_4_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_4_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_5_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_5_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_6_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_6_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_7_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_7_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_2_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_2_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_3_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_3_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_4_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_4_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_5_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_5_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_6_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_6_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_7_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_7_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_8_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_8_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_9_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_9_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_10_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_10_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_11_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_11_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_12_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_12_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_13_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_13_ODD_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_8_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_8_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_9_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_9_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_10_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_10_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_11_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_11_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_12_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_12_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("railway_sign_tianjin_double_13_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_13_EVEN_TILE_ENTITY);
        registerBlockEntityType.accept("metal_detection_door", BlockEntityTypes.METAL_DETECTION_DOOR_TILE_ENTITY);
//        registerBlockEntityType.accept("bench", BlockEntityTypes.BENCH_TILE_ENTITY);
        registerBlockEntityType.accept("service_corridor_sign", BlockEntityTypes.SERVICE_CORRIDOR_SIGN_TILE_ENTITY);
        registerBlockEntityType.accept("station_name_entrance_tianjin", BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_TILE_ENTITY);
        registerBlockEntityType.accept("station_name_entrance_tianjin_pinyin", BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_PINYIN_TILE_ENTITY);
        registerBlockEntityType.accept("station_name_entrance_tianjin_bmt", BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_BMT_TILE_ENTITY);
        registerBlockEntityType.accept("station_name_entrance_tianjin_bmt_pinyin", BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_BMT_PINYIN_TILE_ENTITY);
        registerBlockEntityType.accept("station_name_entrance_tianjin_jinjing", BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_JINJING_TILE_ENTITY);
        registerBlockEntityType.accept("station_name_entrance_tianjin_jinjing_pinyin", BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_JINJING_PINYIN_TILE_ENTITY);
        /* 1.1.0 - Ported from 4.0 */
        registerBlockEntityType.accept("custom_color_mosaic_tile", BlockEntityTypes.CUSTOM_COLOR_MOSAIC_TILE_TILE_ENTITY);
        registerBlockEntityType.accept("custom_color_mosaic_tile_stairs", BlockEntityTypes.CUSTOM_COLOR_MOSAIC_TILE_STAIRS_TILE_ENTITY);
        registerBlockEntityType.accept("custom_color_mosaic_tile_slab", BlockEntityTypes.CUSTOM_COLOR_MOSAIC_TILE_SLAB_TILE_ENTITY);
        /* 1.1.1 - Ported from 4.0 */
        registerBlockEntityType.accept("metal_pole_bmt", BlockEntityTypes.METAL_POLE_BMT);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_2_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_2_ODD);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_3_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_3_ODD);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_4_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_4_ODD);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_5_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_5_ODD);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_6_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_6_ODD);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_7_odd", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_7_ODD);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_2_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_2_EVEN);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_3_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_3_EVEN);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_4_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_4_EVEN);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_5_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_5_EVEN);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_6_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_6_EVEN);
        registerBlockEntityType.accept("railway_sign_tianjin_bmt_7_even", BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_7_EVEN);
        registerBlockEntityType.accept("station_navigator_3", BlockEntityTypes.STATION_NAVIGATOR_3);
        registerBlockEntityType.accept("station_navigator_4", BlockEntityTypes.STATION_NAVIGATOR_4);
        registerBlockEntityType.accept("station_navigator_5", BlockEntityTypes.STATION_NAVIGATOR_5);
        /* 1.1.6 - Ported from 4.0 - APG (half-height platform doors) */
        registerBlockEntityType.accept("apg_door_tianjin", BlockEntityTypes.APG_DOOR_TIANJIN_TILE_ENTITY);
        registerBlockEntityType.accept("apg_door_tianjin_trt", BlockEntityTypes.APG_DOOR_TIANJIN_TRT_TILE_ENTITY);
        registerBlockEntityType.accept("apg_door_tianjin_bmt", BlockEntityTypes.APG_DOOR_TIANJIN_BMT_TILE_ENTITY);
        registerBlockEntityType.accept("apg_door_tianjin_jinjing", BlockEntityTypes.APG_DOOR_TIANJIN_JINJING_TILE_ENTITY);
        registerBlockEntityType.accept("apg_door_single_left", BlockEntityTypes.APG_DOOR_SINGLE_LEFT_TILE_ENTITY);
        registerBlockEntityType.accept("apg_door_single_right", BlockEntityTypes.APG_DOOR_SINGLE_RIGHT_TILE_ENTITY);
        // The default APG glass panel (BlockAPGGlassTianjin) has no tile entity (matches MTR 4.0).
        registerBlockEntityType.accept("apg_glass_tianjin_trt", BlockEntityTypes.APG_GLASS_TIANJIN_TRT_TILE_ENTITY);
        registerBlockEntityType.accept("apg_glass_tianjin_bmt", BlockEntityTypes.APG_GLASS_TIANJIN_BMT_TILE_ENTITY);
        registerBlockEntityType.accept("apg_glass_tianjin_jinjing", BlockEntityTypes.APG_GLASS_TIANJIN_JINJING_TILE_ENTITY);
        /* 1.1.6 - Ported from 4.0 - PSD (full-height platform doors) */
        registerBlockEntityType.accept("psd_door_tianjin", BlockEntityTypes.PSD_DOOR_TIANJIN_TILE_ENTITY);
        registerBlockEntityType.accept("psd_door_tianjin_bmt", BlockEntityTypes.PSD_DOOR_TIANJIN_BMT_TILE_ENTITY);
        registerBlockEntityType.accept("psd_door_tianjin_jinjing", BlockEntityTypes.PSD_DOOR_TIANJIN_JINJING_TILE_ENTITY);
        registerBlockEntityType.accept("psd_door_single_left", BlockEntityTypes.PSD_DOOR_SINGLE_LEFT_TILE_ENTITY);
        registerBlockEntityType.accept("psd_door_single_right", BlockEntityTypes.PSD_DOOR_SINGLE_RIGHT_TILE_ENTITY);
        registerBlockEntityType.accept("psd_top_tianjin", BlockEntityTypes.PSD_TOP_TIANJIN_TILE_ENTITY);
        registerBlockEntityType.accept("psd_top_tianjin_jinjing", BlockEntityTypes.PSD_TOP_TIANJIN_JINJING_TILE_ENTITY);
        registerBlockEntityType.accept("psd_top_tianjin_bmt", BlockEntityTypes.PSD_TOP_TIANJIN_BMT_TILE_ENTITY);
        /* 1.1.6 - Ported from 4.0 - Station Name Projector */
        registerBlockEntityType.accept("station_name_projector", BlockEntityTypes.STATION_NAME_PROJECTOR);

        /* 1.1.7 - Ported from 4.0 - PIDS (Passenger Information Display System) */
        registerBlockEntityType.accept("pids_tianjin", BlockEntityTypes.PIDS_TIANJIN_TILE_ENTITY);
        registerBlockEntityType.accept("psd_glass_screen_tianjin", BlockEntityTypes.PSD_GLASS_SCREEN_TIANJIN_TILE_ENTITY);
        registerBlockEntityType.accept("pids_tianjin_single", BlockEntityTypes.PIDS_TIANJIN_SINGLE_TILE_ENTITY);

        /* 1.1.8 - Ported from 4.0 - Smoke alarm & Time display (even) */
        registerBlockEntityType.accept("smoke_alarm", BlockEntityTypes.SMOKE_ALARM_TILE_ENTITY);
        registerBlockEntityType.accept("time_display_even", BlockEntityTypes.TIME_DISPLAY_EVEN_TILE_ENTITY);
        /* 1.1.8 - Ported from 4.0 - Station name plate */
        registerBlockEntityType.accept("station_name_plate", BlockEntityTypes.STATION_NAME_PLATE_TILE_ENTITY);
        /* 1.1.8 - Ported from 4.0 - Tianjin station name sign */
        registerBlockEntityType.accept("station_name_sign_tianjin", BlockEntityTypes.STATION_NAME_SIGN_TIANJIN);
        /* 1.1.8 - Ported from 4.0 - BMT route map */
        registerBlockEntityType.accept("route_map_bmt", BlockEntityTypes.ROUTE_MAP_BMT);
        /* 1.1.9 - Wall-mounted BMT route map */
        registerBlockEntityType.accept("route_map_bmt_wall", BlockEntityTypes.ROUTE_MAP_BMT_WALL);
        /* 1.1.3 - Ported from 1.1.3 - Single-block railway sign */
        registerBlockEntityType.accept("railway_sign_single", BlockEntityTypes.RAILWAY_SIGN_SINGLE);
        /* 1.1.8 - Ported from 4.0 - Security check sign */
        registerBlockEntityType.accept("security_check_sign", BlockEntityTypes.SECURITY_CHECK_SIGN_TILE_ENTITY);

        /* 1.1.3.1 - Bench seat entity (NeoMTR removed mtr.entity.EntitySeat) */
        registerEntityType.accept("seat", EntityTypes.SEAT);

        mtr.Registry.registerNetworkReceiver(PACKET_UPDATE_CUSTOM_CONTENT, PacketGuiServer::receiveCustomContentC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_UPDATE_CUSTOM_COLOR, PacketGuiServer::receiveCustomColorC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_SIGN_TYPES, PacketGuiServer::receiveSignIdsC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_SIGN_TYPES_DOUBLE, PacketGuiServer::receiveSignIdsDoubleC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_UPDATE_PIDS_TIANJIN_CONFIG, PacketGuiServer::receivePIDSTianjinConfigC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_UPDATE_PIDS_ADS_CONFIG, PacketGuiServer::receivePIDSAdsConfigC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_STATION_NAVIGATOR_CONFIG, PacketGuiServer::receiveStationNavigatorConfigC2S);
        mtr.Registry.registerNetworkReceiver(PACKET_RAILWAY_SIGN_SINGLE_CONFIG, PacketGuiServer::receiveRailwaySignSingleConfigC2S);
    }

    @FunctionalInterface
    public interface RegisterBlockItem
    {
        void accept(String string, RegistryObject<Block> block, Object filter);
    }

    @FunctionalInterface
    public interface RegisterItem
    {
        void accept(String string, RegistryObject<Item> item, Object filter);
    }
}
