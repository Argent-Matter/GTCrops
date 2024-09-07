package argent_matter.gtcrops.common.data.forge;

import argent_matter.gtcrops.GTCrops;
import com.gregtechceu.gtceu.api.registry.registrate.SoundEntryBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        var registryAccess = event.getLookupProvider();
        if (event.includeServer()) {
            var set = Set.of(GTCrops.MOD_ID);
            generator.addProvider(true, new SoundEntryBuilder.SoundEntryProvider(generator.getPackOutput(), GTCrops.MOD_ID));
        }
    }
}