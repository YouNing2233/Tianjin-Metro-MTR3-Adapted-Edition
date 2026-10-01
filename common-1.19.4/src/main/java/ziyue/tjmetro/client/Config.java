package ziyue.tjmetro.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mtr.data.RailwayData;
import mtr.mappings.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import ziyue.tjmetro.data.IGuiExtends;
import ziyue.tjmetro.Reference;
import ziyue.tjmetro.TianjinMetro;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author ZiYueCommentary
 * @see Key
 * @since beta-1
 */

public class Config
{
    public static final Key<Boolean> USE_TIANJIN_METRO_FONT = new Key<>("use_tianjin_metro_font", true);
    public static final Key<Boolean> ROTATED_STATION_NAME = new Key<>("rotated_station_name", true);
    public static final Key<Boolean> DISABLE_TRAIN_RENDERING = new Key<>("disable_train_rendering", false);
    public static final Key<Integer> DYNAMIC_TEXTURE_MAX_SIZE = new Key<>("dynamic_texture_max_size", 2048);
    public static final Key<Boolean> SHRINK_DENSE_STATION_NAME = new Key<>("shrink_dense_station_name", true);
    public static final Key<Boolean> VERTICAL_INTERCHANGE_NAME = new Key<>("vertical_interchange_name", true);

    protected static final Path CONFIG_FILE_PATH = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("tjmetro.json");
    public static final List<Supplier<MutableComponent>> FOOTERS = Arrays.asList(
            () -> IGuiExtends.format(Text.translatable("footer.tjmetro.sources"), IGuiExtends.LINK_STYLE.apply(Reference.SOURCE_REPO)),
            () -> IGuiExtends.format(Text.translatable("footer.tjmetro.contributors"), IGuiExtends.LINK_STYLE.apply(Reference.CONTRIBUTORS)),
            () -> IGuiExtends.format(Text.translatable("footer.tjmetro.forum"), IGuiExtends.LINK_STYLE.apply(Reference.FORUM).withColor(TextColor.fromRgb(15946322))),
            () -> IGuiExtends.format(Text.translatable("footer.tjmetro.weblate"), IGuiExtends.LINK_STYLE.apply(Reference.WEBLATE))
    );

    public static void refreshProperties() {
        TianjinMetro.LOGGER.info("Refreshed Tianjin Metro config");
        try {
            final JsonObject jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(CONFIG_FILE_PATH))).getAsJsonObject();
            USE_TIANJIN_METRO_FONT.set(getBoolean(jsonConfig, USE_TIANJIN_METRO_FONT));
            ROTATED_STATION_NAME.set(getBoolean(jsonConfig, ROTATED_STATION_NAME));
            DISABLE_TRAIN_RENDERING.set(getBoolean(jsonConfig, DISABLE_TRAIN_RENDERING));
            DYNAMIC_TEXTURE_MAX_SIZE.set(getInteger(jsonConfig, DYNAMIC_TEXTURE_MAX_SIZE));
            SHRINK_DENSE_STATION_NAME.set(getBoolean(jsonConfig, SHRINK_DENSE_STATION_NAME));
            VERTICAL_INTERCHANGE_NAME.set(getBoolean(jsonConfig, VERTICAL_INTERCHANGE_NAME));
        } catch (Exception e) {
            writeToFile();
            TianjinMetro.LOGGER.error(e);
        }
    }


    /**
     * Reads a boolean setting, falling back to its default when the config file predates the option.
     *
     * @since 1.1.2.1
     */
    private static boolean getBoolean(JsonObject jsonConfig, Key<Boolean> key) {
        try {
            return jsonConfig.get(key.getId()).getAsBoolean();
        } catch (Exception e) {
            return key.getDefault();
        }
    }

    /**
     * Reads an integer setting, falling back to its default when the config file predates the option.
     *
     * @since 1.1.2.1
     */
    private static int getInteger(JsonObject jsonConfig, Key<Integer> key) {
        try {
            return jsonConfig.get(key.getId()).getAsInt();
        } catch (Exception e) {
            return key.getDefault();
        }
    }

    protected static void writeToFile() {
        TianjinMetro.LOGGER.info("Wrote Tianjin Metro config to file");
        final JsonObject jsonConfig = new JsonObject();
        jsonConfig.addProperty(USE_TIANJIN_METRO_FONT.getId(), USE_TIANJIN_METRO_FONT.get());
        jsonConfig.addProperty(ROTATED_STATION_NAME.getId(), ROTATED_STATION_NAME.get());
        jsonConfig.addProperty(DISABLE_TRAIN_RENDERING.getId(), DISABLE_TRAIN_RENDERING.get());
        jsonConfig.addProperty(DYNAMIC_TEXTURE_MAX_SIZE.getId(), DYNAMIC_TEXTURE_MAX_SIZE.get());
        jsonConfig.addProperty(SHRINK_DENSE_STATION_NAME.getId(), SHRINK_DENSE_STATION_NAME.get());
        jsonConfig.addProperty(VERTICAL_INTERCHANGE_NAME.getId(), VERTICAL_INTERCHANGE_NAME.get());

        try {
            Files.write(CONFIG_FILE_PATH, Collections.singleton(RailwayData.prettyPrint(jsonConfig)));
        } catch (Exception e) {
            TianjinMetro.LOGGER.error(e);
        }
    }

    /**
     * A wrapper for settings.
     *
     * @author ZiYueCommentary
     * @see Config
     * @since beta-1
     */
    public static class Key<T>
    {
        protected T value;
        protected final T defaultValue;
        protected final String id;

        public Key(String id, T defaultValue) {
            this.id = id;
            this.value = defaultValue;
            this.defaultValue = defaultValue;
        }

        public String getId() {
            return id;
        }

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
            writeToFile();
        }

        public T getDefault() {
            return defaultValue;
        }
    }
}
