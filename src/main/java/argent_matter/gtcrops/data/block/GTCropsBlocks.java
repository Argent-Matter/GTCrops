package argent_matter.gtcrops.data.block;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.block.WeedsBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.item.CropSeedItem;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import argent_matter.gtcrops.data.creativetab.GTCropsCreativeModeTabs;
import com.google.common.collect.ImmutableMap;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Map;
import java.util.Optional;

import static argent_matter.gtcrops.api.registry.GTCropsRegistries.REGISTRATE;

public class GTCropsBlocks {

    public static Map<CropType, BlockEntry<GTCropBlock>> CROP_BLOCKS;

    static {
        REGISTRATE.creativeModeTab(GTCropsCreativeModeTabs.GT_CROPS);
    }

    private static void createCrops() {
        var cropBlocksBuilder = new ImmutableMap.Builder<CropType, BlockEntry<GTCropBlock>>();
        for (CropType type : GTCropsRegistries.CROP_TYPES) {
            ResourceLocation id = type.id();
            BlockEntry<GTCropBlock> block = REGISTRATE.block(id.getPath() + "_crop", p -> (GTCropBlock) type.createFunction().apply(type, p))
                    .properties(p -> p.noCollission()
                            .instabreak()
                            .mapColor(MapColor.PLANT)
                            .randomTicks()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .color(() -> () -> (state, level, pos, index) -> type.tintColor())
                    .item((b, p) -> new CropSeedItem(b, type, p))
                    .build()
                    .register();
            cropBlocksBuilder.put(type, block);
        }
        CROP_BLOCKS = cropBlocksBuilder.build();
    }

    public static CropType getCropTypeForBlock(Block block) {
        return CROP_BLOCKS.entrySet().stream()
                .filter(entry -> entry.getValue().get() == block)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No crop type found for block: " + block));
    }

    public static final BlockEntry<WeedsBlock> WEEDS = REGISTRATE.block("weeds", WeedsBlock::new)
            .properties(p -> p.noCollission()
                    .instabreak()
                    .mapColor(MapColor.PLANT)
                    .randomTicks()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)
                    .noLootTable())
            .addLayer(() -> RenderType::cutoutMipped)
            .register();

    public static void init() {
        createCrops();
    }
}
