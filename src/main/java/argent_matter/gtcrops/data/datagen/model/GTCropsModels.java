package argent_matter.gtcrops.data.datagen.model;

import argent_matter.gtcrops.GTCrops;
import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import com.gregtechceu.gtceu.data.pack.GTDynamicResourcePack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class GTCropsModels {

    private static final ModelTemplate CROP = new ModelTemplate(Optional.of(GTCrops.id("block/crop")), Optional.empty(), TextureSlot.CROP);

    public static void generateCropModels() {
        for (CropType type : GTCropsRegistries.CROP_TYPES) {
            GTCropBlock block = GTCropsBlocks.CROP_BLOCKS.get(type).get();
            ResourceLocation blockId = type.id().withSuffix("_crop");

            final ResourceLocation baseModelPath = type.baseModelPath();
            Int2ObjectMap<ResourceLocation> ageToModel = new Int2ObjectOpenHashMap<>();
            PropertyDispatch propertyDispatch;
            if (baseModelPath != null) {
                propertyDispatch = PropertyDispatch.property(GTCropBlock.AGE).generate((age) -> {
                    ResourceLocation modelLocation = ageToModel.computeIfAbsent(age, (agex) -> {
                        return baseModelPath.withSuffix("_stage" + agex);
                    });
                    return Variant.variant().with(VariantProperties.MODEL, modelLocation);
                });
            } else {
                propertyDispatch = PropertyDispatch.property(GTCropBlock.AGE).generate((age) -> {
                    ResourceLocation modelLocation = ageToModel.computeIfAbsent(age, (agex) -> {
                        return createSuffixedVariant(block, "_stage" + agex, TextureMapping::crop);
                    });
                    return Variant.variant().with(VariantProperties.MODEL, modelLocation);
                });
            }
            createSimpleFlatItemModel(block.asItem());
            GTDynamicResourcePack.addBlockState(blockId, MultiVariantGenerator.multiVariant(block).with(propertyDispatch));
        }
    }

    private static ResourceLocation createSuffixedVariant(Block block, String suffix, Function<ResourceLocation, TextureMapping> textureMappingGetter) {
        return GTCropsModels.CROP.createWithSuffix(block, suffix,
                textureMappingGetter.apply(GTCrops.id("block/default" + suffix)),
                GTDynamicResourcePack::addBlockModel);
    }

    private static void createSimpleFlatItemModel(Item flatItem) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(flatItem), TextureMapping.layer0(flatItem), GTDynamicResourcePack::addBlockModel);
    }

}
