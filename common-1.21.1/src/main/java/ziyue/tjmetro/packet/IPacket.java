package ziyue.tjmetro.packet;

import net.minecraft.resources.ResourceLocation;
import ziyue.tjmetro.Reference;

/**
 * @since beta-1
 */

public interface IPacket
{
    ResourceLocation PACKET_OPEN_CUSTOM_CONTENT_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_open_custom_content_screen");
    ResourceLocation PACKET_UPDATE_CUSTOM_CONTENT = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_update_custom_content_screen");
    ResourceLocation PACKET_OPEN_CUSTOM_COLOR_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_open_custom_color_screen");
    ResourceLocation PACKET_UPDATE_CUSTOM_COLOR = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_update_custom_color_screen");
    ResourceLocation PACKET_OPEN_RAILWAY_SIGN_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_open_railway_sign_screen");
    ResourceLocation PACKET_OPEN_RAILWAY_SIGN_DOUBLE_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_open_railway_sign_double_screen");
    ResourceLocation PACKET_SIGN_TYPES = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_sign_types");
    ResourceLocation PACKET_SIGN_TYPES_DOUBLE = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_sign_types_double");

    ResourceLocation PACKET_UPDATE_PIDS_TIANJIN_CONFIG = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_update_pids_tianjin_config");
    ResourceLocation PACKET_UPDATE_PIDS_ADS_CONFIG = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_update_pids_ads_config");
    ResourceLocation PACKET_OPEN_PIDS_TIANJIN_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_open_pids_tianjin_screen");

    ResourceLocation PACKET_OPEN_STATION_NAVIGATOR_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_open_station_navigator_screen");
    ResourceLocation PACKET_STATION_NAVIGATOR_CONFIG = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_station_navigator_config");
    ResourceLocation PACKET_OPEN_RAILWAY_SIGN_SINGLE_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_open_railway_sign_single_screen");
    ResourceLocation PACKET_RAILWAY_SIGN_SINGLE_CONFIG = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "packet_railway_sign_single_config");
}
