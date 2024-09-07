package argent_matter.gtcrops.data.datagen.loot;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import com.gregtechceu.gtceu.core.mixins.BlockBehaviourAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Map;
import java.util.function.Supplier;

public class GTCropsLoot {

    public static void addLoot(final Map<ResourceLocation, LootTable> lootTables, VanillaBlockLoot loot) {
        for (Supplier<GTCropBlock> supplier : GTCropsBlocks.CROP_BLOCKS.values()) {
            GTCropBlock block = supplier.get();
            CropType type = block.getCropType();
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);

            ResourceLocation lootTableId = id.withPrefix("block/");

            LootPool.Builder seedsDrop = LootPool.lootPool()
                    .add(LootItem.lootTableItem(block.asItem())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))));
            LootTable.Builder builder = LootTable.lootTable()
                    .withPool(seedsDrop);
            type.loot().accept(block, builder);
            lootTables.put(lootTableId, builder.setParamSet(LootContextParamSets.BLOCK).build());
            ((BlockBehaviourAccessor) block).setDrops(lootTableId);
        }
    }
}
