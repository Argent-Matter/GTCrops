package argent_matter.gtcrops.api.entity;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GTCropBlockEntity extends BlockEntity {

    private int crossbreedingCooldown = 0;
    private static final int CROSSBREEDING_COOLDOWN_TICKS = 1200;
    private static final int MAX_AGE = 7;
    private static final int MIN_CROSSBREED_AGE = 3;
    private static final int MAX_GROWTH = 24;
    private static final int MAX_GAIN = 31;

    private static final BlockPos[] OFFSETS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 1)
    };

    public GTCropBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("CrossbreedingCooldown", this.crossbreedingCooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.crossbreedingCooldown = tag.getInt("CrossbreedingCooldown");
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, GTCropBlockEntity blockEntity) {
        RandomSource random = level.getRandom();
        int age = state.getValue(GTCropBlock.AGE);

        if (age < MAX_AGE) {
            float growthChance = blockEntity.getGrowthChance(state);
            if (random.nextFloat() <= growthChance) {
                level.setBlock(pos, state.setValue(GTCropBlock.AGE, age + 1), 3);
            }
        }

        blockEntity.handleWeeds(level, pos, state, random);

        if (blockEntity.crossbreedingCooldown <= 0) {
            blockEntity.attemptCrossbreeding(level, pos, state);
            blockEntity.crossbreedingCooldown = CROSSBREEDING_COOLDOWN_TICKS;
        } else {
            blockEntity.crossbreedingCooldown--;
        }
    }

    private void handleWeeds(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        int growthLevel = state.getValue(GTCropBlock.GROWTH);
        if (growthLevel >= 22) {
            float weedChance = getWeedChance(growthLevel);
            if (random.nextFloat() <= weedChance) {
                convertToWeed(level, pos);
            }
        }
    }

    private void convertToWeed(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 3);
    }

    private float getWeedChance(int growthLevel) {
        return Math.min(1.0f, 0.02f * growthLevel);
    }

    private float getGrowthChance(BlockState state) {
        int growth = state.getValue(GTCropBlock.GROWTH);
        return Math.min(1.0f, 0.01f + 0.008f * growth);
    }

    private void attemptCrossbreeding(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getValue(GTCropBlock.AGE) < MIN_CROSSBREED_AGE) {
            return;
        }

        Pair<BlockPos, GTCropBlock> neighbor = findNeighboringCropPos(level, pos);
        if (neighbor == null) {
            return;
        }

        GTCropBlock neighborCrop = neighbor.getRight();
        BlockPos neighborPos = neighbor.getLeft();

        if (level.getBlockState(neighborPos).getValue(GTCropBlock.AGE) < MIN_CROSSBREED_AGE) {
            return;
        }

        BlockPos airPos = findAirBlockBetweenCrops(pos, neighborPos);
        if (airPos == null || !level.getBlockState(airPos).isAir()) {
            return;
        }

        if (canCrossbreedWith(neighborCrop)) {
            if (state.getBlock() instanceof GTCropBlock cropBlock) {
                if (cropBlock.getCropType().equals(neighborCrop.getCropType())) {
                    createCropWithBetterStats(level, airPos, cropBlock, state);
                } else {
                    createNewCrop(level, airPos, cropBlock, neighborCrop);
                }
            }
        }
    }

    private Pair<BlockPos, GTCropBlock> findNeighboringCropPos(Level world, BlockPos pos) {
        for (BlockPos offset : OFFSETS) {
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

    private void createCropWithBetterStats(Level world, BlockPos airPos, GTCropBlock cropBlock, BlockState current) {
        BlockState currentState = world.getBlockState(airPos);
        if (!currentState.isAir()) {
            return;
        }

        Random random = new Random();
        int currentGrowth = current.getValue(GTCropBlock.GROWTH);
        int currentGain = current.getValue(GTCropBlock.GAIN);

        if (random.nextBoolean()) {
            int newGrowth = Math.min(currentGrowth + 1, MAX_GROWTH);
            world.setBlock(airPos, current.setValue(GTCropBlock.GROWTH, newGrowth), 3);
        } else {
            int newGain = Math.min(currentGain + 1, MAX_GAIN);
            world.setBlock(airPos, current.setValue(GTCropBlock.GAIN, newGain), 3);
        }
    }

    private void createNewCrop(Level world, BlockPos airPos, GTCropBlock crop1, GTCropBlock crop2) {
        BlockState soilState = world.getBlockState(airPos.below());
        if (!soilState.is(Blocks.FARMLAND)) {
            return;
        }

        BlockState currentState = world.getBlockState(airPos);
        if (!currentState.isAir()) {
            return;
        }

        Random random = new Random();
        GTCropBlock newCrop;
        int parentTier = Math.max(crop1.getCropType().getTier(), crop2.getCropType().getTier()); // Use getTier()

        if (random.nextFloat() < 0.5) {
            newCrop = getRandomNewCrop(parentTier);
        } else {
            newCrop = random.nextBoolean() ? crop1 : crop2;
        }

        world.setBlock(airPos, newCrop.defaultBlockState().setValue(GTCropBlock.GROWTH, 1).setValue(GTCropBlock.GAIN, 1), 3);
    }

    private GTCropBlock getRandomNewCrop(int parentTier) {
        List<GTCropBlock> availableCropBlocks = new ArrayList<>();

        for (CropType cropType : GTCropsRegistries.CROP_TYPES.values()) {
            if (cropType.getTier() <= parentTier) {
                availableCropBlocks.add((GTCropBlock) GTCropsBlocks.CROP_BLOCKS.get(cropType).get());
            }
        }

        Random random = new Random();
        return availableCropBlocks.get(random.nextInt(availableCropBlocks.size()));
    }

    private boolean canCrossbreedWith(GTCropBlock neighborCrop) {
        GTCropBlock thisCrop = (GTCropBlock) this.getBlockState().getBlock();
        int thisTier = thisCrop.getCropType().getTier();
        int neighborTier = neighborCrop.getCropType().getTier();

        return thisTier == neighborTier || (thisTier == 1 && neighborTier <= 2) || (thisTier == 2 && neighborTier == 1);
    }
}
