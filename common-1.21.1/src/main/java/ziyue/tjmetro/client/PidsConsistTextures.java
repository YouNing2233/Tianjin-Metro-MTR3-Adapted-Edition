package ziyue.tjmetro.client;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import ziyue.tjmetro.Reference;
import ziyue.tjmetro.TianjinMetro;

/**
 * 「节数 → 一张动态贴图」的 MC 胶水层，移植自 mtr-external-lights 的 PidsXiamenConsistTextures。
 *
 * <p>分工：{@link PidsConsistPainter} 只画图形（底带 + 卡片 + 雪花，纯 Java2D，无文字）；
 * 这里负责素材读取、贴图上传与缓存；「N车」文字由渲染器用 {@code layout(int)} 给出的位置另画。
 */
public final class PidsConsistTextures {

    private static final ResourceLocation BAND_ID = ResourceLocation.parse(Reference.MOD_ID + ":textures/gui/pids/consist_band.png");
    private static final ResourceLocation CARD_ID = ResourceLocation.parse(Reference.MOD_ID + ":textures/gui/pids/consist_card.png");
    private static final ResourceLocation FLAKE_ID = ResourceLocation.parse(Reference.MOD_ID + ":textures/gui/pids/consist_flake.png");

    private static final Map<Integer, ResourceLocation> TEXTURES = new HashMap<>();

    private static BufferedImage band;
    private static BufferedImage card;
    private static BufferedImage flake;
    private static boolean warned;

    private PidsConsistTextures() {
    }

    /** 节数对应的编组条贴图；素材缺失或客户端未就绪时返回 {@code null}。 */
    public static ResourceLocation texture(final int cars) {
        final int n = Math.max(0, Math.min(PidsConsistPainter.MAX_CARS, cars));
        if (n <= 0) {
            return null;
        }

        final ResourceLocation cached = TEXTURES.get(n);
        if (cached != null) {
            return cached;
        }

        final BufferedImage bandImage = asset(0);
        final BufferedImage cardImage = asset(1);
        final BufferedImage flakeImage = asset(2);
        if (bandImage == null || cardImage == null || flakeImage == null) {
            return null;
        }

        try {
            final BufferedImage image = PidsConsistPainter.paint(bandImage, cardImage, flakeImage, n);
            if (image == null) {
                return null;
            }
            final int w = image.getWidth();
            final int h = image.getHeight();
            final NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, w, h, false);
            final int[] pixels = image.getRGB(0, 0, w, h, null, 0, w);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    final int argb = pixels[y * w + x];
                    nativeImage.setPixelRGBA(x, y, ((argb & 0xFF000000)) + ((argb & 0xFF) << 16) + (argb & 0xFF00) + ((argb >> 16) & 0xFF));
                }
            }
            final DynamicTexture texture = new DynamicTexture(nativeImage);
            texture.setFilter(false, false);
            final ResourceLocation id = ResourceLocation.parse(Reference.MOD_ID + ":" + "dynamic_consist_" + n);
            Minecraft.getInstance().getTextureManager().register(id, texture);
            TEXTURES.put(n, id);
            return id;
        } catch (final Throwable t) {
            if (!warned) {
                warned = true;
                TianjinMetro.LOGGER.warn("Cannot build the PIDS consist texture", t);
            }
            return null;
        }
    }

    /** 资源重载后清空（由渲染器在换包时调用）。 */
    public static void invalidate() {
        band = null;
        card = null;
        flake = null;
        TEXTURES.clear();
    }

    private static BufferedImage asset(final int which) {
        switch (which) {
            case 0:
                if (band == null) {
                    band = read(BAND_ID);
                }
                return band;
            case 1:
                if (card == null) {
                    card = read(CARD_ID);
                }
                return card;
            default:
                if (flake == null) {
                    flake = read(FLAKE_ID);
                }
                return flake;
        }
    }

    private static BufferedImage read(final ResourceLocation id) {
        try {
            final Minecraft client = Minecraft.getInstance();
            final ResourceManager manager = client == null ? null : client.getResourceManager();
            if (manager == null) {
                return null;
            }
            final Optional<Resource> resource = manager.getResource(id);
            if (!resource.isPresent()) {
                return null;
            }
            try (InputStream in = resource.get().open()) {
                return ImageIO.read(in);
            }
        } catch (final Throwable t) {
            if (!warned) {
                warned = true;
                TianjinMetro.LOGGER.warn("Cannot read the PIDS consist asset {}", id, t);
            }
            return null;
        }
    }
}
