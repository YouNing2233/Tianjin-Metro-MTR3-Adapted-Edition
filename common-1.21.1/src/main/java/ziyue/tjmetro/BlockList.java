package ziyue.tjmetro;

import mtr.Blocks;
import mtr.RegistryObject;
import mtr.block.BlockPlatform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import ziyue.tjmetro.block.*;
import ziyue.tjmetro.block.base.StairBlock;

public interface BlockList
{
    RegistryObject<Block> LOGO = new RegistryObject<>(BlockLogo::new);
    RegistryObject<Block> ROLLING = new RegistryObject<>(BlockRolling::new);
    RegistryObject<Block> CEILING_NOT_LIT = new RegistryObject<>(BlockCeilingNotLit::new);
    RegistryObject<Block> STATION_COLOR_CEILING = new RegistryObject<>(BlockStationColorCeilingAuto::new);
    RegistryObject<Block> STATION_COLOR_CEILING_LIGHT = new RegistryObject<>(() -> new BlockStationColorCeiling(BlockBehaviour.Properties.ofFullCopy(Blocks.CEILING_LIGHT.get())));
    RegistryObject<Block> STATION_COLOR_CEILING_NO_LIGHT = new RegistryObject<>(() -> new BlockStationColorCeiling(BlockBehaviour.Properties.ofFullCopy(Blocks.CEILING_NO_LIGHT.get())));
    RegistryObject<Block> STATION_COLOR_CEILING_NOT_LIT = new RegistryObject<>(BlockStationColorCeilingNotLit::new);
    RegistryObject<Block> STATION_NAME_SIGN_1 = new RegistryObject<>(BlockStationNameSign1::new);
    RegistryObject<Block> STATION_NAME_SIGN_2 = new RegistryObject<>(BlockStationNameSign2::new);
    RegistryObject<Block> ROADBLOCK = new RegistryObject<>(BlockRoadblock::new);
    RegistryObject<Block> ROADBLOCK_SIGN = new RegistryObject<>(BlockRoadblockSign::new);
    RegistryObject<Block> BENCH = new RegistryObject<>(BlockBench::new);
    RegistryObject<Block> PLATFORM_TJ_1 = new RegistryObject<>(() -> new BlockPlatform(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2), false));
    RegistryObject<Block> PLATFORM_TJ_1_INDENTED = new RegistryObject<>(() -> new BlockPlatform(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2).noOcclusion(), true));
    RegistryObject<Block> PLATFORM_TJ_1_SLAB = new RegistryObject<>(() -> new BlockPlatformSlab(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2)));
    RegistryObject<Block> PLATFORM_TJ_LINE_11_1 = new RegistryObject<>(() -> new BlockPlatform(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2), false));
    RegistryObject<Block> PLATFORM_TJ_LINE_11_1_INDENTED = new RegistryObject<>(() -> new BlockPlatform(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2).noOcclusion(), true));
    RegistryObject<Block> PLATFORM_TJ_LINE_11_1_SLAB = new RegistryObject<>(() -> new BlockPlatformSlab(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2)));
    RegistryObject<Block> MARBLE_GRAY = new RegistryObject<>(() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1)));
    RegistryObject<Block> MARBLE_GRAY_SLAB = new RegistryObject<>(() -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BlockList.MARBLE_GRAY.get())));
    RegistryObject<Block> MARBLE_GRAY_STAIRS = new RegistryObject<>(() -> new StairBlock(MARBLE_GRAY.get()));
    RegistryObject<Block> PLATFORM_TJ_2 = new RegistryObject<>(() -> new BlockPlatform(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2), false));
    RegistryObject<Block> PLATFORM_TJ_2_INDENTED = new RegistryObject<>(() -> new BlockPlatform(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2).noOcclusion(), true));
    RegistryObject<Block> PLATFORM_TJ_2_SLAB = new RegistryObject<>(() -> new BlockPlatformSlab(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2)));
    RegistryObject<Block> PLATFORM_TJ_LINE_11_2 = new RegistryObject<>(() -> new BlockPlatform(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2), false));
    RegistryObject<Block> PLATFORM_TJ_LINE_11_2_INDENTED = new RegistryObject<>(() -> new BlockPlatform(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2).noOcclusion(), true));
    RegistryObject<Block> PLATFORM_TJ_LINE_11_2_SLAB = new RegistryObject<>(() -> new BlockPlatformSlab(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2)));
    RegistryObject<Block> MARBLE_YELLOW = new RegistryObject<>(() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(1)));
    RegistryObject<Block> MARBLE_YELLOW_SLAB = new RegistryObject<>(() -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MARBLE_YELLOW.get())));
    RegistryObject<Block> MARBLE_YELLOW_STAIRS = new RegistryObject<>(() -> new StairBlock(MARBLE_YELLOW.get()));
    RegistryObject<Block> STATION_NAME_WALL_LEGACY = new RegistryObject<>(BlockStationNameWallLegacy::new);
    RegistryObject<Block> CUSTOM_COLOR_CONCRETE = new RegistryObject<>(BlockCustomColorConcrete::new);
    RegistryObject<Block> CUSTOM_COLOR_CONCRETE_STAIRS = new RegistryObject<>(BlockCustomColorConcreteStairs::new);
    RegistryObject<Block> CUSTOM_COLOR_CONCRETE_SLAB = new RegistryObject<>(BlockCustomColorConcreteSlab::new);
    RegistryObject<Block> APG_CORNER = new RegistryObject<>(BlockAPGCorner::new);
    RegistryObject<Block> PLAYER_DETECTOR = new RegistryObject<>(BlockPlayerDetector::new);
    RegistryObject<Block> HIGH_SPEED_REPEATER = new RegistryObject<>(BlockHighSpeedRepeater::new);
    RegistryObject<Block> TIME_DISPLAY = new RegistryObject<>(BlockTimeDisplay::new);
    RegistryObject<Block> EMERGENCY_EXIT_SIGN = new RegistryObject<>(BlockEmergencyExitSign::new);
    RegistryObject<Block> RAILWAY_SIGN_WALL_4 = new RegistryObject<>(() -> new BlockRailwaySignWall(4));
    RegistryObject<Block> RAILWAY_SIGN_WALL_6 = new RegistryObject<>(() -> new BlockRailwaySignWall(6));
    RegistryObject<Block> RAILWAY_SIGN_WALL_8 = new RegistryObject<>(() -> new BlockRailwaySignWall(8));
    RegistryObject<Block> RAILWAY_SIGN_WALL_10 = new RegistryObject<>(() -> new BlockRailwaySignWall(10));
    RegistryObject<Block> RAILWAY_SIGN_WALL_MIDDLE = new RegistryObject<>(() -> new BlockRailwaySignWall(0));
    RegistryObject<Block> RAILWAY_SIGN_WALL_DOUBLE_4 = new RegistryObject<>(() -> new BlockRailwaySignWallDouble(4));
    RegistryObject<Block> RAILWAY_SIGN_WALL_DOUBLE_6 = new RegistryObject<>(() -> new BlockRailwaySignWallDouble(6));
    RegistryObject<Block> RAILWAY_SIGN_WALL_DOUBLE_8 = new RegistryObject<>(() -> new BlockRailwaySignWallDouble(8));
    RegistryObject<Block> RAILWAY_SIGN_WALL_DOUBLE_10 = new RegistryObject<>(() -> new BlockRailwaySignWallDouble(10));
    RegistryObject<Block> RAILWAY_SIGN_WALL_DOUBLE_MIDDLE = new RegistryObject<>(() -> new BlockRailwaySignWallDouble(0));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_2 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(2));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_3 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(3));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_4 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(4));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_5 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(5));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_6 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(6));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_7 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(7));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_8 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(8));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_9 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(9));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_10 = new RegistryObject<>(() -> new BlockRailwaySignWallBig(10));
    RegistryObject<Block> RAILWAY_SIGN_WALL_BIG_MIDDLE = new RegistryObject<>(() -> new BlockRailwaySignWallBig(0));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_3_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(3, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_4_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(4, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_5_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(5, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_6_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(6, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_7_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(7, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_2_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(2, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_3_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(3, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_4_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(4, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_5_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(5, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_6_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(6, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_7_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(7, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_8_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(8, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_9_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(9, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_10_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(10, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_11_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(11, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_12_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(12, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_13_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjin(13, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_8_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(8, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_9_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(9, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_10_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(10, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_11_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(11, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_12_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(12, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_13_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjin(13, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_MIDDLE = new RegistryObject<>(() -> new BlockRailwaySignTianjin(0, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_3_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(3, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_4_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(4, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_5_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(5, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_6_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(6, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_7_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(7, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_2_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(2, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_3_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(3, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_4_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(4, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_5_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(5, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_6_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(6, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_7_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(7, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_8_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(8, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_9_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(9, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_10_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(10, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_11_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(11, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_12_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(12, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_13_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(13, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_8_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(8, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_9_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(9, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_10_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(10, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_11_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(11, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_12_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(12, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_13_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(13, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_DOUBLE_MIDDLE = new RegistryObject<>(() -> new BlockRailwaySignTianjinDouble(0, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_POLE = new RegistryObject<>(BlockRailwaySignTianjinPole::new);
    RegistryObject<Block> METAL_DETECTION_DOOR = new RegistryObject<>(BlockMetalDetectionDoor::new);
    RegistryObject<Block> SERVICE_CORRIDOR_SIGN = new RegistryObject<>(BlockServiceCorridorSign::new);
    RegistryObject<Block> STATION_NAME_ENTRANCE_TIANJIN = new RegistryObject<>(() -> new BlockStationNameEntranceTianjin(false, BlockStationNameEntranceTianjin.Type.TRT));
    RegistryObject<Block> STATION_NAME_ENTRANCE_TIANJIN_PINYIN = new RegistryObject<>(() -> new BlockStationNameEntranceTianjin(true, BlockStationNameEntranceTianjin.Type.TRT));
    RegistryObject<Block> STATION_NAME_ENTRANCE_TIANJIN_BMT = new RegistryObject<>(() -> new BlockStationNameEntranceTianjin(false, BlockStationNameEntranceTianjin.Type.BMT));
    RegistryObject<Block> STATION_NAME_ENTRANCE_TIANJIN_BMT_PINYIN = new RegistryObject<>(() -> new BlockStationNameEntranceTianjin(true, BlockStationNameEntranceTianjin.Type.BMT));
    RegistryObject<Block> STATION_NAME_ENTRANCE_TIANJIN_JINJING = new RegistryObject<>(() -> new BlockStationNameEntranceTianjin(false, BlockStationNameEntranceTianjin.Type.JINJING));
    RegistryObject<Block> STATION_NAME_ENTRANCE_TIANJIN_JINJING_PINYIN = new RegistryObject<>(() -> new BlockStationNameEntranceTianjin(true, BlockStationNameEntranceTianjin.Type.JINJING));

    RegistryObject<Block> STATION_SIGN = new RegistryObject<>(BlockStationSign::new);
    RegistryObject<Block> STATION_SIGN_BMT = new RegistryObject<>(BlockStationSignBMT::new);
    RegistryObject<Block> NO_ENTERING_SIGN = new RegistryObject<>(BlockNoEnteringSign::new);
    RegistryObject<Block> STATION_NAVIGATOR_POLE = new RegistryObject<>(BlockStationNavigatorPole::new);
    RegistryObject<Block> CUSTOM_COLOR_MOSAIC_TILE = new RegistryObject<>(BlockCustomColorMosaicTile::new);
    RegistryObject<Block> CUSTOM_COLOR_MOSAIC_TILE_SLAB = new RegistryObject<>(BlockCustomColorMosaicTileSlab::new);
    RegistryObject<Block> CUSTOM_COLOR_MOSAIC_TILE_STAIRS = new RegistryObject<>(BlockCustomColorMosaicTileStairs::new);
    RegistryObject<Block> MOSAIC_TILE_WHITE = new RegistryObject<>(() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).requiresCorrectToolForDrops().strength(1)));
    RegistryObject<Block> MOSAIC_TILE_GRAY = new RegistryObject<>(() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(1)));

    RegistryObject<Block> CEILING_TIANJIN = new RegistryObject<>(BlockCeilingTianjinAuto::new);
    RegistryObject<Block> CEILING_TIANJIN_LIGHT = new RegistryObject<>(() -> new BlockCeilingTianjin(15));
    RegistryObject<Block> CEILING_TIANJIN_NO_LIGHT = new RegistryObject<>(() -> new BlockCeilingTianjin(0));
    RegistryObject<Block> CEILING_TIANJIN_NOT_LIT = new RegistryObject<>(() -> new BlockCeilingTianjin(0));
    RegistryObject<Block> TRASH_CAN = new RegistryObject<>(BlockTrashCan::new);
    RegistryObject<Block> METAL_POLE_BMT = new RegistryObject<>(BlockMetalPoleBMT::new);
    RegistryObject<Block> TICKET_BARRIER_TIANJIN_ENTRANCE = new RegistryObject<>(() -> new mtr.block.BlockTicketBarrier(true));
    RegistryObject<Block> TICKET_BARRIER_TIANJIN_EXIT = new RegistryObject<>(() -> new mtr.block.BlockTicketBarrier(false));


    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_2_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(2, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_3_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(3, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_4_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(4, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_5_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(5, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_6_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(6, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_7_ODD = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(7, true));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_2_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(2, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_3_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(3, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_4_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(4, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_5_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(5, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_6_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(6, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_7_EVEN = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(7, false));
    RegistryObject<Block> RAILWAY_SIGN_TIANJIN_BMT_MIDDLE = new RegistryObject<>(() -> new BlockRailwaySignTianjinBMT(0, false));

    RegistryObject<Block> STATION_NAVIGATOR_3 = new RegistryObject<>(() -> new BlockStationNavigator(3));
    RegistryObject<Block> STATION_NAVIGATOR_4 = new RegistryObject<>(() -> new BlockStationNavigator(4));
    RegistryObject<Block> STATION_NAVIGATOR_5 = new RegistryObject<>(() -> new BlockStationNavigator(5));
    RegistryObject<Block> STATION_NAVIGATOR_MIDDLE = new RegistryObject<>(() -> new BlockStationNavigator(0));

    /* 1.1.6 - Ported from 4.0 - APG (half-height platform doors) */
    RegistryObject<Block> APG_DOOR_TIANJIN = new RegistryObject<>(BlockAPGDoorTianjin::new);
    RegistryObject<Block> APG_DOOR_TIANJIN_TRT = new RegistryObject<>(BlockAPGDoorTianjinTRT::new);
    RegistryObject<Block> APG_DOOR_TIANJIN_BMT = new RegistryObject<>(BlockAPGDoorTianjinBMT::new);
    RegistryObject<Block> APG_DOOR_TIANJIN_JINJING = new RegistryObject<>(BlockAPGDoorTianjinJinjing::new);
    RegistryObject<Block> APG_DOOR_SINGLE_LEFT = new RegistryObject<>(() -> new BlockAPGDoorSingle(true));
    RegistryObject<Block> APG_DOOR_SINGLE_RIGHT = new RegistryObject<>(() -> new BlockAPGDoorSingle(false));
    RegistryObject<Block> APG_GLASS_TIANJIN = new RegistryObject<>(BlockAPGGlassTianjin::new);
    RegistryObject<Block> APG_GLASS_TIANJIN_TRT = new RegistryObject<>(BlockAPGGlassTianjinTRT::new);
    RegistryObject<Block> APG_GLASS_TIANJIN_BMT = new RegistryObject<>(BlockAPGGlassTianjinBMT::new);
    RegistryObject<Block> APG_GLASS_TIANJIN_JINJING = new RegistryObject<>(BlockAPGGlassTianjinJinjing::new);
    RegistryObject<Block> APG_GLASS_END_TIANJIN = new RegistryObject<>(BlockAPGGlassEndTianjin::new);
    RegistryObject<Block> APG_GLASS_END_TIANJIN_TRT = new RegistryObject<>(BlockAPGGlassEndTianjinTRT::new);
    RegistryObject<Block> APG_GLASS_END_TIANJIN_BMT = new RegistryObject<>(BlockAPGGlassEndTianjinBMT::new);
    RegistryObject<Block> APG_GLASS_END_TIANJIN_JINJING = new RegistryObject<>(BlockAPGGlassEndTianjinJinjing::new);

    /* 1.1.6 - Ported from 4.0 - PSD (full-height platform doors) */
    RegistryObject<Block> PSD_DOOR_TIANJIN = new RegistryObject<>(BlockPSDDoorTianjin::new);
    RegistryObject<Block> PSD_DOOR_TIANJIN_BMT = new RegistryObject<>(BlockPSDDoorTianjinBMT::new);
    RegistryObject<Block> PSD_DOOR_TIANJIN_JINJING = new RegistryObject<>(BlockPSDDoorTianjinJinjing::new);
    RegistryObject<Block> PSD_DOOR_SINGLE_LEFT = new RegistryObject<>(() -> new BlockPSDDoorSingle(true));
    RegistryObject<Block> PSD_DOOR_SINGLE_RIGHT = new RegistryObject<>(() -> new BlockPSDDoorSingle(false));
    RegistryObject<Block> PSD_GLASS_TIANJIN = new RegistryObject<>(BlockPSDGlassTianjin::new);
    RegistryObject<Block> PSD_GLASS_TIANJIN_BMT = new RegistryObject<>(BlockPSDGlassTianjinBMT::new);
    RegistryObject<Block> PSD_GLASS_TIANJIN_JINJING = new RegistryObject<>(BlockPSDGlassTianjinJinjing::new);
    RegistryObject<Block> PSD_GLASS_END_TIANJIN = new RegistryObject<>(BlockPSDGlassEndTianjin::new);
    RegistryObject<Block> PSD_GLASS_END_TIANJIN_BMT = new RegistryObject<>(BlockPSDGlassEndTianjinBMT::new);
    RegistryObject<Block> PSD_GLASS_END_TIANJIN_JINJING = new RegistryObject<>(BlockPSDGlassEndTianjinJinjing::new);
    RegistryObject<Block> PSD_TOP_TIANJIN = new RegistryObject<>(() -> new BlockPSDTopTianjin(false));
    RegistryObject<Block> PSD_TOP_TIANJIN_JINJING = new RegistryObject<>(() -> new BlockPSDTopTianjin(true));
    RegistryObject<Block> PSD_TOP_TIANJIN_BMT = new RegistryObject<>(BlockPSDTopTianjinBMT::new);

    /* 1.1.6 - Ported from 4.0 - Station Name Projector */
    RegistryObject<Block> STATION_NAME_PROJECTOR = new RegistryObject<>(BlockStationNameProjector::new);

    /* 1.1.7 - Ported from 4.0 - PIDS (Passenger Information Display System) */
    RegistryObject<Block> PIDS_TIANJIN = new RegistryObject<>(BlockPIDSTianjin::new);
    RegistryObject<Block> PIDS_TIANJIN_SINGLE = new RegistryObject<>(BlockPIDSTianjinSingle::new);

    /* 1.1.8 - Ported from 4.0 - Smoke alarm */
    RegistryObject<Block> SMOKE_ALARM = new RegistryObject<>(BlockSmokeAlarm::new);

    /* 1.1.8 - Ported from 4.0 - Time display (even) */
    RegistryObject<Block> TIME_DISPLAY_EVEN = new RegistryObject<>(BlockTimeDisplayEven::new);

    /* 1.1.8 - Ported from 4.0 - Station name plate */
    RegistryObject<Block> STATION_NAME_PLATE = new RegistryObject<>(BlockStationNamePlate::new);
    RegistryObject<Block> STATION_NAME_PLATE_MIDDLE = new RegistryObject<>(BlockStationNamePlate::new);

    /* 1.1.8 - Ported from 4.0 - Tianjin station name sign */
    RegistryObject<Block> STATION_NAME_SIGN_TIANJIN = new RegistryObject<>(BlockStationNameSignTianjin::new);

    /* 1.1.8 - Ported from 4.0 - BMT route map */
    RegistryObject<Block> ROUTE_MAP_BMT = new RegistryObject<>(BlockRouteMapBMT::new);
    RegistryObject<Block> ROUTE_MAP_BMT_MIDDLE = new RegistryObject<>(BlockRouteMapBMT::new);

    /* 1.1.8 - Ported from 4.0 - Security check sign */
    RegistryObject<Block> SECURITY_CHECK_SIGN = new RegistryObject<>(BlockSecurityCheckSign::new);
}
