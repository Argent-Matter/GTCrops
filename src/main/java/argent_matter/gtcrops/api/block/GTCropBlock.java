package argent_matter.gtcrops.api.block;

import argent_matter.gtcrops.api.crop.CropType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import lombok.Getter;

@Getter
public class GTCropBlock extends CropBlock {
    public static final IntegerProperty AGE = CropBlock.AGE;
    public static final IntegerProperty GROWTH = IntegerProperty.create("growth", 1, 24);
    public static final IntegerProperty GAIN = IntegerProperty.create("gain", 1, 31);

    public static final BlockPos[] OFFSETS = {
            new BlockPos(-1, 0, -1), new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1),
            new BlockPos(0, 0, -1), new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0)
    };

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, GROWTH, GAIN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES_BY_AGE[state.getValue(AGE)];
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}
