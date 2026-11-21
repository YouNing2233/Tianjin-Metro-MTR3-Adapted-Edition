package ziyue.tjmetro.mixin;

import mtr.block.BlockPSDAPGDoorBase;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ziyue.tjmetro.block.IBlockExtends;

/**
 * Lets the Tianjin Metro wrench open and close vanilla MTR platform screen doors
 * and platform gates, in addition to MTR's own brush.
 * <p>
 * MTR's {@code BlockPSDAPGDoorBase#use} only accepts {@code mtr.Items.BRUSH}
 * ({@code IBlock.checkHoldingBrush}), which is inconsistent with every other
 * Tianjin Metro block that is operated with the wrench.
 *
 * @since 1.1.3.3
 */

@Mixin(BlockPSDAPGDoorBase.class)
public abstract class BlockPSDAPGDoorBaseMixin
{
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lmtr/block/IBlock;checkHoldingBrush(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Ljava/lang/Runnable;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult tjmetro$checkHoldingBrushOrWrench(Level world, Player player, Runnable callback) {
        return IBlockExtends.checkHoldingBrushOrWrench(world, player, callback);
    }
}
