package ziyue.tjmetro.block;

import mtr.Blocks;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.block.base.BlockCustomColorBase;

/**
 * @author ZiYueCommentary
 * @see BlockCustomColorBase
 * @since 1.1.0
 */
public class BlockCustomColorMosaicTile extends BlockCustomColorBase
{
    public BlockCustomColorMosaicTile() {
        this(BlockBehaviour.Properties.copy(Blocks.STATION_COLOR_CONCRETE.get()));
    }

    public BlockCustomColorMosaicTile(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new CustomColorMosaicTileEntity(pos, state);
    }

    public static class CustomColorMosaicTileEntity extends CustomColorBlockEntity
    {
        public CustomColorMosaicTileEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.CUSTOM_COLOR_MOSAIC_TILE_TILE_ENTITY.get(), pos, state);
        }
    }
}
