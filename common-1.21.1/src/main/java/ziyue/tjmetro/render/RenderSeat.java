package ziyue.tjmetro.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import ziyue.tjmetro.entity.EntitySeat;

/**
 * Invisible renderer for the bench seat entity.
 *
 * <p>The entity itself never draws anything, but Minecraft requires every registered entity type
 * to have a renderer on the client, otherwise rendering the entity crashes the game.</p>
 *
 * @since 1.1.3.1
 */

public class RenderSeat extends EntityRenderer<EntitySeat>
{
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    public RenderSeat(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySeat entity) {
        return TEXTURE;
    }
}
