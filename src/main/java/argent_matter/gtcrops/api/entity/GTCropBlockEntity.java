package argent_matter.gtcrops.api.entity;

import argent_matter.gtcrops.api.block.GTCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GTCropBlockEntity extends BlockEntity {

    private int growth;
    private int gain;
    private static final int MAX_AGE = 7;
    private static final int MAX_GROWTH = 24;
    private static final int MAX_GAIN = 31;

    public GTCropBlockEntity(BlockPos pos, BlockState state) {
        super(CropBlockEntity.GT_CROP_BLOCK_ENTITY.get(), pos, state);
        this.growth = 1;
        this.gain = 1;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Growth", this.growth);
        tag.putInt("Gain", this.gain);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.growth = tag.getInt("Growth");
        this.gain = tag.getInt("Gain");
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, GTCropBlockEntity blockEntity) {
        RandomSource random = level.getRandom();
        int age = state.getValue(GTCropBlock.AGE);


        if (age < MAX_AGE) {
            float growthChance = blockEntity.getGrowthChance();
            if (random.nextFloat() <= growthChance) {
                level.setBlock(pos, state.setValue(GTCropBlock.AGE, age + 1), 3);
            }
        } else {

        }

        blockEntity.handleWeeds(level, pos, random);
    }

    private void handleWeeds(ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.growth >= 22) {
            float weedChance = getWeedChance();
            if (random.nextFloat() <= weedChance) {
                convertToWeed(level, pos);
            }
        }
    }

    private void convertToWeed(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
    }

    private float getWeedChance() {
        return Math.min(1.0f, 0.02f * this.growth);
    }

    private float getGrowthChance() {
        return Math.min(1.0f, 0.01f + 0.008f * this.growth);
    }
}
