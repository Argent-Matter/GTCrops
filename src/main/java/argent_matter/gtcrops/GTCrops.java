package argent_matter.gtcrops;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import argent_matter.gtcrops.data.block.GTCropsBlocks;
import argent_matter.gtcrops.data.crop.GTCropsCrops;
import argent_matter.gtcrops.data.datagen.GTCropsDatagen;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.DimensionMarker;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.registry.MaterialRegistry;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.common.registry.GTRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod(GTCrops.MOD_ID)
public class GTCrops {

    public static final String MOD_ID = "gtcrops";
    public static final String NAME = "GregTech Crops";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
    public static MaterialRegistry MATERIAL_REGISTRY;

    public GTCrops() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        GTCrops.init();

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(this);
        bus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        bus.addGenericListener(Class.class, this::registerRecipeConditions);
        bus.addGenericListener(MachineDefinition.class, this::registerMachines);
        bus.addGenericListener(DimensionMarker.class, this::registerDimensionMarkers);
    }

    public static void init() {
        GTCropsCrops.init();
        GTCropsBlocks.init();

        // fabric exclusive, squeeze this in here to register before stuff is used
        GTCropsRegistries.REGISTRATE.registerRegistrate();

        GTCropsDatagen.init();
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
