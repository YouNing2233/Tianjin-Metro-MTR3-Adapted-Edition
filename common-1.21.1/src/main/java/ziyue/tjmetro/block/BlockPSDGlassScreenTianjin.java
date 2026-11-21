package ziyue.tjmetro.block;

import mtr.block.IBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import mtr.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.block.flag.BlockFlagPSDTianjinJinjing;
import ziyue.tjmetro.packet.PacketGuiServer;

/**
 * A tall platform screen door glass panel with an embedded passenger
 * information board.
 *
 * <p>Three panels placed side by side (and two blocks tall) form one board; the
 * board itself is drawn by {@code RenderPSDGlassScreenTianjin}. Everything else
 * - advertisements, the configuration screen, the platform selection and the
 * scrolling message - is inherited from {@link BlockPIDSTianjin}.</p>
 *
 * @author ZiYueCommentary
 * @since 1.1.8
 */
public class BlockPSDGlassScreenTianjin extends mtr.block.BlockPSDAPGGlassBase implements EntityBlock, BlockFlagPSDTianjinJinjing
{
    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_SCREEN_TIANJIN.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        final Object half = IBlock.getStatePropertySafe(blockState, IBlock.HALF);
        return half instanceof Enum<?> && ((Enum<?>) half).name().equals("LOWER") ? new TileEntityPSDGlassScreenTianjin(blockPos, blockState) : null;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level world, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if ((player.isHolding(Items.BRUSH.get()) || player.isHolding(ItemList.WRENCH.get())) && !world.isClientSide) {
            final BlockEntity blockEntity = world.getBlockEntity(blockPos);

            if (blockEntity instanceof TileEntityPSDGlassScreenTianjin) {
                PacketGuiServer.openPIDSTianjinScreenS2C((ServerPlayer) player, blockPos);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useWithoutItem(blockState, world, blockPos, player, blockHitResult);
    }

    public static class TileEntityPSDGlassScreenTianjin extends BlockPIDSTianjin.TileEntityBlockPIDSTianjin
    {
        public TileEntityPSDGlassScreenTianjin(BlockPos blockPos, BlockState blockState) {
            super(BlockEntityTypes.PSD_GLASS_SCREEN_TIANJIN_TILE_ENTITY.get(), blockPos, blockState);
        }
    }
}
