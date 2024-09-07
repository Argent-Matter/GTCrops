package argent_matter.gtcrops.common.blocks;

import net.minecraft.core.BlockPos;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GTCropBlock extends CropBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    public static final IntegerProperty GROWTH = IntegerProperty.create("growth", 1, 24); // Growth stat max 24
    public static final IntegerProperty GAIN = IntegerProperty.create("gain", 1, 31); // Gain stat max 31

    private static final Map<Integer, VoxelShape> SHAPES = new HashMap<>();
    private final CropType cropType;

    static {
        SHAPES.put(0, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D));
        SHAPES.put(1, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D));
        SHAPES.put(2, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D));
        SHAPES.put(3, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D));
        SHAPES.put(4, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D));
        SHAPES.put(5, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D));
        SHAPES.put(6, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D));
        SHAPES.put(7, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    }

    public GTCropBlock(CropType cropType, BlockBehaviour.Properties properties) {
        super(properties);
        this.cropType = cropType;

        // Minden crop 1-1 statokkal indul kibaszottul fontos
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(GROWTH, 1).setValue(GAIN, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, GROWTH, GAIN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(AGE));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true; // Crop növékedési ciklusok itt és most
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int growth = state.getValue(GROWTH);
        int age = state.getValue(AGE);

        if (age >= 3) {
            if (random.nextFloat() <= 0.7f) { // 70%-os esély a crossbreedinghez - NemEzanevem (TESTING)
                BlockPos neighborPos = findNeighboringCropPos(level, pos);
                if (neighborPos != null) {
                    BlockPos airPos = findAirBlockBetweenCrops(pos, neighborPos);
                    GTCropBlock neighborCrop = (GTCropBlock) level.getBlockState(neighborPos).getBlock();

                    if (airPos != null && canCrossbreedWith(neighborCrop)) {
                        if (this.cropType.getTier() == neighborCrop.cropType.getTier()) {
                            createCropWithBetterStats(level, airPos, neighborCrop);
                        } else if (this.cropType.getTier() < 2) {
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
                    level.setBlock(pos, state.setValue(AGE, age + 1), 2);
                }
            }
        }
    }

    private BlockPos findNeighboringCropPos(Level world, BlockPos pos) {
        for (BlockPos offset : new BlockPos[]{
                pos.north(2), pos.south(2), pos.east(2), pos.west(2),    // Egyenes irányok
                pos.north(2).west(2), pos.north(2).east(2),              // Átlós irányok
                pos.south(2).west(2), pos.south(2).east(2)
        }) {
            BlockPos neighborPos = pos.offset((offset.getX() - pos.getX()), 0, (offset.getZ() - pos.getZ()));
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof GTCropBlock) {
                return neighborPos;
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

    private void createCropWithBetterStats(Level world, BlockPos airPos, GTCropBlock neighborCrop) {
        BlockState currentState = world.getBlockState(airPos);
        if (!currentState.isAir()) {
            System.out.println("Position is not empty at " + airPos + ", current block is: " + currentState);
            return;
        }

        int currentGrowth = this.defaultBlockState().getValue(GROWTH);
        int currentGain = this.defaultBlockState().getValue(GAIN);

        Random random = new Random();
        if (random.nextBoolean()) {
            // Growth növelése
            int newGrowth = Math.min(currentGrowth + 1, 24);
            world.setBlock(airPos, this.defaultBlockState().setValue(GROWTH, newGrowth).setValue(GAIN, currentGain), 2);
        } else {
            // Gain növelése
            int newGain = Math.min(currentGain + 1, 31);
            world.setBlock(airPos, this.defaultBlockState().setValue(GROWTH, currentGrowth).setValue(GAIN, newGain), 2);
        }
        System.out.println("New crop created with better stats at " + airPos);
    }

    private void createNewCrop(Level world, BlockPos pos, GTCropBlock crop1, GTCropBlock crop2) {
        // Új crop létrehozása, max Tier 2-ig
        BlockState soilState = world.getBlockState(pos.below());
        if (!soilState.is(BlockTags.DIRT) && !soilState.is(Blocks.FARMLAND)) {
            System.out.println("Invalid soil for crop at " + pos);
            return;
        }

        BlockState currentState = world.getBlockState(pos);
        if (!currentState.isAir()) {
            System.out.println("Position is not empty at " + pos + ", current block is: " + currentState);
            return;
        }

        world.setBlock(pos, crop1.defaultBlockState().setValue(GROWTH, 1).setValue(GAIN, 1), 2);
        System.out.println("New crop created with default stats at " + pos);
    }

    private boolean canCrossbreedWith(GTCropBlock neighborCrop) {
        return this.cropType.getTier() == neighborCrop.cropType.getTier()
                || (this.cropType.getTier() == 1 && neighborCrop.cropType.getTier() <= 2);
    }

    private void convertToWeed(Level world, BlockPos pos) {
        world.setBlock(pos, new WeedBlock(BlockBehaviour.Properties.of().noCollission().instabreak()).defaultBlockState(), 2);
        System.out.println("Crop at " + pos + " turned into a Weed!");
    }

    private float getWeedChance(int growth) {
        switch (growth) {
            case 22: return 0.25f; // 25% esély
            case 23: return 0.50f; // 50% esély
            case 24: return 0.75f; // 75% esély
            default: return 0.0f;
        }
    }

    private float getGrowthChance(int growth) {
        return Math.min(1.0f, 0.01f + 0.008f * growth);
    }

    public String getCropTypeName() {
        return this.cropType.getName();
    }
}
