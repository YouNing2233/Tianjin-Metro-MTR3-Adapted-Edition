package ziyue.tjmetro.block;

import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;
import ziyue.tjmetro.block.base.BlockRailwaySignBase;
import ziyue.tjmetro.block.base.IRailwaySign;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class BlockRailwaySignTianjinBMT extends BlockRailwaySignBase
{
    public final int length;
    public final boolean odd;

    public BlockRailwaySignTianjinBMT(int length, boolean odd) {
        this(length, odd, BlockBehaviour.Properties.of(Material.STONE).noOcclusion().lightLevel(state -> 15));
    }

    public BlockRailwaySignTianjinBMT(int length, boolean odd, Properties blockSettings) {
        super(blockSettings, length, odd);
        this.length = length;
        this.odd = odd;
    }

    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        return IRailwaySign.updateShape(state, direction, newState, BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        IRailwaySign.setPlacedBy(world, pos, state, BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get(), getMiddleLength());
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return IRailwaySign.getShape(blockState, getXStart(), BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get());
    }

    @Override
    public String getDescriptionId() {
        return "block.tjmetro.railway_sign_tianjin_bmt";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        if (length != 0)
            return new BlockEntity(length, odd, pos, state);
        else
            return null;
    }

    public static class BlockEntity extends BlockRailwaySignBase.TileEntityRailwaySign
    {
        public final int length;
        public final boolean odd;

        public BlockEntity(int length, boolean odd, BlockPos pos, BlockState state) {
            super(getType(length, odd), length, pos, state);
            this.length = length;
            this.odd = odd;
        }

        public static net.minecraft.world.level.block.entity.BlockEntityType<?> getType(int length, boolean odd) {
            switch (length) {
                case 2: return odd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_2_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_2_EVEN.get();
                case 3: return odd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_3_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_3_EVEN.get();
                case 4: return odd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_4_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_4_EVEN.get();
                case 5: return odd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_5_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_5_EVEN.get();
                case 6: return odd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_6_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_6_EVEN.get();
                case 7: return odd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_7_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_7_EVEN.get();
                default: throw new IllegalArgumentException();
            }
        }
    }
}
