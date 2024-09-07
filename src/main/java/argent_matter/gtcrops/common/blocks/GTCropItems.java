package argent_matter.gtcrops.common.blocks;

import argent_matter.gtcrops.GTCrops;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class GTCropItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GTCrops.MOD_ID);

    public static final Map<String, RegistryObject<Item>> SEEDS = new HashMap<>();

    static {
        for (String cropTypeName : new String[]{"wheat", "carrot", "potato", "beetroot", "cane", "goo_cane"}) {
            RegistryObject<Block> cropBlock = switch (cropTypeName) {
                case "wheat" -> BlocksRegistry.WHEAT_CROP;
                case "carrot" -> BlocksRegistry.CARROT_CROP;
                case "potato" -> BlocksRegistry.POTATO_CROP;
                case "beetroot" -> BlocksRegistry.BEETROOT_CROP;
                case "cane" -> BlocksRegistry.CANE_CROP;
                case "goo_cane" -> BlocksRegistry.GOO_CANE_CROP;
                default -> throw new IllegalArgumentException("Unknown crop type: " + cropTypeName);
            };

            CropType cropType = CropType.fromName(cropTypeName);  // Győződj meg róla, hogy létezik a CropType.fromName() metódus
            SEEDS.put(cropTypeName, ITEMS.register(cropTypeName + "_seed",
                    () -> new CropSeedItem(cropBlock.get(), cropType, new Item.Properties())));
        }
    }
}
