package argent_matter.gtcrops.data.crop;

import argent_matter.gtcrops.GTCrops;
import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.common.data.GTItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class GTCropsCrops {

    static {
        GTCropsRegistries.CROP_TYPES.unfreeze();
    }
    
    public static final CropType WHEAT = register("wheat", 0xcdb159, 1, () -> Items.WHEAT);
    public static final CropType CARROT = register("carrot", 0xe38a1d, 1, () -> Items.CARROT);
    public static final CropType POTATO = register("potato", 0xc8a24b, 1, () -> Items.POTATO);
    public static final CropType BEETROOT = register("beetroot", 0xbf2529, 1, () -> Items.BEETROOT);
    public static final CropType IRON = register("iron", 0xdcdcdc, 2, () -> Items.IRON_INGOT);
    public static final CropType CANE = register("sugar_cane", "sugar_cane", 2, () -> Items.SUGAR_CANE);
    public static final CropType RABBITBUSH = register("rabbitbush", "rabbitbush", 4, () -> GTItems.STICKY_RESIN);

    private static CropType register(String id, int color, int tier, Supplier<ItemLike> drop) {
        return register(GTCrops.id(id), null, color, tier, 1, 1,
                (block, loot) -> loot.withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(drop.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .when(new LootItemBlockStatePropertyCondition.Builder(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(GTCropBlock.AGE, GTCropBlock.MAX_AGE)))),
                GTCropBlock::new);
    }

    private static CropType register(String id, String baseModelPath, int tier, Supplier<ItemLike> drop) {
        return register(GTCrops.id(id), GTCrops.id("block/" + baseModelPath), -1, tier, 1, 1,
                (block, loot) -> loot.withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(drop.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .when(new LootItemBlockStatePropertyCondition.Builder(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(GTCropBlock.AGE, GTCropBlock.MAX_AGE)))),
                GTCropBlock::new);
    }

    public static CropType register(ResourceLocation id, @Nullable ResourceLocation baseModelPath, int tintColor,
                                    int tier, int defaultGrowth, int defaultGain,
                                    BiConsumer<GTCropBlock, LootTable.Builder> loot,
                                    BiFunction<CropType, BlockBehaviour.Properties, ? extends GTCropBlock> createFunction) {
        CropType type = new CropType(id, baseModelPath, tintColor, tier, defaultGrowth, defaultGain, loot, createFunction);
        GTCropsRegistries.CROP_TYPES.register(id, type);
        return type;
    }

    public static void init() {
        ModLoader.get().postEvent(new GTCEuAPI.RegisterEvent<>(GTCropsRegistries.CROP_TYPES, CropType.class));
        GTCropsRegistries.CROP_TYPES.freeze();
    }
}
