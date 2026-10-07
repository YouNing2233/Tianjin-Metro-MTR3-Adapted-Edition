package ziyue.tjmetro.block;

import mtr.Blocks;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.EntityBlockMapper;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.block.base.BlockCustomColorBase;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockCustomColorBase
 * @since 1.1.0
 */
public class BlockCustomColorMosaicTileSlab extends SlabBlock implements EntityBlockMapper
{
    public BlockCustomColorMosaicTileSlab() {
        this(BlockBehaviour.Properties.copy(Blocks.STATION_COLOR_CONCRETE.get()));
    }

    public BlockCustomColorMosaicTileSlab(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new CustomColorMosaicTileSlabEntity(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Text.translatable("tooltip.tjmetro.custom_color").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof BlockCustomColorBase.CustomColorBlockEntity) {
                BlockCustomColorBase.CustomColorBlockEntity entity1 = (BlockCustomColorBase.CustomColorBlockEntity) entity;
                entity1.syncData();
                PacketGuiServer.openCustomColorScreenS2C((ServerPlayer) player, pos);
            }
        });
    }

    public static class CustomColorMosaicTileSlabEntity extends BlockCustomColorBase.CustomColorBlockEntity
    {
        public CustomColorMosaicTileSlabEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.CUSTOM_COLOR_MOSAIC_TILE_SLAB_TILE_ENTITY.get(), pos, state);
        }
    }
}
