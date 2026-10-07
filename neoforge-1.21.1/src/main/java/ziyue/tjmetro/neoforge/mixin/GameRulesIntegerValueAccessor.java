package ziyue.tjmetro.neoforge.mixin;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.IntegerValue.class)
public interface GameRulesIntegerValueAccessor
{
    @Invoker("create")
    static GameRules.Type<GameRules.IntegerValue> invokeCreate(int defaultValue) {
        throw new IllegalStateException("Mixin invoker not applied");
    }
}
