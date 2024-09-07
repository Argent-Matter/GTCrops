package argent_matter.gtcrops.data.creativetab;

import argent_matter.gtcrops.GTCrops;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import argent_matter.gtcrops.data.crop.GTCropsCrops;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTab;

import static argent_matter.gtcrops.api.registry.GTCropsRegistries.REGISTRATE;

public class GTCropsCreativeModeTabs {

public static RegistryEntry<CreativeModeTab> GT_CROPS = REGISTRATE.defaultCreativeTab("gt_crops",
                builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("gt_crops", REGISTRATE))
                        .icon(() -> GTCropsBlocks.CROP_BLOCKS.get(GTCropsCrops.BEETROOT).asStack())
                        .title(REGISTRATE.addLang("itemGroup", GTCrops.id("gt_crops"),
                                GTCrops.NAME))
                        .build())
        .register();
}
