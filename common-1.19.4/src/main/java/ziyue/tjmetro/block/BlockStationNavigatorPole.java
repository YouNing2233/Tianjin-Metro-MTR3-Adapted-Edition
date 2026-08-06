package ziyue.tjmetro.block;

import mtr.block.IBlock;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author ZiYueCommentary
 * @since 1.1.0
 */
public class BlockStationNavigatorPole extends Block
{
    public BlockStationNavigatorPole() {
        this(BlockBehaviour.Properties.of(Material.STONE).noOcclusion());
    }

    public BlockStationNavigatorPole(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Text.translatable("tooltip.tjmetro.station_navigator_pole").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return IBlock.getVoxelShapeByDirection(7.25, 0, 7.25, 8.75, 16, 8.75, Direction.NORTH);
    }
}
