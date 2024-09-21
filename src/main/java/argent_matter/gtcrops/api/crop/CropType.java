package argent_matter.gtcrops.api.crop;

import argent_matter.gtcrops.api.block.GTCropBlock;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class CropType {

    @Getter
    private final ResourceLocation id;
    @Getter
    private final ResourceLocation baseModelPath;
    @Getter
    private final int tintColor;
    @Getter
    private final int tier;
    @Getter
    private final int defaultGrowth;
    @Getter
    private final int defaultGain;
    @Getter
    private final BiConsumer<GTCropBlock, LootTable.Builder> loot;
    @Getter
    private final BiFunction<CropType, BlockBehaviour.Properties, ? extends GTCropBlock> createFunction; // Add @Getter here
    @Getter
    private final MutableComponent name;

    private final Item seedItem;
    private final Item primaryDrop;

    public CropType(ResourceLocation id, @Nullable ResourceLocation baseModelPath, int tintColor, int tier,
                    int defaultGrowth, int defaultGain,
                    BiConsumer<GTCropBlock, LootTable.Builder> loot,
                    BiFunction<CropType, BlockBehaviour.Properties, ? extends GTCropBlock> createFunction,
                    Item seedItem, Item primaryDrop) {
        this.id = id;
        this.baseModelPath = baseModelPath;
        this.tintColor = tintColor;
        this.tier = tier;
        this.defaultGrowth = defaultGrowth;
        this.defaultGain = defaultGain;
        this.loot = loot;
        this.createFunction = createFunction;
        this.seedItem = seedItem;
        this.primaryDrop = primaryDrop;
        this.name = Component.translatable(getNameId());
    }

    public String getNameId() {
        return id.toLanguageKey("crop");
    }

    public ItemStack getSeedItem() {
        return new ItemStack(seedItem);
    }

    public ItemStack getPrimaryDrop(int gain) {
        return new ItemStack(primaryDrop, gain);
    }

    public GTCropBlock createBlock(BlockBehaviour.Properties properties) {
        return createFunction.apply(this, properties);
    }

    public void applyLoot(GTCropBlock cropBlock, LootTable.Builder builder) {
        loot.accept(cropBlock, builder);
    }
}
