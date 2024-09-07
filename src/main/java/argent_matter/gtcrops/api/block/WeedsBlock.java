package argent_matter.gtcrops.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class WeedsBlock extends Block {

    public static final IntegerProperty INFESTATION_LEVEL = IntegerProperty.create("infestation_level", 0, 3);

    public WeedsBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(INFESTATION_LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INFESTATION_LEVEL);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int infestationLevel = state.getValue(INFESTATION_LEVEL);
        if (infestationLevel < 3) {
            level.setBlock(pos, state.setValue(INFESTATION_LEVEL, infestationLevel + 1), 2);
        } else {
            spreadInfestation(level, pos);
        }
    }

    private void spreadInfestation(Level world, BlockPos pos) {
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
            BlockState nearbyState = world.getBlockState(nearbyPos);
            if (nearbyState.getBlock() instanceof GTCropBlock) {
                world.setBlock(nearbyPos, Blocks.DIRT.defaultBlockState(), 2);
            }
        }
    }
}
