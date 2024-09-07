package argent_matter.gtcrops.api.crop;

import argent_matter.gtcrops.api.block.GTCropBlock;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Crop type interface.
 * TODO better API documentation
 */
public record CropType(ResourceLocation id, @Nullable ResourceLocation baseModelPath, int tintColor, int tier,
                       int defaultGrowth, int defaultGain, BiConsumer<GTCropBlock, LootTable.Builder> loot,
                       BiFunction<CropType, BlockBehaviour.Properties, ? extends GTCropBlock> createFunction) {

    public Component getName() {
        return Component.translatable(getNameId());
    }

    public String getNameId() {
        return id().toLanguageKey("crop");
    }
}
