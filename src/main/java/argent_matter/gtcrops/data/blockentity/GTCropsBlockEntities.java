package argent_matter.gtcrops.data.blockentity;

import argent_matter.gtcrops.api.entity.GTCropBlockEntity;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import com.gregtechceu.gtceu.common.blockentity.CableBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;

import static argent_matter.gtcrops.api.registry.GTCropsRegistries.REGISTRATE;

public class GTCropsBlockEntities {

    @SuppressWarnings("unchecked")
    public static final BlockEntityEntry<CableBlockEntity> CROP = REGISTRATE
            .blockEntity("crop", GTCropBlockEntity::new)
            .validBlocks(GTCropsBlocks.CROP_BLOCKS.values().toArray(BlockEntry[]::new))
            .register();

    public static void init() {}
}
