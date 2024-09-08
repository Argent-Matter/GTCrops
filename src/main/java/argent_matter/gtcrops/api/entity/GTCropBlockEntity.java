package argent_matter.gtcrops.api.entity;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.Level;

@Getter
public class GTCropBlockEntity extends BlockEntity {

    private final CropType cropType;
    private BlockState blockState;

    public GTCropBlockEntity(BlockPos pos, BlockState state, CropType cropType) {
        super(CropBlockEntity.CROP_BLOCK_ENTITY.get(), pos, state);
        this.blockState = state;
        this.cropType = cropType;
    }

    public void randomTick(ServerLevel level, BlockPos pos, RandomSource random) {
        int growth = blockState.getValue(GTCropBlock.GROWTH);
        int age = blockState.getValue(GTCropBlock.AGE);

        if (age >= 3) {
            if (random.nextFloat() <= 0.7f) {
                var neighbor = findNeighboringCropPos(level, pos);
                if (neighbor != null) {
                    BlockPos airPos = findAirBlockBetweenCrops(pos, neighbor.getFirst());
                    GTCropBlockEntity neighborEntity = (GTCropBlockEntity) level.getBlockEntity(neighbor.getFirst());

                    if (airPos != null && canCrossbreedWith(neighborEntity)) {
                        if (this.cropType.tier() == neighborEntity.getCropType().tier()) {
                            createCropWithBetterStats(level, airPos, neighborEntity.getBlockState());
                        } else if (this.cropType.tier() < 2) {
                            createNewCrop(level, airPos, neighborEntity.getBlockState());
                        }
                    }
                }
            }
        }

        if (growth >= 22) {
            float weedChance = getWeedChance(growth);
            if (random.nextFloat() <= weedChance) {
                convertToWeed(level, pos);
                return;
            }
        }

        if (level.getRawBrightness(pos, 0) >= 9) {
            if (age < 7) {
                float growthChance = getGrowthChance(growth);
                if (random.nextFloat() <= growthChance) {
                    blockState = blockState.setValue(GTCropBlock.AGE, age + 1);
                    level.setBlock(pos, blockState, 3);
                }
            }
        }
    }

    private float getWeedChance(int growth) {
        return switch (growth) {
            case 22 -> 0.25f;
            case 23 -> 0.50f;
            case 24 -> 0.75f;
            default -> 0.0f;
        };
    }

    private float getGrowthChance(int growth) {
        return Math.min(1.0f, 0.01f + 0.008f * growth);
    }

    private void convertToWeed(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, GTCropsBlocks.WEEDS.getDefaultState(), 3);
    }

    private void createCropWithBetterStats(ServerLevel level, BlockPos airPos, BlockState neighborState) {
        if (!level.getBlockState(airPos).isAir()) return;

        int growth = neighborState.getValue(GTCropBlock.GROWTH);
        int gain = neighborState.getValue(GTCropBlock.GAIN);

        RandomSource random = RandomSource.create();
        BlockState newState = random.nextBoolean()
                ? neighborState.setValue(GTCropBlock.GROWTH, Math.min(growth + 1, 24))
                : neighborState.setValue(GTCropBlock.GAIN, Math.min(gain + 1, 31));

        level.setBlock(airPos, newState, 3);
    }

    private void createNewCrop(ServerLevel level, BlockPos pos, BlockState neighborState) {
        if (!level.getBlockState(pos).isAir()) return;

        BlockState newState = neighborState.setValue(GTCropBlock.GROWTH, 1).setValue(GTCropBlock.GAIN, 1);
        level.setBlock(pos, newState, 3);
    }

    private boolean canCrossbreedWith(GTCropBlockEntity neighborEntity) {
        return this.cropType.tier() == neighborEntity.getCropType().tier()
                || (this.cropType.tier() == 1 && neighborEntity.getCropType().tier() <= 2);
    }

    private Pair<BlockPos, GTCropBlock> findNeighboringCropPos(Level world, BlockPos pos) {
        for (BlockPos offset : GTCropBlock.OFFSETS) {
            BlockPos neighborPos = pos.offset(offset);
            if (world.getBlockState(neighborPos).getBlock() instanceof GTCropBlock cropBlock) {
                return Pair.of(neighborPos, cropBlock);
            }
        }
        return null;
    }

    private BlockPos findAirBlockBetweenCrops(BlockPos pos1, BlockPos pos2) {
        int midX = (pos1.getX() + pos2.getX()) / 2;
        int midY = (pos1.getY() + pos2.getY()) / 2;
        int midZ = (pos1.getZ() + pos2.getZ()) / 2;

        BlockPos midPos = new BlockPos(midX, midY, midZ);
        if (pos1.distSqr(midPos) == 1 && pos2.distSqr(midPos) == 1) {
            return midPos;
        }
        return null;
    }
}
