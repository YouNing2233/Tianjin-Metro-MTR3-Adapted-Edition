package ziyue.tjmetro.block;

import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagAPGTianjinTRT;

/**
 * Tianjin TRT (Line 1) APG door (half-height).
 * <p>
 * Unlike the other variants the door light strip is baked into the block model,
 * so the {@code light} block state property is updated by the renderer
 * ({@link ziyue.tjmetro.render.RenderAPGDoorTianjinTRT}).
 *
 * @author ZiYueCommentary
 * @see mtr.block.BlockAPGDoor
 * @see ziyue.tjmetro.item.ItemPSDAPGTianjinBase
 * @since 1.1.6 - Ported from 4.0
 */
public class BlockAPGDoorTianjinTRT extends BlockPSDAPGDoorBase implements BlockFlagAPGTianjinTRT
{
    public static final EnumProperty<LightProperty> LIGHT = EnumProperty.create("light", LightProperty.class);

    @Override
    protected boolean isAPG() {
        return true;
    }

    @Override
    public Item asItem() {
        return ItemList.APG_DOOR_TIANJIN_TRT.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityAPGDoorTianjinTRT(pos, state);
    }

    public static class TileEntityAPGDoorTianjinTRT extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase
    {
        public TileEntityAPGDoorTianjinTRT(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.APG_DOOR_TIANJIN_TRT_TILE_ENTITY.get(), pos, state);
        }
    }

    public enum LightProperty implements StringRepresentable
    {
        NO_LIGHT("no_light"),
        LIGHT_OFF("light_off"),
        LIGHT_ON("light_on");

        private final String name;

        LightProperty(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
