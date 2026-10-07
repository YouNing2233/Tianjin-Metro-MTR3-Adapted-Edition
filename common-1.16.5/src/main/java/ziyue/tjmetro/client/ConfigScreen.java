package ziyue.tjmetro.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;

import java.util.Random;

import static ziyue.tjmetro.client.Config.*;

public class ConfigScreen
{
    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("gui.tjmetro.options")).transparentBackground();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory categoryTianjinMetro = builder.getOrCreateCategory(Text.translatable("config.category.tjmetro"));
        BooleanListEntry booleanUseTianjinMetroFont = entryBuilder.startBooleanToggle(Text.translatable("config.tjmetro.use_tianjin_metro_font"), USE_TIANJIN_METRO_FONT.get()).setTooltip(Text.translatable("tooltip.tjmetro.use_tianjin_metro_font"), Text.translatable("tooltip.tjmetro.experimental").withStyle(ChatFormatting.YELLOW)).build();
        TextListEntry textFooter = entryBuilder.startTextDescription(FOOTERS.get(new Random().nextInt(FOOTERS.size())).get()).build();
        final BooleanListEntry booleanDisableTrainRendering = entryBuilder.startBooleanToggle(Text.translatable("config.tjmetro.disable_train_rendering"), DISABLE_TRAIN_RENDERING.get()).setDefaultValue(DISABLE_TRAIN_RENDERING.getDefault()).setTooltip(Text.translatable("tooltip.tjmetro.disable_train_rendering")).build();
        final IntegerSliderEntry intDynamicTextureMaxSize = entryBuilder.startIntSlider(Text.translatable("config.tjmetro.dynamic_texture_max_size"), DYNAMIC_TEXTURE_MAX_SIZE.get(), 512, 8192).setDefaultValue(DYNAMIC_TEXTURE_MAX_SIZE.getDefault()).setTooltip(Text.translatable("tooltip.tjmetro.dynamic_texture_max_size")).build();
        final BooleanListEntry booleanShrinkDenseStationName = entryBuilder.startBooleanToggle(Text.translatable("config.tjmetro.shrink_dense_station_name"), SHRINK_DENSE_STATION_NAME.get()).setDefaultValue(SHRINK_DENSE_STATION_NAME.getDefault()).setTooltip(Text.translatable("tooltip.tjmetro.shrink_dense_station_name")).build();
        final BooleanListEntry booleanVerticalInterchangeName = entryBuilder.startBooleanToggle(Text.translatable("config.tjmetro.vertical_interchange_name"), VERTICAL_INTERCHANGE_NAME.get()).setDefaultValue(VERTICAL_INTERCHANGE_NAME.getDefault()).setTooltip(Text.translatable("tooltip.tjmetro.vertical_interchange_name")).build();
        final IntegerSliderEntry intStationNameSize = entryBuilder.startIntSlider(Text.translatable("config.tjmetro.station_name_size"), STATION_NAME_SIZE.get(), 40, 130).setDefaultValue(STATION_NAME_SIZE.getDefault()).setTooltip(Text.translatable("tooltip.tjmetro.station_name_size")).build();
        categoryTianjinMetro.addEntry(booleanUseTianjinMetroFont).addEntry(booleanDisableTrainRendering).addEntry(intDynamicTextureMaxSize).addEntry(booleanShrinkDenseStationName).addEntry(booleanVerticalInterchangeName).addEntry(intStationNameSize).addEntry(textFooter);

        builder.setSavingRunnable(() -> {
            USE_TIANJIN_METRO_FONT.set(booleanUseTianjinMetroFont.getValue());
            DISABLE_TRAIN_RENDERING.set(booleanDisableTrainRendering.getValue());
            DYNAMIC_TEXTURE_MAX_SIZE.set(intDynamicTextureMaxSize.getValue());
            SHRINK_DENSE_STATION_NAME.set(booleanShrinkDenseStationName.getValue());
            VERTICAL_INTERCHANGE_NAME.set(booleanVerticalInterchangeName.getValue());
            STATION_NAME_SIZE.set(intStationNameSize.getValue());
            ClientCache.DATA_CACHE.resetFonts();
            ClientCache.DATA_CACHE.refreshDynamicResources();
        });
        return builder.build();
    }
}
