package ziyue.tjmetro.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mtr.client.CustomResources;
import mtr.client.ICustomResources;
import mtr.client.IResourcePackCreatorProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ziyue.tjmetro.TianjinMetro;
import ziyue.tjmetro.block.BlockPIDSTianjin;
import ziyue.tjmetro.block.base.IRailwaySign;
import ziyue.tjmetro.client.ClientCache;

import java.awt.Color;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static mtr.client.CustomResources.CUSTOM_SIGNS;

@Mixin(CustomResources.class)
public abstract class CustomResourcesMixin implements IResourcePackCreatorProperties, ICustomResources
{
    @Inject(at = @At("HEAD"), method = "reload", remap = false)
    private static void beforeReload(ResourceManager manager, CallbackInfo ci){
        ClientCache.DATA_CACHE.resetFonts();
    }

    @Inject(at = @At("TAIL"), method = "reload", remap = false)
    private static void afterReload(ResourceManager manager, CallbackInfo ci){
        for (IRailwaySign.SignType value : IRailwaySign.SignType.values()) {
            CUSTOM_SIGNS.put(value.signId, value.sign);
        }

        BlockPIDSTianjin.CATEGORIES.clear();

        final ResourceLocation adsResource = new ResourceLocation("tjmetro", "pids_tianjin_ads.json");
        try {
            for (final Resource resource : manager.getResources(adsResource)) {
                try {
                    final JsonObject config = JsonParser.parseReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

                    for (Map.Entry<String, com.google.gson.JsonElement> entry : config.entrySet()) {
                        if (!entry.getValue().isJsonObject()) {
                            continue;
                        }

                        final long id = entry.getKey().hashCode();
                        final JsonObject adDefinition = entry.getValue().getAsJsonObject();

                        final BlockPIDSTianjin.Category category;
                        if (BlockPIDSTianjin.CATEGORIES.containsKey(id)) {
                            category = BlockPIDSTianjin.CATEGORIES.get(id);
                        } else {
                            category = new BlockPIDSTianjin.Category(
                                    id,
                                    new Color(Integer.parseInt(adDefinition.get("color").getAsString(), 16)),
                                    new TranslatableComponent(adDefinition.get("name").getAsString()),
                                    new TranslatableComponent(adDefinition.get("description").getAsString())
                            );
                            BlockPIDSTianjin.CATEGORIES.put(id, category);
                        }

                        if (!adDefinition.has("advertisements") || !adDefinition.get("advertisements").isJsonArray()) {
                            TianjinMetro.LOGGER.warn("Bad advertisement config at {}: missing advertisement array. Skipping!", entry.getKey());
                            continue;
                        }

                        final JsonArray advertisements = adDefinition.get("advertisements").getAsJsonArray();
                        final AtomicInteger index = new AtomicInteger();
                        for (com.google.gson.JsonElement adElement : advertisements) {
                            try {
                                if (adElement.isJsonObject()) {
                                    final JsonObject slide = adElement.getAsJsonObject();
                                    final ResourceLocation image = new ResourceLocation(slide.get("image").getAsString());
                                    final Component text = new TranslatableComponent(slide.get("text").getAsString());
                                    final String url = slide.has("url") && !slide.get("url").isJsonNull() && !slide.get("url").getAsString().isEmpty()
                                            ? slide.get("url").getAsString()
                                            : null;
                                    category.add(new BlockPIDSTianjin.Advertisement(image, text, url));
                                }
                            } catch (Exception e) {
                                TianjinMetro.LOGGER.warn("Bad advertisement config at {}, index {}. Skipping!", entry.getKey(), index.get());
                            }
                            index.getAndIncrement();
                        }
                    }
                } catch (Exception e) {
                    TianjinMetro.LOGGER.warn("Error when initializing advertisements!", e);
                }
            }
        } catch (Exception e) {
            TianjinMetro.LOGGER.warn("Error when initializing advertisements!", e);
        }
        TianjinMetro.LOGGER.info("Found {} categories for PIDS Tianjin", BlockPIDSTianjin.CATEGORIES.size());
    }
}