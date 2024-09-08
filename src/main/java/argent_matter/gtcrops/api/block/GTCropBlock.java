package argent_matter.gtcrops.api.block;

import argent_matter.gtcrops.GTCrops;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

// TODO remove debugging messages
@Getter
public class GTCropBlock extends CropBlock {
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = CropBlock.AGE;
    public static final IntegerProperty GROWTH = IntegerProperty.create("growth", 1, 24); // Growth stat max 24
    public static final IntegerProperty GAIN = IntegerProperty.create("gain", 1, 31); // Gain stat max 31

    private static final VoxelShape[] SHAPES_BY_AGE = {
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    private static final BlockPos[] OFFSETS;
    static {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        OFFSETS = new BlockPos[]{
                // cardinal directions
                pos.north(2),
                pos.south(2),
                pos.east(2),
                pos.west(2),
                // diagonal directions
                pos.move(-2, 0, -2).immutable(),
                pos.move(-2, 0, 2).immutable(),
                pos.move(2, 0, -2).immutable(),
                pos.move(2, 0, 2).immutable(),
        };
    }

    private final CropType cropType;

    public GTCropBlock(CropType cropType, BlockBehaviour.Properties properties) {
        super(properties);
        this.cropType = cropType;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(GROWTH, cropType.defaultGrowth())
                .setValue(GAIN, cropType.defaultGain()));
    }

    @Override
    public String getDescriptionId() {
        return "block.gtcrops.crop";
    }

    @Override
    public MutableComponent getName() {
        return Component.translatable("block.gtcrops.crop", cropType.getName());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, GROWTH, GAIN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES_BY_AGE[state.getValue(AGE)];
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true; // Crop growth cycles
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int growth = state.getValue(GROWTH);
        int age = state.getValue(AGE);

        if (age >= 3) {
            // 70% chance of crossbreeding - NemEzanevem (TESTING)
            // TODO change?
            if (random.nextFloat() <= 0.7f) {
                var neighbor = findNeighboringCropPos(level, pos);
                if (neighbor != null) {
                    BlockPos airPos = findAirBlockBetweenCrops(pos, neighbor.getFirst());
                    GTCropBlock neighborCrop = neighbor.getSecond();

                    if (airPos != null && canCrossbreedWith(neighborCrop)) {
                        if (this.cropType.tier() == neighborCrop.cropType.tier()) {
                            createCropWithBetterStats(level, airPos, neighborCrop, state);
                        } else if (this.cropType.tier() < 2) {
                            createNewCrop(level, airPos, this, neighborCrop);
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
                    level.setBlock(pos, state.setValue(AGE, age + 1), 3);
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

    private void createCropWithBetterStats(Level world, BlockPos airPos, GTCropBlock neighborCrop, BlockState current) {
        BlockState currentState = world.getBlockState(airPos);
        if (!currentState.isAir()) {
            GTCrops.LOGGER.info("Position is not empty at {}, current block is: {}", airPos, currentState);
            return;
        }

        Random random = new Random();
        if (random.nextBoolean()) {
            // Growth addition
            int currentGrowth = current.getValue(GROWTH);
            int newGrowth = Math.min(currentGrowth + 1, 24);
            world.setBlock(airPos, current.setValue(GROWTH, newGrowth), 3);
        } else {
            // Gain addition
            int currentGain = current.getValue(GAIN);
            int newGain = Math.min(currentGain + 1, 31);
            world.setBlock(airPos, current.setValue(GAIN, newGain), 3);
        }
        GTCrops.LOGGER.info("New crop created with better stats at {}", airPos);
    }

    private void createNewCrop(Level world, BlockPos pos, GTCropBlock crop1, GTCropBlock crop2) {
        // TODO new limitation: max Tier 2
        BlockState soilState = world.getBlockState(pos.below());
        if (!soilState.is(BlockTags.DIRT) && !soilState.is(Blocks.FARMLAND)) {
            GTCrops.LOGGER.warn("Invalid soil for crop at {}", pos);
            return;
        }

        BlockState currentState = world.getBlockState(pos);
        if (!currentState.isAir()) {
            GTCrops.LOGGER.warn("Position is not empty at {}, current block is: {}", pos, currentState);
            return;
        }

        world.setBlock(pos, crop1.defaultBlockState().setValue(GROWTH, 1).setValue(GAIN, 1), 3);
        GTCrops.LOGGER.warn("New crop created with default stats at {}", pos);
    }

    private boolean canCrossbreedWith(GTCropBlock neighborCrop) {
        return this.cropType.tier() == neighborCrop.cropType.tier()
                || (this.cropType.tier() == 1 && neighborCrop.cropType.tier() <= 2);
    }

    private void convertToWeed(Level world, BlockPos pos) {
        world.setBlock(pos, GTCropsBlocks.WEEDS.getDefaultState(), 3);
        GTCrops.LOGGER.warn("Crop at {} turned into a Weed!", pos);
    }

    private float getWeedChance(int growth) {
        return switch (growth) {
            case 22 -> 0.25f; // 25% chance
            case 23 -> 0.50f; // 50% chance
            case 24 -> 0.75f; // 75% chance
            default -> 0.0f;
        };
    }

    private float getGrowthChance(int growth) {
        return Math.min(1.0f, 0.01f + 0.008f * growth);
    }
}
