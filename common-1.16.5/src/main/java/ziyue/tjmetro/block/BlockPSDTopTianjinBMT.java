package ziyue.tjmetro.block;

import mtr.Items;
import mtr.block.BlockPSDAPGBase;
import mtr.block.BlockPSDAPGGlassEndBase;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinBMT;

import static mtr.block.IBlock.SIDE;
import static mtr.block.IBlock.SIDE_EXTENDED;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

/**
 * Tianjin BMT-line PSD top plate (吊板).
 * <p>
 * The BMT top plate shows the station name / route map, and uses {@link EnumPSDType}
 * to switch between the BMT and TRT display styles.
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockPSDTop
 * @see ziyue.tjmetro.render.RenderPSDTopTianjinBMT
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDTopTianjinBMT extends BlockPSDTop implements BlockFlagPSDTianjinBMT
{
    public static final IntegerProperty ARROW_DIRECTION = IntegerProperty.create("arrow_direction", 0, 1);
    public static final EnumProperty<EnumPSDType> STYLE = EnumProperty.create("style", EnumPSDType.class);

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingItem(world, player, item -> {
            if (item == ItemList.WRENCH.get()) {
                world.setBlockAndUpdate(pos, state.cycle(STYLE));
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getClockWise(), STYLE, 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getCounterClockWise(), STYLE, 1);
            } else {
                world.setBlockAndUpdate(pos, state.cycle(ARROW_DIRECTION));
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getClockWise(), ARROW_DIRECTION, 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getCounterClockWise(), ARROW_DIRECTION, 1);
            }
        }, null, Items.BRUSH.get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !(neighborState.getBlock() instanceof BlockPSDAPGBase)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return getActualState(world, pos);
        }
    }

    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_TIANJIN_BMT.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE, FACING, SIDE_EXTENDED, AIR_LEFT, AIR_RIGHT, ARROW_DIRECTION);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPSDTopTianjinBMT(pos, state);
    }

    public static BlockState getActualState(LevelAccessor world, BlockPos pos) {
        Direction facing = null;
        IBlock.EnumSide side = null;
        boolean airLeft = false, airRight = false;

        final BlockState stateBelow = world.getBlockState(pos.below());
        final Block blockBelow = stateBelow.getBlock();
        if (blockBelow instanceof BlockPSDGlassTianjinBMT || blockBelow instanceof BlockPSDDoorTianjinBMT || blockBelow instanceof BlockPSDGlassEndTianjinBMT) {
            if (blockBelow instanceof BlockPSDDoorTianjinBMT) {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE);
            } else {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE_EXTENDED);
            }

            if (blockBelow instanceof BlockPSDGlassEndTianjinBMT) {
                if (IBlock.getStatePropertySafe(stateBelow, BlockPSDAPGGlassEndBase.TOUCHING_LEFT) == BlockPSDAPGGlassEndBase.EnumPSDAPGGlassEndSide.AIR) {
                    airLeft = true;
                }
                if (IBlock.getStatePropertySafe(stateBelow, BlockPSDAPGGlassEndBase.TOUCHING_RIGHT) == BlockPSDAPGGlassEndBase.EnumPSDAPGGlassEndSide.AIR) {
                    airRight = true;
                }
            }

            facing = IBlock.getStatePropertySafe(stateBelow, FACING);
        }

        final BlockState oldState = world.getBlockState(pos);
        BlockState neighborState = (oldState.getBlock() instanceof BlockPSDTop ? oldState : BlockList.PSD_TOP_TIANJIN_BMT.get().defaultBlockState()).setValue(AIR_LEFT, airLeft).setValue(AIR_RIGHT, airRight);
        if (facing != null) {
            neighborState = neighborState.setValue(FACING, facing);
        }
        if (side != null) {
            neighborState = neighborState.setValue(SIDE_EXTENDED, side);
        }
        return neighborState;
    }

    public static class TileEntityPSDTopTianjinBMT extends BlockPSDTop.TileEntityRouteBase
    {
        public TileEntityPSDTopTianjinBMT(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PSD_TOP_TIANJIN_BMT_TILE_ENTITY.get(), pos, state);
        }
    }

    public enum EnumPSDType implements StringRepresentable
    {
        BMT("bmt"),
        TRT("trt");

        private final String name;

        EnumPSDType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
