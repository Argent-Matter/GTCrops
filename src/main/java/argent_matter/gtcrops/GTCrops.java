package argent_matter.gtcrops;

import argent_matter.gtcrops.common.blocks.BlocksRegistry;
import argent_matter.gtcrops.common.blocks.GTCropBlock;
import argent_matter.gtcrops.common.blocks.GTCropItems;
import argent_matter.gtcrops.common.data.forge.GTCropsDataGenerator;
import argent_matter.gtcrops.data.GTCropsDatagen;
import argent_matter.gtcrops.registry.GTCropsRegistries;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.DimensionMarker;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.registry.MaterialRegistry;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GTCrops.MOD_ID)
public class GTCrops {
    public static final String
            MOD_ID = "gtcrops",
            NAME = "Gregtech Crops";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
    public static MaterialRegistry MATERIAL_REGISTRY;

    public GTCrops() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        GTCrops.init();
        GTCropsDatagen.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlocksRegistry.CROP_BLOCK.register(modEventBus);
        GTCropItems.ITEMS.register(modEventBus);
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(this);

        bus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        bus.addGenericListener(Class.class, this::registerRecipeConditions);
        bus.addGenericListener(MachineDefinition.class, this::registerMachines);
        bus.addGenericListener(DimensionMarker.class, this::registerDimensionMarkers);
    }

    public static void init() {
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @SubscribeEvent
    public void registerMaterialRegistry(MaterialRegistryEvent event) {
        MATERIAL_REGISTRY = GTCEuAPI.materialManager.createRegistry(GTCrops.MOD_ID);
    }

    @SubscribeEvent
    public void registerMaterials(MaterialEvent event) {
    }

    @SubscribeEvent
    public void modifyMaterials(PostMaterialEvent event) {
    }

    public void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
    }

    public void registerRecipeConditions(GTCEuAPI.RegisterEvent<String, Class<? extends RecipeCondition>> event) {
    }

    public void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
    }

    public void registerDimensionMarkers(GTCEuAPI.RegisterEvent<ResourceLocation, DimensionMarker> event) {
    }
}