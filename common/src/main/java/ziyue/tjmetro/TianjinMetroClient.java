package ziyue.tjmetro;

import mtr.RegistryClient;
import mtr.render.RenderAPGGlass;
import mtr.render.RenderPSDTop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import ziyue.tjmetro.block.BlockSmokeAlarm;
import ziyue.tjmetro.packet.IPacket;
import ziyue.tjmetro.packet.PacketGuiClient;
import ziyue.tjmetro.render.*;
import ziyue.tjmetro.sound.LoopingSoundInstance;

/**
 * Client Entry of <b>Tianjin Metro Mod</b>.
 *
 * @see TianjinMetro
 * @since beta-1
 */

public class TianjinMetroClient
{
    public static final LoopingSoundInstance SMOKE_ALARM_SOUND_INSTANCE = new LoopingSoundInstance("smoke_alarm");

    public static void init() {
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.LOGO.get());


        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.STATION_NAME_SIGN_1.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.STATION_NAME_SIGN_2.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_1.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_1_INDENTED.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_1_SLAB.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_LINE_11_1.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_LINE_11_1_INDENTED.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_LINE_11_1_SLAB.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_2.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_2_INDENTED.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_2_SLAB.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_LINE_11_2.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_LINE_11_2_INDENTED.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PLATFORM_TJ_LINE_11_2_SLAB.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_CORNER.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.HIGH_SPEED_REPEATER.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.EMERGENCY_EXIT_SIGN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.SERVICE_CORRIDOR_SIGN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.METAL_DETECTION_DOOR.get());
        RegistryClient.registerBlockRenderType(RenderType.translucent(), BlockList.ROLLING.get());
        /* 1.1.7 - Ported from 4.0 - PIDS */
        RegistryClient.registerBlockRenderType(RenderType.translucent(), BlockList.PIDS_TIANJIN.get());
        /* 1.1.0 - Ported from 4.0 */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.STATION_SIGN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.STATION_SIGN_BMT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.NO_ENTERING_SIGN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.STATION_NAVIGATOR_POLE.get());
        /* 1.1.1 - Ported from 4.0 */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.CEILING_TIANJIN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.CEILING_TIANJIN_LIGHT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.CEILING_TIANJIN_NO_LIGHT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.CEILING_TIANJIN_NOT_LIT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.TRASH_CAN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.METAL_POLE_BMT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.TICKET_BARRIER_TIANJIN_ENTRANCE.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.TICKET_BARRIER_TIANJIN_EXIT.get());
        /* 1.1.3 - Ported from 1.1.3 - Ticket barrier for outbound transfer */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.TICKET_BARRIER_OUTBOUND_TRANSFER_EXIT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.TICKET_BARRIER_OUTBOUND_TRANSFER_TIANJIN.get());
        /* 1.1.6 - Ported from 4.0 - APG (half-height platform doors) */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_DOOR_TIANJIN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_DOOR_TIANJIN_TRT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_DOOR_TIANJIN_BMT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_DOOR_TIANJIN_JINJING.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_DOOR_SINGLE_LEFT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_DOOR_SINGLE_RIGHT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_GLASS_TIANJIN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_GLASS_TIANJIN_TRT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_GLASS_TIANJIN_BMT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_GLASS_TIANJIN_JINJING.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_GLASS_END_TIANJIN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_GLASS_END_TIANJIN_TRT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_GLASS_END_TIANJIN_BMT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.APG_GLASS_END_TIANJIN_JINJING.get());
        /* 1.1.6 - Ported from 4.0 - PSD (full-height platform doors) */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_DOOR_TIANJIN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_DOOR_TIANJIN_BMT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_DOOR_TIANJIN_JINJING.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_DOOR_SINGLE_LEFT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_DOOR_SINGLE_RIGHT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_GLASS_TIANJIN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_GLASS_TIANJIN_BMT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_GLASS_TIANJIN_JINJING.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_GLASS_END_TIANJIN.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_GLASS_END_TIANJIN_BMT.get());
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.PSD_GLASS_END_TIANJIN_JINJING.get());

        RegistryClient.registerBlockColors(BlockList.STATION_COLOR_CEILING.get());
        RegistryClient.registerBlockColors(BlockList.STATION_COLOR_CEILING_LIGHT.get());
        RegistryClient.registerBlockColors(BlockList.STATION_COLOR_CEILING_NO_LIGHT.get());
        RegistryClient.registerBlockColors(BlockList.STATION_NAME_SIGN_1.get());
        RegistryClient.registerBlockColors(BlockList.STATION_COLOR_CEILING_NOT_LIT.get());

        Registry.registerCustomColorBlock(BlockList.CUSTOM_COLOR_CONCRETE.get());
        Registry.registerCustomColorBlock(BlockList.CUSTOM_COLOR_CONCRETE_STAIRS.get());
        Registry.registerCustomColorBlock(BlockList.CUSTOM_COLOR_CONCRETE_SLAB.get());
        /* 1.1.0 - Ported from 4.0 */
        Registry.registerCustomColorBlock(BlockList.CUSTOM_COLOR_MOSAIC_TILE.get());
        Registry.registerCustomColorBlock(BlockList.CUSTOM_COLOR_MOSAIC_TILE_STAIRS.get());
        Registry.registerCustomColorBlock(BlockList.CUSTOM_COLOR_MOSAIC_TILE_SLAB.get());
        /* 1.1.4 - Fixed color mosaic tiles */
        Registry.registerFixedColorBlock(BlockList.MOSAIC_TILE_WHITE.get(), 0xFFFFFF);
        Registry.registerFixedColorBlock(BlockList.MOSAIC_TILE_GRAY.get(), 0xC8C8C8);
        /* 1.1.1 - Ported from 4.0 */
        Registry.registerCustomColorBlock(BlockList.METAL_POLE_BMT.get());

        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_SIGN_ENTITY_1.get(), RenderStationNameSign::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_SIGN_ENTITY_2.get(), RenderStationNameSign::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.ROADBLOCK_SIGN_ENTITY.get(), RenderRoadblockSign::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_WALL_LEGACY_TILE_ENTITY.get(), RenderStationNameWallLegacy::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.TIME_DISPLAY_TILE_ENTITY.get(), RenderTimeDisplay::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_4_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_6_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_8_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_10_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_4_TILE_ENTITY.get(), RenderRailwaySignWallDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_6_TILE_ENTITY.get(), RenderRailwaySignWallDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_8_TILE_ENTITY.get(), RenderRailwaySignWallDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_10_TILE_ENTITY.get(), RenderRailwaySignWallDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_2_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_3_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_4_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_5_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_6_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_7_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_8_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_9_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_WALL_BIG_10_TILE_ENTITY.get(), RenderRailwaySignWall::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_2_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_3_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_4_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_5_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_6_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_7_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_3_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_4_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_5_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_6_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_7_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_8_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_9_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_10_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_11_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_12_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_13_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_8_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_9_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_10_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_11_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_12_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_13_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_2_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_3_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_4_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_5_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_6_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_7_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_3_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_4_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_5_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_6_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_7_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_8_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_9_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_10_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_11_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_12_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_13_EVEN_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_8_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_9_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_10_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_11_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_12_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_DOUBLE_13_ODD_TILE_ENTITY.get(), RenderRailwaySignTianjinDouble::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.SERVICE_CORRIDOR_SIGN_TILE_ENTITY.get(), RenderServiceCorridorSign::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_TILE_ENTITY.get(), RenderStationNameEntranceTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_PINYIN_TILE_ENTITY.get(), RenderStationNameEntranceTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_BMT_TILE_ENTITY.get(), RenderStationNameEntranceTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_BMT_PINYIN_TILE_ENTITY.get(), RenderStationNameEntranceTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_JINJING_TILE_ENTITY.get(), RenderStationNameEntranceTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_JINJING_PINYIN_TILE_ENTITY.get(), RenderStationNameEntranceTianjin::new);

        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_PLATE_TILE_ENTITY.get(), RenderStationNamePlate::new);
        /* 1.1.8 - Ported from 4.0 - Tianjin station name sign */
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_SIGN_TIANJIN.get(), RenderStationNameSignTianjin::new);
        /* 1.1.8 - Ported from 4.0 - BMT route map */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.ROUTE_MAP_BMT.get());
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.ROUTE_MAP_BMT.get(), RenderRouteMapBMT::new);
        /* 1.1.9 - Wall-mounted BMT route map */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.ROUTE_MAP_BMT_WALL.get());
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.ROUTE_MAP_BMT_WALL.get(), RenderRouteMapBMTWall::new);
        /* 1.1.3 - Ported from 1.1.3 - Single-block railway sign */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.RAILWAY_SIGN_SINGLE.get());
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_SINGLE.get(), RenderRailwaySignSingle::new);
        /* 1.1.8 - Ported from 4.0 - Security check sign */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.SECURITY_CHECK_SIGN.get());
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.SECURITY_CHECK_SIGN_TILE_ENTITY.get(), RenderSecurityCheckSign::new);
        /* 1.1.1 - Ported from 4.0 */
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_2_ODD.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_3_ODD.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_4_ODD.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_5_ODD.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_6_ODD.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_2_EVEN.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_3_EVEN.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_4_EVEN.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_5_EVEN.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_6_EVEN.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_7_ODD.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_7_EVEN.get(), RenderRailwaySignBMTBase::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAVIGATOR_3.get(), RenderStationNavigator::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAVIGATOR_4.get(), RenderStationNavigator::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAVIGATOR_5.get(), RenderStationNavigator::new);
        /* 1.1.6 - Ported from 4.0 - APG doors (custom Tianjin renderer) */
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_DOOR_TIANJIN_TILE_ENTITY.get(), RenderAPGDoorTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_DOOR_TIANJIN_TRT_TILE_ENTITY.get(), RenderAPGDoorTianjinTRT::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_DOOR_TIANJIN_BMT_TILE_ENTITY.get(), RenderAPGDoorTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_DOOR_TIANJIN_JINJING_TILE_ENTITY.get(), RenderAPGDoorTianjinJinjing::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_DOOR_SINGLE_LEFT_TILE_ENTITY.get(), RenderAPGDoorTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_DOOR_SINGLE_RIGHT_TILE_ENTITY.get(), RenderAPGDoorTianjin::new);
        /* 1.1.6 - Ported from 4.0 - APG glass (custom Tianjin renderers) */
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_GLASS_TIANJIN_TRT_TILE_ENTITY.get(), RenderAPGGlassTianjinTRT::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_GLASS_TIANJIN_BMT_TILE_ENTITY.get(), RenderAPGGlassTianjinBMT::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.APG_GLASS_TIANJIN_JINJING_TILE_ENTITY.get(), RenderAPGGlassTianjinJinjing::new);
        /* 1.1.6 - Ported from 4.0 - PSD doors (custom Tianjin renderer) */
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.PSD_DOOR_TIANJIN_TILE_ENTITY.get(), RenderPSDDoorTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.PSD_DOOR_TIANJIN_BMT_TILE_ENTITY.get(), RenderPSDDoorTianjinBMT::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.PSD_DOOR_TIANJIN_JINJING_TILE_ENTITY.get(), RenderPSDDoorTianjin::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.PSD_DOOR_SINGLE_LEFT_TILE_ENTITY.get(), RenderTianjinPSDDoor::new);
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.PSD_DOOR_SINGLE_RIGHT_TILE_ENTITY.get(), RenderTianjinPSDDoor::new);
        /* 1.1.6 - Ported from 4.0 - PSD top (custom Tianjin top plate renderer) */
        registerRouteRenderer(BlockEntityTypes.PSD_TOP_TIANJIN_TILE_ENTITY.get(), RenderPSDTopTianjin::new);
        registerRouteRenderer(BlockEntityTypes.PSD_TOP_TIANJIN_JINJING_TILE_ENTITY.get(), RenderPSDTopTianjin::new);
        registerRouteRenderer(BlockEntityTypes.PSD_TOP_TIANJIN_BMT_TILE_ENTITY.get(), RenderPSDTopTianjinBMT::new);
        /* 1.1.6 - Ported from 4.0 - Station Name Projector */
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.STATION_NAME_PROJECTOR.get(), RenderStationNameProjector::new);
        /* 1.1.6 - Ported from 4.0 - PIDS Tianjin */
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.PIDS_TIANJIN_TILE_ENTITY.get(), RenderPIDSTianjin::new);
        /* 1.1.8 - Ported from 4.0 - PIDS Tianjin (single) */
        RegistryClient.registerBlockRenderType(RenderType.translucent(), BlockList.PIDS_TIANJIN_SINGLE.get());
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.PIDS_TIANJIN_SINGLE_TILE_ENTITY.get(), RenderPIDSTianjinSingle::new);

        /* 1.1.8 - Ported from 4.0 - Smoke alarm & Time display (even) */
        RegistryClient.registerBlockRenderType(RenderType.cutout(), BlockList.SMOKE_ALARM.get());
        RegistryClient.registerTileEntityRenderer(BlockEntityTypes.TIME_DISPLAY_EVEN_TILE_ENTITY.get(), RenderTimeDisplayEven::new);

        BlockSmokeAlarm.TileEntitySmokeAlarm.updateSoundSource = SMOKE_ALARM_SOUND_INSTANCE::setPos;

        RegistryClient.registerNetworkReceiver(IPacket.PACKET_OPEN_CUSTOM_CONTENT_SCREEN, packet -> PacketGuiClient.openCustomContentScreenS2C(Minecraft.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(IPacket.PACKET_OPEN_CUSTOM_COLOR_SCREEN, packet -> PacketGuiClient.openCustomColorScreenS2C(Minecraft.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(IPacket.PACKET_OPEN_RAILWAY_SIGN_SCREEN, packet -> PacketGuiClient.openRailwaySignScreenS2C(Minecraft.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(IPacket.PACKET_OPEN_RAILWAY_SIGN_DOUBLE_SCREEN, packet -> PacketGuiClient.openRailwaySignDoubleScreenS2C(Minecraft.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(IPacket.PACKET_OPEN_PIDS_TIANJIN_SCREEN, packet -> PacketGuiClient.openPIDSTianjinScreenS2C(Minecraft.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(IPacket.PACKET_OPEN_STATION_NAVIGATOR_SCREEN, packet -> PacketGuiClient.openStationNavigatorScreenS2C(Minecraft.getInstance(), packet));
        RegistryClient.registerNetworkReceiver(IPacket.PACKET_OPEN_RAILWAY_SIGN_SINGLE_SCREEN, packet -> PacketGuiClient.openRailwaySignSingleScreenS2C(Minecraft.getInstance(), packet));
    }

    /**
     * Helper to register route-base renderers (RenderAPGGlass / RenderPSDTop) for Tianjin tile entities.
     * Tianjin tile entities extend {@link mtr.block.BlockPSDTop.TileEntityRouteBase} directly, while the
     * MTR renderers are typed to their specific TileEntity subclasses. Since the renderers only use
     * methods from {@code TileEntityRouteBase}, an unchecked cast is safe here.
     *
     * @since 1.1.6
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends mtr.mappings.BlockEntityMapper> void registerRouteRenderer(
            net.minecraft.world.level.block.entity.BlockEntityType<T> type,
            java.util.function.Function<net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher, mtr.mappings.BlockEntityRendererMapper> factory) {
        RegistryClient.registerTileEntityRenderer(type, (java.util.function.Function) factory);
    }
}
