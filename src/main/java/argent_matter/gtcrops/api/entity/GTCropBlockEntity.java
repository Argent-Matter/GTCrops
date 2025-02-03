package argent_matter.gtcrops.api.entity;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class GTCropBlockEntity extends BlockEntity {

    @Getter
    private int growth;
    @Getter
    private int gain;
    @Getter
    private int resistance;

    private int crossbreedingCooldown = 0;

    private static final int CROSSBREEDING_COOLDOWN_TICKS = 1200;
    private static final int MAX_AGE = 7;
    private static final int MIN_CROSSBREED_AGE = 3;

    public static final int MAX_GROWTH = 35;
    public static final int MAX_GAIN = 31;
    public static final int MAX_RESISTANCE = 31;

    public GTCropBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.growth = 1;
        this.gain = 1;
        this.resistance = 1;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GTCropBlockEntity blockEntity) {
        if (level.isClientSide) return;
        blockEntity.handleGrowth(level, pos, state);
        if (blockEntity.crossbreedingCooldown <= 0) {
            blockEntity.handleCrossbreeding((ServerLevel) level, pos, state);
            blockEntity.crossbreedingCooldown = CROSSBREEDING_COOLDOWN_TICKS;
        } else {
            blockEntity.crossbreedingCooldown--;
        }
    }

    private void handleGrowth(Level level, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.getRandom();
            handleRandomTick(state, serverLevel, pos, random);
        }
    }

    public void handleRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        if (random.nextFloat() <= getGrowthChance()) {
            incrementAge(state, level, pos);
        }
    }

    private boolean canSurvive(Level level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(Blocks.FARMLAND);
    }

    private float getGrowthChance() {
        return 0.04f + (this.growth - 1) * 0.002f;
    }

    private void incrementAge(BlockState state, Level level, BlockPos pos) {
        int currentAge = state.getValue(GTCropBlock.AGE);
        if (currentAge < MAX_AGE) {
            level.setBlock(pos, state.setValue(GTCropBlock.AGE, currentAge + 1), 3);
        } else {
            checkForWeeds(level, pos);
        }
    }

    private float getWeedChance() {
        CropType type = ((GTCropBlock)getBlockState().getBlock()).getCropType();
        int tier = type.tier();
        float baseChance;
        if (this.growth < 30) {
            baseChance = 0.005f;
        } else {
            baseChance = 0.10f + (this.growth - 30) * (0.02f * tier);
        }
        float reduction = 0.01f + (this.resistance - 1) * 0.025f;
        reduction = Math.min(reduction, 0.80f);
        float finalChance = baseChance - reduction;
        return Math.max(0, finalChance);
    }

    private void checkForWeeds(Level level, BlockPos pos) {
        RandomSource random = level.getRandom();
        float weedChance = getWeedChance();
        if (random.nextFloat() < weedChance) {
            level.setBlock(pos, GTCropsBlocks.WEEDS.get().defaultBlockState(), 3);
        }
    }

    private void handleCrossbreeding(ServerLevel level, BlockPos pos, BlockState state) {
        int age = state.getValue(GTCropBlock.AGE);
        if (age < MIN_CROSSBREED_AGE) return;
        RandomSource random = level.getRandom();
        boolean bred = false;
        int[][] inlineOffsets = { {2, 0}, {-2, 0}, {0, 2}, {0, -2} };
        for (int[] off : inlineOffsets) {
            int dx = off[0], dz = off[1];
            BlockPos emptyPos = pos.offset(dx / 2, 0, dz / 2);
            if (!level.isEmptyBlock(emptyPos)) continue;
            BlockPos partnerPos = pos.offset(dx, 0, dz);
            BlockState partnerState = level.getBlockState(partnerPos);
            Block partnerBlock = partnerState.getBlock();
            if (!(partnerBlock instanceof GTCropBlock)) continue;
            BlockEntity partnerBE = level.getBlockEntity(partnerPos);
            if (!(partnerBE instanceof GTCropBlockEntity)) continue;
            int partnerAge = partnerState.getValue(GTCropBlock.AGE);
            if (partnerAge < MIN_CROSSBREED_AGE) continue;
            bred = true;
            breedWithPartner(random, level, emptyPos, this, (GTCropBlockEntity) partnerBE);
            break;
        }
        if (!bred) {
            int[][] diagonalOffsets = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };
            for (int[] off : diagonalOffsets) {
                int dx = off[0], dz = off[1];
                BlockPos partnerPos = pos.offset(dx, 0, dz);
                BlockState partnerState = level.getBlockState(partnerPos);
                Block partnerBlock = partnerState.getBlock();
                if (!(partnerBlock instanceof GTCropBlock)) continue;
                BlockEntity partnerBE = level.getBlockEntity(partnerPos);
                if (!(partnerBE instanceof GTCropBlockEntity)) continue;
                int partnerAge = partnerState.getValue(GTCropBlock.AGE);
                if (partnerAge < MIN_CROSSBREED_AGE) continue;
                BlockPos pos2 = pos.offset(dx, 0, 0);
                BlockPos pos3 = pos.offset(0, 0, dz);
                List<BlockPos> candidates = new ArrayList<>();
                if (level.isEmptyBlock(pos2)) candidates.add(pos2);
                if (level.isEmptyBlock(pos3)) candidates.add(pos3);
                if (candidates.isEmpty()) continue;
                BlockPos emptyPos = candidates.get(random.nextInt(candidates.size()));
                bred = true;
                breedWithPartner(random, level, emptyPos, this, (GTCropBlockEntity) partnerBE);
                break;
            }
        }
    }

    public void harvest(ServerLevel level, BlockPos pos, Player player) {
        RandomSource random = level.getRandom();
        int drops = 1;
        for (int i = 1; i < gain; i++) {
            if (random.nextFloat() < 0.75f) {
                drops++;
            }
        }
        Block cropBlock = getBlockState().getBlock();
        ItemStack produce = new ItemStack(cropBlock.asItem(), drops);
        ItemEntity dropEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, produce);
        level.addFreshEntity(dropEntity);
        level.setBlock(pos, getBlockState().setValue(GTCropBlock.AGE, 0), 3);
    }

    private void breedWithPartner(RandomSource random, ServerLevel level, BlockPos emptyPos,
                                  GTCropBlockEntity parent1, GTCropBlockEntity parent2) {
        CropType type1 = parent1.getCropType();
        CropType type2 = parent2.getCropType();

        CropType offspringType;
        int offspringGrowth, offspringGain, offspringResistance;

        if (type1.equals(type2)) {
            offspringType = type1;
            offspringGrowth = (parent1.growth + parent2.growth) / 2;
            offspringGain = (parent1.gain + parent2.gain) / 2;
            offspringResistance = (parent1.resistance + parent2.resistance) / 2;
            if (random.nextFloat() < 0.10f) {
                offspringType = getMutationCropType(type1, random);
            } else if (random.nextBoolean()) {
                int statToIncrease = random.nextInt(3);
                if (statToIncrease == 0) {
                    offspringGrowth++;
                } else if (statToIncrease == 1) {
                    offspringGain++;
                } else {
                    offspringResistance++;
                }
            }
        } else {
            offspringType = random.nextBoolean() ? type1 : type2;
            offspringGrowth = Math.max(parent1.growth, parent2.growth);
            offspringGain = Math.max(parent1.gain, parent2.gain);
            offspringResistance = Math.max(parent1.resistance, parent2.resistance);
            if (random.nextBoolean()) {
                int statToIncrease = random.nextInt(3);
                if (statToIncrease == 0) {
                    offspringGrowth++;
                } else if (statToIncrease == 1) {
                    offspringGain++;
                } else {
                    offspringResistance++;
                }
            }
        }

        offspringGrowth = Math.min(offspringGrowth, MAX_GROWTH);
        offspringGain = Math.min(offspringGain, MAX_GAIN);
        offspringResistance = Math.min(offspringResistance, MAX_RESISTANCE);

        Block cropBlock = GTCropsBlocks.CROP_BLOCKS.get(offspringType).get();
        BlockState offspringState = cropBlock.defaultBlockState().setValue(GTCropBlock.AGE, 0);
        level.setBlock(emptyPos, offspringState, 3);

        BlockEntity offspringBE = level.getBlockEntity(emptyPos);
        if (offspringBE instanceof GTCropBlockEntity newCrop) {
            newCrop.growth = offspringGrowth;
            newCrop.gain = offspringGain;
            newCrop.resistance = offspringResistance;
        }
    }

    private CropType getMutationCropType(CropType parentType, RandomSource random) {
        List<CropType> candidates = new ArrayList<>();
        for (CropType type : GTCropsRegistries.CROP_TYPES) {
            if (!type.equals(parentType) && type.tier() == parentType.tier()) {
                candidates.add(type);
            }
        }
        return candidates.isEmpty() ? parentType : candidates.get(random.nextInt(candidates.size()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.growth = tag.getInt("growth");
        this.gain = tag.getInt("gain");
        this.resistance = tag.getInt("resistance");
        this.crossbreedingCooldown = tag.getInt("crossbreedingCooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("growth", growth);
        tag.putInt("gain", gain);
        tag.putInt("resistance", resistance);
        tag.putInt("crossbreedingCooldown", crossbreedingCooldown);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("growth", growth);
        tag.putInt("gain", gain);
        tag.putInt("resistance", resistance);
        return tag;
    }

    public CropType getCropType() {
        return ((GTCropBlock) getBlockState().getBlock()).getCropType();
    }
}
