package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagDoorSingle;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjin;

/**
 * Tianjin single-leaf PSD door.
 * Uses {@link BlockFlagDoorSingle} for shared single-leaf logic.
 *
 * @author ZiYueCommentary
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDDoorSingle extends BlockPSDAPGDoorBase implements BlockFlagPSDTianjin, BlockFlagDoorSingle
{
    public final boolean isLeft;

    public BlockPSDDoorSingle(boolean isLeft) {
        this.isLeft = isLeft;
    }

    @Override
    public boolean isLeft() {
        return isLeft;
    }

    @Override
    public Item asItem() {
        return isLeft ? ItemList.PSD_DOOR_SINGLE_LEFT.get() : ItemList.PSD_DOOR_SINGLE_RIGHT.get();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        if (IBlock.getSideDirection(state) == direction && !newState.is(this)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return BlockFlagDoorSingle.getStateForNeighborUpdate(state, world, pos);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        return BlockFlagDoorSingle.onUse(state, world, pos, player);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockFlagDoorSingle.onBreak(world, pos, state, player);
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPSDDoorSingle(pos, state, isLeft);
    }

    public static class TileEntityPSDDoorSingle extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase
    {
        public TileEntityPSDDoorSingle(BlockPos pos, BlockState state, boolean isLeft) {
            super(isLeft ? BlockEntityTypes.PSD_DOOR_SINGLE_LEFT_TILE_ENTITY.get() : BlockEntityTypes.PSD_DOOR_SINGLE_RIGHT_TILE_ENTITY.get(), pos, state);
        }
    }
}
