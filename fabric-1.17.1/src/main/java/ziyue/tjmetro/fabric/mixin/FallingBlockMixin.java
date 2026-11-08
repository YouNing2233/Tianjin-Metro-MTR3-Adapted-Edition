package ziyue.tjmetro.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ziyue.tjmetro.TianjinMetro;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin extends Block
{
    public FallingBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true, remap = true)
    private void beforeTick(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci) {
        if (level.getGameRules().getBoolean(TianjinMetro.PREVENT_BLOCK_FALLING)) {
            ci.cancel();
        }
    }
}