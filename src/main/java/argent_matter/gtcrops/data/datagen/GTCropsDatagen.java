package argent_matter.gtcrops.data.datagen;

import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import argent_matter.gtcrops.data.datagen.lang.LangHandler;
import com.tterrag.registrate.providers.ProviderType;

public class GTCropsDatagen {
    public static void init() {
        GTCropsRegistries.REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
    }
}
