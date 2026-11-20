package ziyue.tjmetro.render;

import net.minecraft.resources.ResourceLocation;

/**
 * Pre-built texture locations for the Tianjin platform door renderers (Beta feature).
 * <p>
 * The renderers used to build a {@link ResourceLocation} out of a {@code String.format} call
 * for every door piece and every frame, which allocates a formatted string plus a new
 * resource location each time. All combinations are finite, so they are resolved once here.
 *
 * @author __Lingxii__
 * @since 1.1.3.2-EXP
 */
public final class DoorTextureCache
{
    /**
     * Texture of the locked door sign, taken from MTR itself.
     */
    public static final ResourceLocation LOCKED = ResourceLocation.parse("mtr:textures/block/sign/door_not_in_use.png");

    private static final String PREFIX_TJ = "tjmetro:textures/block/";
    private static final String PREFIX_MTR = "mtr:textures/block/";

    private static final ResourceLocation[][] APG_TIANJIN = door("apg_door_tianjin");
    private static final ResourceLocation[][] APG_TRT = door("apg_door_tianjin_trt");
    private static final ResourceLocation[][] PSD = door("psd_door_tianjin");
    private static final ResourceLocation[][] PSD_END_1 = end("psd_door_end_tianjin", 1);
    private static final ResourceLocation[][] PSD_END_2 = end("psd_door_end_tianjin", 2);

    private static final ResourceLocation[] APG_LIGHT = light("apg_door_tianjin_light");
    private static final ResourceLocation[] APG_JINJING_LIGHT = light("apg_door_tianjin_jinjing_light");
    private static final ResourceLocation[] PSD_LIGHT = new ResourceLocation[]{tex("light_off"), tex("light_on")};

    private DoorTextureCache()
    {
    }

    /**
     * @param prefix the door texture prefix of the renderer
     * @param top    whether the upper half of the door is being drawn
     * @param right  whether the right panel of the door is being drawn
     */
    public static ResourceLocation apg(String prefix, boolean top, boolean right)
    {
        final ResourceLocation[][] table = "apg_door_tianjin_trt".equals(prefix) ? APG_TRT : APG_TIANJIN;
        return table[top ? 1 : 0][right ? 1 : 0];
    }

    /**
     * @param prefix the light texture prefix of the renderer, {@code null} if the light is baked into the model
     */
    public static ResourceLocation apgLight(String prefix, boolean on)
    {
        final ResourceLocation[] table = "apg_door_tianjin_jinjing".equals(prefix) ? APG_JINJING_LIGHT : APG_LIGHT;
        return table[on ? 1 : 0];
    }

    public static ResourceLocation psd(boolean top, boolean right)
    {
        return PSD[top ? 1 : 0][right ? 1 : 0];
    }

    public static ResourceLocation psdEnd(boolean top, boolean right, int panel)
    {
        final ResourceLocation[][] table = panel <= 1 ? PSD_END_1 : PSD_END_2;
        return table[top ? 1 : 0][right ? 1 : 0];
    }

    public static ResourceLocation psdLight(boolean on)
    {
        return PSD_LIGHT[on ? 1 : 0];
    }

    private static ResourceLocation[][] door(String prefix)
    {
        final ResourceLocation[][] result = new ResourceLocation[2][2];
        for (int top = 0; top < 2; top++) {
            for (int right = 0; right < 2; right++) {
                result[top][right] = tex(prefix + "_" + (top == 1 ? "top" : "bottom") + "_" + (right == 1 ? "right" : "left"));
            }
        }
        return result;
    }

    private static ResourceLocation[][] end(String prefix, int panel)
    {
        final ResourceLocation[][] result = new ResourceLocation[2][2];
        for (int top = 0; top < 2; top++) {
            for (int right = 0; right < 2; right++) {
                result[top][right] = tex(prefix + "_" + (top == 1 ? "top" : "bottom") + "_" + (right == 1 ? "right" : "left") + "_" + panel);
            }
        }
        return result;
    }

    private static ResourceLocation[] light(String prefix)
    {
        return new ResourceLocation[]{tex(prefix + "_off"), tex(prefix + "_on")};
    }

    private static ResourceLocation tex(String name)
    {
        return ResourceLocation.parse(PREFIX_TJ + name + ".png");
    }
}
