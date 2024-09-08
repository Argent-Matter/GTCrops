package argent_matter.gtcrops.api.entity;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GTCropBlockEntity extends BlockEntity {


    }



            }
        }
    }

            if (random.nextFloat() <= weedChance) {
                convertToWeed(level, pos);
            }
        }
    }

    private void convertToWeed(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, GTCropsBlocks.WEEDS.getDefaultState(), 3);
    }

    }

    }
}
