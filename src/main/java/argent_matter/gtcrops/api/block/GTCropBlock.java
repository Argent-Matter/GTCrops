package argent_matter.gtcrops.api.block;

import argent_matter.gtcrops.api.entity.GTCropBlockEntity;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.data.blockentity.GTCropsBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GTCropBlock extends Block implements EntityBlock {

    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    @Getter
    private final CropType cropType;

    private static final VoxelShape[] AGE_SHAPES = new VoxelShape[]{
            Block.box(2, 0, 2, 14, 2, 14),
            Block.box(2, 0, 2, 14, 4, 14),
            Block.box(2, 0, 2, 14, 6, 14),
            Block.box(2, 0, 2, 14, 8, 14),
            Block.box(2, 0, 2, 14, 10, 14),
            Block.box(2, 0, 2, 14, 12, 14),
            Block.box(2, 0, 2, 14, 14, 14),
            Block.box(2, 0, 2, 14, 16, 14)
    };

    public GTCropBlock(CropType cropType, Properties properties) {
        super(properties);
        this.cropType = cropType;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public String getDescriptionId() {
        return "block.gtcrops.crop";
    }

    @Override
    public MutableComponent getName() {
        return cropType.getName();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GTCropBlockEntity(GTCropsBlockEntities.CROP.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, blockState, t) -> {
            if (t instanceof GTCropBlockEntity gtCropBlockEntity && lvl instanceof ServerLevel serverLevel) {
                GTCropBlockEntity.tick(serverLevel, pos, blockState, gtCropBlockEntity);
            }
        };
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GTCropBlockEntity gtCropBlockEntity) {
            if (canSurvive(state, level, pos)) {
                GTCropBlockEntity.tick(level, pos, state, gtCropBlockEntity);
            } else {
                level.destroyBlock(pos, true);
            }
        }
    }

    public boolean canSurvive(BlockState state, Level level, BlockPos pos) {
        BlockState soil = level.getBlockState(pos.below());
        return soil.is(Blocks.FARMLAND);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState soil = context.getLevel().getBlockState(context.getClickedPos().below());
        return soil.is(Blocks.FARMLAND) ? this.defaultBlockState() : null;
    }

    public void incrementAge(Level level, BlockPos pos, BlockState state) {
        int currentAge = state.getValue(AGE);
        if (currentAge < MAX_AGE) {
            level.setBlock(pos, state.setValue(AGE, currentAge + 1), 3);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return AGE_SHAPES[state.getValue(AGE)];
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            int age = state.getValue(AGE);
            if (age >= MAX_AGE) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof GTCropBlockEntity cropEntity) {
                    int dropCount = cropEntity.getGain();
                    Item dropItem = cropEntity.getCropType().dropSupplier().get().asItem();
                    for (int i = 0; i < dropCount; i++) {
                        ItemStack stack = new ItemStack(dropItem);
                        ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                        level.addFreshEntity(itemEntity);
                    }

                    level.setBlock(pos, state.setValue(AGE, 0), 3);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof GTCropBlockEntity cropEntity) {
                Item seedItem = this.asItem();
                ItemStack seedStack = new ItemStack(seedItem);

                CompoundTag tag = seedStack.getOrCreateTag();
                tag.putInt("growth", cropEntity.getGrowth());
                tag.putInt("gain", cropEntity.getGain());
                tag.putInt("resistance", cropEntity.getResistance());

                Block.popResource(level, pos, seedStack);
            }
        }
    }
}
