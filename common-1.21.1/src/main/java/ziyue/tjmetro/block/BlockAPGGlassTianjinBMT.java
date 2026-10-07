package ziyue.tjmetro.block;

import mtr.Items;
import mtr.block.BlockAPGGlass;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjinBMT;

import java.util.function.BiConsumer;

import static mtr.block.BlockAPGGlass.ARROW_DIRECTION;
import static mtr.block.IBlock.HALF;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

/**
 * Tianjin BMT-line APG glass panel (half-height).
 * <p>
 * Shows the route map, the station name or the next station, switched with the
 * wrench (and propagated to the neighboring glass panels).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockAPGGlass
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @see ziyue.tjmetro.render.RenderAPGGlassTianjinBMT
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGGlassTianjinBMT extends BlockAPGGlass implements BlockFlagAPGTianjinBMT
{
    public static final EnumProperty<EnumDoorType> STYLE = EnumProperty.create("style", EnumDoorType.class);

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER) {
            return IBlock.checkHoldingItem(world, player, item -> {
                if (item == ItemList.WRENCH.get()) {
                    world.setBlockAndUpdate(pos, state.cycle(STYLE));
                    final BiConsumer<Boolean, Direction> setStyle = (bool, direction) -> {
                        EnumDoorType style = IBlock.getStatePropertySafe(world, pos, STYLE);
                        BlockPos offsetPos = pos;
                        for (; ; ) {
                            if (world.getBlockState(offsetPos).getBlock() == BlockList.APG_DOOR_TIANJIN_BMT.get()) {
                                offsetPos = offsetPos.relative(direction);
                                if (bool) {
                                    final int id = style.id - 1;
                                    style = EnumDoorType.byId(id < 0 ? 2 : id % 3);
                                } else {
                                    style = EnumDoorType.byId((style.id + 1) % 3);
                                }
                            } else if (world.getBlockState(offsetPos).getBlock() == BlockList.APG_GLASS_TIANJIN_BMT.get()) {
                                world.setBlockAndUpdate(offsetPos, world.getBlockState(offsetPos).setValue(STYLE, style));
                            } else {
                                break;
                            }
                            offsetPos = offsetPos.relative(direction);
                        }
                    };
                    setStyle.accept(true, IBlock.getStatePropertySafe(state, FACING).getClockWise());
                    setStyle.accept(false, IBlock.getStatePropertySafe(state, FACING).getCounterClockWise());
                } else {
                    world.setBlockAndUpdate(pos, state.cycle(ARROW_DIRECTION));
                    propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getClockWise(), ARROW_DIRECTION, 3);
                    propagate(world, pos, IBlock.getStatePropertySafe(state, FACING).getCounterClockWise(), ARROW_DIRECTION, 3);
                }
            }, null, Items.BRUSH.get(), ItemList.WRENCH.get());
        } else {
            return super.useWithoutItem(state, world, pos, player, blockHitResult);
        }
    }

    @Override
    protected boolean isAPG() {
        return true;
    }

    @Override
    public Item asItem() {
        return ItemList.APG_GLASS_TIANJIN_BMT.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAPGGlassTianjinBMT(pos, state);
    }

    public static class TileEntityAPGGlassTianjinBMT extends BlockPSDTop.TileEntityRouteBase
    {
        public TileEntityAPGGlassTianjinBMT(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.APG_GLASS_TIANJIN_BMT_TILE_ENTITY.get(), pos, state);
        }
    }

    public enum EnumDoorType implements StringRepresentable
    {
        ROUTE(0, "route"),
        STATION_NAME(1, "station_name"),
        NEXT_STATION(2, "next_station");

        public final int id;
        private final String name;

        EnumDoorType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static EnumDoorType byId(int id) {
            return switch (id) {
                case 0 -> ROUTE;
                case 1 -> STATION_NAME;
                case 2 -> NEXT_STATION;
                default -> throw new IllegalStateException();
            };
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
