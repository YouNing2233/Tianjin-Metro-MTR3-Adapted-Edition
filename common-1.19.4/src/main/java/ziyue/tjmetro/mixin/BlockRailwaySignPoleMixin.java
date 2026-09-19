package ziyue.tjmetro.mixin;

import mtr.block.BlockPoleCheckBase;
import mtr.block.BlockRailwaySign;
import mtr.block.BlockRailwaySignPole;
import mtr.block.IBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ziyue.tjmetro.block.base.BlockRailwaySignBase;

/**
 * Lets the vanilla MTR railway sign pole recognize Tianjin Metro railway signs.
 * Ported from MTR4 edition (BlockRailwaySignPoleMixin).
 *
 * @since 1.1.2.1
 */

@Mixin(BlockRailwaySignPole.class)
public abstract class BlockRailwaySignPoleMixin extends BlockPoleCheckBase
{
    @Shadow(remap = false)
    @Final
    public static IntegerProperty TYPE;

    public BlockRailwaySignPoleMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState placeWithState(BlockState stateBelow) {
        final Block block = stateBelow.getBlock();
        int type;
        if (block instanceof BlockRailwaySign sign) {
            type = (sign.length + (sign.isOdd ? 2 : 0)) % 4;
        } else if (block instanceof BlockRailwaySignBase sign) {
            type = (sign.length + (sign.isOdd ? 2 : 0)) % 4;
        } else {
            type = IBlock.getStatePropertySafe(stateBelow, TYPE);
        }
        return super.placeWithState(stateBelow).setValue(TYPE, type);
    }

    @Override
    protected boolean isBlock(Block block) {
        final boolean vanilla = block instanceof BlockRailwaySign sign && sign.length > 0 || block instanceof BlockRailwaySignPole;
        final boolean extended = block instanceof BlockRailwaySignBase sign && sign.length > 0;
        return vanilla || extended;
    }
}