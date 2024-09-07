package argent_matter.gtcrops.data.datagen.lang;

import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import com.tterrag.registrate.providers.RegistrateLangProvider;

import static com.gregtechceu.gtceu.utils.FormattingUtil.toEnglishName;

public class LangHandler extends com.gregtechceu.gtceu.data.lang.LangHandler {

    public static void init(RegistrateLangProvider provider) {
        // Names
        for (CropType type : GTCropsRegistries.CROP_TYPES) {
            provider.add(type.id().toLanguageKey("crop"), toEnglishName(type.id().getPath()));
        }
        provider.add("block.gtcrops.crop", "%s Crop");
        provider.add("item.gtcrops.crop_seeds", "%s Seeds");

        // tooltips
        provider.add("tooltip.gtcrops.growth", "Growth: %s");
        provider.add("tooltip.gtcrops.gain", "Gain: %s");
        provider.add("tooltip.gtcrops.tier", "Tier: %s");
    }
}
