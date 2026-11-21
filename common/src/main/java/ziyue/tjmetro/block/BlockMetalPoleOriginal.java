package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.block.base.BlockCustomColorBase;

/**
 * Metal pole in its original (unpainted / natural) colour.
 * <p>
 * The geometry and interactions are inherited from {@link BlockMetalPoleBMT}; the only
 * difference is the default tint, which is pure white so that the metal texture is
 * rendered exactly as authored instead of being tinted with Line 9 yellow.
 *
 * @author __Lingxii__
 * @since 1.1.4
 */
public class BlockMetalPoleOriginal extends BlockMetalPoleBMT
{
    public BlockMetalPoleOriginal() {
        super();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntity(pos, state);
    }

    public static class BlockEntity extends BlockCustomColorBase.CustomColorBlockEntity
    {
        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(BlockEntityTypes.METAL_POLE_ORIGINAL.get(), blockPos, blockState);
            this.color = 0xffffff;
        }
    }
}
