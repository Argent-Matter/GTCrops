package argent_matter.gtcrops.api.entity;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class CropBlockEntity {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "gtcrops");

    public static final RegistryObject<BlockEntityType<GTCropBlockEntity>> CROP_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("crop_block_entity",
                    () -> BlockEntityType.Builder.of(
                                    (pos, state) -> {
                                        Block block = state.getBlock();
                                        CropType cropType = getCropTypeForBlock(block);
                                        return new GTCropBlockEntity(pos, state, cropType);
                                    },
                                    GTCropsBlocks.CROP_BLOCKS.values().stream()
                                            .map(BlockEntry::get)
                                            .toArray(Block[]::new))
                            .build(null));

    private static CropType getCropTypeForBlock(Block block) {
        return GTCropsBlocks.CROP_BLOCKS.entrySet().stream()
                .filter(entry -> entry.getValue().get() == block)
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
