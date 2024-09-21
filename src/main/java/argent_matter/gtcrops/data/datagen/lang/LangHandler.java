package argent_matter.gtcrops.data.datagen.lang;

import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import com.tterrag.registrate.providers.RegistrateLangProvider;

import static com.gregtechceu.gtceu.utils.FormattingUtil.toEnglishName;

public class LangHandler extends com.gregtechceu.gtceu.data.lang.LangHandler {

    public static void init(RegistrateLangProvider provider) {
        for (CropType type : GTCropsRegistries.CROP_TYPES) {
            provider.add(type.getId().toLanguageKey("crop"), toEnglishName(type.getId().getPath()));
        }
        provider.add("block.gtcrops.crop", "%s");
        provider.add("item.gtcrops.crop_seeds", "Seed Bag (%s)");

        // Tooltips
        provider.add("tooltip.gtcrops.growth", "Growth: %s");
        provider.add("tooltip.gtcrops.gain", "Gain: %s");
        provider.add("tooltip.gtcrops.tier", "Tier: %s");
    }
}
