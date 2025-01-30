package argent_matter.gtcrops.api.block;

import argent_matter.gtcrops.api.entity.GTCropBlockEntity;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.data.blockentity.GTCropsBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GTCropBlock extends CropBlock implements EntityBlock {

    public static final int MAX_AGE = 7;

    @Getter
    private final CropType cropType;

    public GTCropBlock(CropType cropType, Properties properties) {
        super(properties);
        this.cropType = cropType;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0));
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
}
