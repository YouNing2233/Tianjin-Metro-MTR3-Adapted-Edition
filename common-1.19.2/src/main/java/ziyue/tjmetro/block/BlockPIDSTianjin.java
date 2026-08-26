package ziyue.tjmetro.block;

import mtr.block.BlockPIDSBaseHorizontal;
import mtr.block.IBlock;
import mtr.data.IPIDS;
import mtr.Items;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ziyue.tjmetro.BlockEntityTypes;
import ziyue.tjmetro.BlockList;
import ziyue.tjmetro.ItemList;
import ziyue.tjmetro.client.ClientCache;
import ziyue.tjmetro.client.ScrollingText;
import ziyue.tjmetro.packet.PacketGuiServer;

import java.util.HashMap;
import java.util.Map;

public class BlockPIDSTianjin extends BlockPIDSBaseHorizontal {

    public static final Map<Long, Category> CATEGORIES = new HashMap<>();

    public BlockPIDSTianjin() {
        super();
    }

    public boolean canStoreData(BlockGetter world, BlockPos pos) {
        final Direction facing = IBlock.getStatePropertySafe(world, pos, net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
        return facing == Direction.NORTH || facing == Direction.EAST;
    }

    public BlockPos getBlockPosWithData(BlockGetter world, BlockPos pos) {
        if (canStoreData(world, pos)) {
            return pos;
        } else {
            return pos.relative(IBlock.getStatePropertySafe(world, pos, net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBlockPIDSTianjin(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, net.minecraft.world.level.Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final BlockPos dataPos = getBlockPosWithData(world, pos);
        final net.minecraft.world.level.block.entity.BlockEntity be = world.getBlockEntity(dataPos);
        if (!(be instanceof TileEntityBlockPIDSTianjin entity)) return InteractionResult.PASS;

        if (player.isHolding(Items.BRUSH.get()) || player.isHolding(ItemList.WRENCH.get())) {
            if (!world.isClientSide) {
                entity.setChanged();
                PacketGuiServer.openPIDSTianjinScreenS2C((net.minecraft.server.level.ServerPlayer) player, dataPos);
            }
            return InteractionResult.SUCCESS;
        }

        if (entity.advertisement != null && entity.advertisement.url() != null) {
            if (!world.isClientSide) {
                final String url = entity.advertisement.url();
                final net.minecraft.network.chat.MutableComponent link = net.minecraft.network.chat.Component.literal(url)
                        .setStyle(net.minecraft.network.chat.Style.EMPTY
                                .withClickEvent(new net.minecraft.network.chat.ClickEvent(net.minecraft.network.chat.ClickEvent.Action.OPEN_URL, url))
                                .withUnderlined(true));
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("chat.tjmetro.ad_link", link));
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static class TileEntityBlockPIDSTianjin extends IPIDS.TileEntityPIDS {

        protected final LongAdderList categories = new LongAdderList();
        protected int categoryIndex;
        protected int advertisementIndex;
        public BlockPIDSTianjin.Advertisement advertisement;
        public final ScrollingText scrollingText;
        protected boolean useRealTime = true;

        public static final String CATEGORIES_ID = "categories";
        public static final String CATEGORY_INDEX_ID = "category_index";
        public static final String ADVERTISEMENT_INDEX_ID = "advertisement_index";
        public static final String USE_REAL_TIME_ID = "use_real_time";

        public TileEntityBlockPIDSTianjin(BlockPos pos, BlockState state) {
            this(BlockEntityTypes.PIDS_TIANJIN_TILE_ENTITY.get(), pos, state);
        }

        /**
         * Constructor for subclasses (e.g. the single-row variant) that need their own
         * registered block entity type.
         */
        protected TileEntityBlockPIDSTianjin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
            categories.add("tjmetro".hashCode());
            this.scrollingText = new ScrollingText(158F, 47F, 4, true);
        }

        @Override
        public int getMaxArrivals() {
            return 2;
        }

        @Override
        public int getLinesPerArrival() {
            return 1;
        }

        @Override
        public void setData(String[] messages, boolean[] hideArrival, java.util.Set<Long> platformIds, int displayPage) {
            super.setData(messages, hideArrival, platformIds, displayPage);
            setChanged();
            syncData();
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            super.readCompoundTag(compoundTag);
            categories.clear();
            final long[] categoriesArray = compoundTag.getLongArray(CATEGORIES_ID);
            for (final long category : categoriesArray) {
                categories.add(category);
            }
            categoryIndex = compoundTag.getInt(CATEGORY_INDEX_ID);
            advertisementIndex = compoundTag.getInt(ADVERTISEMENT_INDEX_ID);
            if (compoundTag.contains(USE_REAL_TIME_ID)) {
                useRealTime = compoundTag.getBoolean(USE_REAL_TIME_ID);
            }
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            super.writeCompoundTag(compoundTag);
            compoundTag.putLongArray(CATEGORIES_ID, new java.util.ArrayList<>(categories));
            compoundTag.putInt(CATEGORY_INDEX_ID, categoryIndex);
            compoundTag.putInt(ADVERTISEMENT_INDEX_ID, advertisementIndex);
            compoundTag.putBoolean(USE_REAL_TIME_ID, useRealTime);
        }

        public LongAdderList getCategories() {
            return categories;
        }

        public int getCategoryIndex() {
            return categoryIndex;
        }

        public int getAdvertisementIndex() {
            return advertisementIndex;
        }

        public void setCategoryIndex(int index) {
            this.categoryIndex = index;
            setChanged();
            syncData();
        }

        public void setAdvertisementIndex(int index) {
            this.advertisementIndex = index;
            setChanged();
            syncData();
        }

        public void setCategories(LongAdderList newCategories) {
            this.categories.clear();
            this.categories.addAll(newCategories);
            setChanged();
            syncData();
        }

        public boolean isUseRealTime() {
            return useRealTime;
        }

        public void setUseRealTime(boolean useRealTime) {
            this.useRealTime = useRealTime;
            setChanged();
            syncData();
        }

        public void setData(String[] messages, boolean[] hideArrival, java.util.Set<Long> platformIds, int displayPage, boolean useRealTime) {
            super.setData(messages, hideArrival, platformIds, displayPage);
            this.useRealTime = useRealTime;
            setChanged();
            syncData();
        }
    }

    public record Advertisement(ResourceLocation image, Component text, String url) {
    }

    public static class Category extends java.util.ArrayList<Advertisement> {
        public final long id;
        public final java.awt.Color color;
        public final Component name;
        public final Component description;

        public Category(long id, java.awt.Color color, Component name, Component description) {
            this.id = id;
            this.color = color;
            this.name = name;
            this.description = description;
        }
    }

    public static class LongAdderList extends java.util.AbstractList<Long> {
        private final java.util.ArrayList<Long> delegate = new java.util.ArrayList<>();

        public boolean add(long value) {
            return delegate.add(value);
        }

        public void addAll(LongAdderList other) {
            delegate.addAll(other.delegate);
        }

        public long getLong(int index) {
            return delegate.get(index);
        }

        @Override
        public Long get(int index) {
            return delegate.get(index);
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public void clear() {
            delegate.clear();
        }
    }
}