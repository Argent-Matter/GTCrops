package argent_matter.gtcrops.api.block;

import argent_matter.gtcrops.api.entity.GTCropBlockEntity;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.data.blockentity.GTCropsBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GTCropBlock extends Block implements EntityBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    public static final int MAX_AGE = 7;
    public static final IntegerProperty GROWTH = IntegerProperty.create("growth", 1, 24);
    public static final IntegerProperty GAIN = IntegerProperty.create("gain", 1, 31);

    private static final VoxelShape[] SHAPES_BY_AGE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    @Getter
    private final CropType cropType;

    public GTCropBlock(CropType cropType, Properties properties) {
        super(properties);
        this.cropType = cropType;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(GROWTH, cropType.getDefaultGrowth())
                .setValue(GAIN, cropType.getDefaultGain()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, GROWTH, GAIN);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GTCropBlockEntity(GTCropsBlockEntities.CROP.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof GTCropBlockEntity gtCropBlockEntity && lvl instanceof ServerLevel serverLevel) {
                    GTCropBlockEntity.tick(serverLevel, pos, blockState, gtCropBlockEntity);
                }
            };
        }
        return null;
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

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPES_BY_AGE[state.getValue(AGE)];
    }

    @Override
    public SoundType getSoundType(BlockState state) {
        return SoundType.CROP;
    }

    // Handle right-click (harvest)
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(AGE) >= MAX_AGE) {
            ItemStack primaryDrop = cropType.getPrimaryDrop(state.getValue(GAIN));
            popResource(level, pos, primaryDrop);

            level.setBlock(pos, state.setValue(AGE, 0), 2);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            return;
        }

        RandomSource random = level.getRandom();
        if (random.nextFloat() < 0.8f) {
            ItemStack seedDrop = cropType.getSeedItem();
            popResource(level, pos, seedDrop);
        }

        level.removeBlock(pos, false);
    }
}
