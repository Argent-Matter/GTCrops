package argent_matter.gtcrops.api.registry;

import argent_matter.gtcrops.GTCrops;
import argent_matter.gtcrops.api.crop.CropType;
import com.gregtechceu.gtceu.api.registry.GTRegistry;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

public class GTCropsRegistries {

    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GTCrops.MOD_ID);

    public static final GTRegistry.RL<CropType> CROP_TYPES = new GTRegistry.RL<>(GTCrops.id("crop_type"));
}
