package argent_matter.gtcrops.data;

import argent_matter.gtcrops.registry.GTCropsRegistries;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.tterrag.registrate.providers.ProviderType;

public class GTCropsDatagen {
    public static void init() {
        GTCropsRegistries.REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
    }
}