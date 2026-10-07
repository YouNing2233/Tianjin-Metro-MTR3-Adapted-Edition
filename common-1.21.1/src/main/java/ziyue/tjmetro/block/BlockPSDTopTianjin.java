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
import net.minecraft.world.phys.BlockHitResult;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjin;

import java.util.function.Consumer;

import static mtr.block.IBlock.SIDE;
import static mtr.block.IBlock.SIDE_EXTENDED;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

/**
 * Tianjin PSD top plate (吊板, default / TRT / Jinjing style).
 * <p>
 * The top plate displays the station name, direction arrows or the route map,
 * depending on the {@link EnumPSDType} and {@link BlockPSDTop.EnumPersistent} state.
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockPSDTop
 * @see ziyue.tjmetro.render.RenderPSDTopTianjin
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockPSDTopTianjin extends BlockPSDTop implements BlockFlagPSDTianjin
{
    public final boolean jinjing;
    public static final EnumProperty<EnumPSDType> STYLE = EnumProperty.create("style", EnumPSDType.class);

    public BlockPSDTopTianjin(boolean jinjing) {
        this.jinjing = jinjing;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingItem(world, player, item -> {
            if (item == ItemList.WRENCH.get()) {
                if (IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.ARROW || world.getBlockState(pos.below()).getBlock() instanceof BlockPSDDoorTianjin) {
                    world.setBlockAndUpdate(pos, IBlockExtends.cycleBlockState(state, STYLE, value -> value != EnumPSDType.NEXT_STATION));
                    final BlockPos pos1 = IBlock.getStatePropertySafe(state, SIDE_EXTENDED) == IBlock.EnumSide.LEFT ? pos.relative(IBlock.getStatePropertySafe(state, FACING).getClockWise()) : pos.relative(IBlock.getStatePropertySafe(state, FACING).getCounterClockWise());
                    world.setBlockAndUpdate(pos1, IBlockExtends.cycleBlockState(world.getBlockState(pos1), STYLE, value -> value != EnumPSDType.NEXT_STATION));
                } else {
                    world.setBlockAndUpdate(pos, IBlockExtends.cycleBlockState(state, STYLE, EnumPSDType.DEFAULT, EnumPSDType.NEXT_STATION));
                    final Consumer<Direction> setStyle = direction -> {
                        EnumPSDType style = IBlock.getStatePropertySafe(world, pos, STYLE);
                        BlockPos offsetPos = pos;
                        for (; ; ) {
                            if (IBlock.getStatePropertySafe(world.getBlockState(offsetPos), PERSISTENT) == EnumPersistent.ARROW || world.getBlockState(offsetPos.below()).getBlock() instanceof BlockPSDDoorTianjin) {
                                offsetPos = offsetPos.relative(direction);
                                style = style == EnumPSDType.DEFAULT ? EnumPSDType.NEXT_STATION : EnumPSDType.DEFAULT;
                            } else if (this == world.getBlockState(offsetPos).getBlock()) {
                                world.setBlockAndUpdate(offsetPos, world.getBlockState(offsetPos).setValue(STYLE, style));
                            } else {
                                break;
                            }
                            offsetPos = offsetPos.relative(direction);
                        }
                    };
                    setStyle.accept(IBlock.getStatePropertySafe(state, FACING).getClockWise());
                    setStyle.accept(IBlock.getStatePropertySafe(state, FACING).getCounterClockWise());
                }
            } else if (item == Items.BRUSH.get()) {
                world.setBlockAndUpdate(pos, state.cycle(ARROW_DIRECTION));
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getClockWise(), ARROW_DIRECTION, 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getCounterClockWise(), ARROW_DIRECTION, 1);
            } else {
                final boolean shouldBePersistent = IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.NONE;
                setState(world, pos, shouldBePersistent);
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getClockWise(), offsetPos -> setState(world, offsetPos, shouldBePersistent), 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getCounterClockWise(), offsetPos -> setState(world, offsetPos, shouldBePersistent), 1);
            }
        }, null, Items.BRUSH.get(), ItemList.WRENCH.get(), net.minecraft.world.item.Items.SHEARS);
    }

    protected void setState(Level world, BlockPos pos, boolean shouldBePersistent) {
        final Block blockBelow = world.getBlockState(pos.below()).getBlock();
        if (blockBelow instanceof BlockFlagPSDTianjin) {
            if (shouldBePersistent) {
                world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(PERSISTENT, blockBelow instanceof BlockPSDDoorTianjin ? EnumPersistent.ARROW : blockBelow instanceof BlockPSDGlassTianjin ? EnumPersistent.ROUTE : EnumPersistent.BLANK));
            } else {
                world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(PERSISTENT, EnumPersistent.NONE));
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.NONE && !(neighborState.getBlock() instanceof BlockPSDAPGBase)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return getActualState(world, pos, jinjing);
        }
    }

    @Override
    public Item asItem() {
        return jinjing ? ItemList.PSD_GLASS_TIANJIN_JINJING.get() : ItemList.PSD_GLASS_TIANJIN.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPSDTopTianjin(pos, state, jinjing);
    }

    public static BlockState getActualState(LevelAccessor world, BlockPos pos, boolean jinjing) {
        Direction facing = null;
        IBlock.EnumSide side = null;
        boolean airLeft = false, airRight = false;

        final BlockState stateBelow = world.getBlockState(pos.below());
        final Block blockBelow = stateBelow.getBlock();
        if (blockBelow instanceof BlockFlagPSDTianjin) {
            if (blockBelow instanceof BlockPSDDoorTianjin) {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE);
            } else {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE_EXTENDED);
            }

            if (blockBelow instanceof BlockPSDGlassEndTianjin) {
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
        BlockState neighborState = (oldState.getBlock() instanceof BlockPSDTop ? oldState : (jinjing ? BlockList.PSD_TOP_TIANJIN_JINJING.get() : BlockList.PSD_TOP_TIANJIN.get()).defaultBlockState()).setValue(AIR_LEFT, airLeft).setValue(AIR_RIGHT, airRight);
        if (facing != null) {
            neighborState = neighborState.setValue(FACING, facing);
        }
        if (side != null) {
            neighborState = neighborState.setValue(SIDE_EXTENDED, side);
        }
        return neighborState;
    }

    public static class TileEntityPSDTopTianjin extends BlockPSDTop.TileEntityRouteBase
    {
        public final boolean jinjing;

        public TileEntityPSDTopTianjin(BlockPos pos, BlockState state, boolean jinjing) {
            super(jinjing ? BlockEntityTypes.PSD_TOP_TIANJIN_JINJING_TILE_ENTITY.get() : BlockEntityTypes.PSD_TOP_TIANJIN_TILE_ENTITY.get(), pos, state);
            this.jinjing = jinjing;
        }
    }

    public enum EnumPSDType implements StringRepresentable
    {
        DEFAULT("default"),
        STATION_NAME("station_name"),
        NEXT_STATION("next_station");

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
