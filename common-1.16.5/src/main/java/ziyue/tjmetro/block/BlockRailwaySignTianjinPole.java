package ziyue.tjmetro.block;

import mtr.block.BlockRailwaySignPole;
import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockRailwaySignTianjin
 * @see BlockRailwaySignPole
 * @since beta-1
 */

public class BlockRailwaySignTianjinPole extends BlockRailwaySignPole
{
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 4);

    public BlockRailwaySignTianjinPole() {
        this(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1).noOcclusion());
    }

    public BlockRailwaySignTianjinPole(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Text.translatable("tooltip.tjmetro.railway_sign_tianjin_pole").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockState stateBelow = ctx.getLevel().getBlockState(ctx.getClickedPos().below());
        if (isBlock(stateBelow.getBlock())) {
            return placeWithState(stateBelow);
        } else if (stateBelow.isFaceSturdy(ctx.getLevel(), ctx.getClickedPos().below(), Direction.UP)) {
            return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
        } else {
            return null;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        switch (IBlock.getStatePropertySafe(state, TYPE)) {
            case 0: return IBlock.getVoxelShapeByDirection(3, 0, 7.5, 4, 16, 8.5, facing);
            case 1: return IBlock.getVoxelShapeByDirection(15, 0, 7.5, 16, 16, 8.5, facing);
            case 2: return IBlock.getVoxelShapeByDirection(11, 0, 7.5, 12, 16, 8.5, facing);
            case 3: return IBlock.getVoxelShapeByDirection(7, 0, 7.5, 8, 16, 8.5, facing);
            case 4: return IBlock.getVoxelShapeByDirection(7.5, 0, 7.5, 8.5, 16, 8.5, facing);
            default: return Shapes.block();
        }
    }

    @Override
    protected BlockState placeWithState(BlockState stateBelow) {
        final int type;
        final Block block = stateBelow.getBlock();
        final Direction facing = IBlock.getStatePropertySafe(stateBelow, FACING);
        if (block instanceof BlockRailwaySignTianjin) {
            BlockRailwaySignTianjin block1 = (BlockRailwaySignTianjin) block;
            type = (block1.length + (block1.isOdd ? 2 : 0)) % 4;
        } else if (block instanceof BlockRailwaySignTianjinBMT) {
            BlockRailwaySignTianjinBMT block1 = (BlockRailwaySignTianjinBMT) block;
            type = (block1.length + (block1.odd ? 2 : 0)) % 4;
        } else if (block instanceof BlockPIDSTianjin) {
            type = 4;
        } else {
            type = IBlock.getStatePropertySafe(stateBelow, TYPE);
        }
        return defaultBlockState().setValue(FACING, facing).setValue(TYPE, type);
    }

    @Override
    protected boolean isBlock(Block block) {
        return (block instanceof BlockRailwaySignTianjin && ((BlockRailwaySignTianjin) block).length > 0)
                || (block instanceof BlockRailwaySignTianjinBMT && ((BlockRailwaySignTianjinBMT) block).length > 0)
                || block instanceof BlockRailwaySignTianjinPole
                || block instanceof BlockRailwaySignTianjinDouble
                || block instanceof BlockPIDSTianjin;
    }
}
