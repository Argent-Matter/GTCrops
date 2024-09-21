package argent_matter.gtcrops.data.datagen;

import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import argent_matter.gtcrops.data.datagen.lang.LangHandler;
import argent_matter.gtcrops.data.datagen.model.GTCropsModels;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GTCropsDatagen {

    public static void init() {
        GTCropsRegistries.REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);

        GTCropsRegistries.REGISTRATE.addDataGenerator(ProviderType.BLOCKSTATE, generator -> GTCropsModels.generateCropModels());
    }
}
