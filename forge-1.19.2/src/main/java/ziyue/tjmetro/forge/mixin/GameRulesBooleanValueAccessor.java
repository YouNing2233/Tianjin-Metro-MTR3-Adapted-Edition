package ziyue.tjmetro.forge.mixin;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.BooleanValue.class)
public interface GameRulesBooleanValueAccessor
{
    @Invoker("create")
    static GameRules.Type<GameRules.BooleanValue> invokeCreate(boolean defaultValue) {
        throw new IllegalStateException("Mixin invoker not applied");
    }
}
