package argent_matter.gtcrops.api.entity;

import argent_matter.gtcrops.GTCrops;
import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.entity.GTCropBlockEntity;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CropBlockEntity {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GTCrops.MOD_ID);

    public static final RegistryObject<BlockEntityType<GTCropBlockEntity>> GT_CROP_BLOCK_ENTITY = BLOCK_ENTITIES.register("crop_blockentity",
            () -> BlockEntityType.Builder.of(GTCropBlockEntity::new, GTCropsBlocks.CROP_BLOCKS.values().stream()
                    .map(BlockEntry::get).toArray(GTCropBlock[]::new)).build(null));
}
