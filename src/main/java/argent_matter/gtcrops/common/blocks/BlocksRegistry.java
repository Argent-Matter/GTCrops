package argent_matter.gtcrops.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlocksRegistry {
    // default state
    public static final DeferredRegister<Block> CROP_BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS, "gtcrops");

    // tier 1 cropok
    public static final RegistryObject<Block> WHEAT_CROP = CROP_BLOCK.register("wheat",
            () -> new GTCropBlock(CropType.WHEAT, BlockBehaviour.Properties.of().noCollission().instabreak()));

    public static final RegistryObject<Block> POTATO_CROP = CROP_BLOCK.register("potato",
            () -> new GTCropBlock(CropType.POTATO, BlockBehaviour.Properties.of().noCollission().instabreak()));

    public static final RegistryObject<Block> CARROT_CROP = CROP_BLOCK.register("garrot",
            () -> new GTCropBlock(CropType.CARROT, BlockBehaviour.Properties.of().noCollission().instabreak()));

    public static final RegistryObject<Block> BEETROOT_CROP = CROP_BLOCK.register("beetroot",
            () -> new GTCropBlock(CropType.BEETROOT, BlockBehaviour.Properties.of().noCollission().instabreak()));

    // tier 2
    public static final RegistryObject<Block> CANE_CROP = CROP_BLOCK.register("cane",
            () -> new GTCropBlock(CropType.CANE, BlockBehaviour.Properties.of().noCollission().instabreak()));

    // tier 4
    public static final RegistryObject<Block> GOO_CANE_CROP = CROP_BLOCK.register("goo_cane",
            () -> new GTCropBlock(CropType.GOO_CANE, BlockBehaviour.Properties.of().noCollission().instabreak()));

    }
