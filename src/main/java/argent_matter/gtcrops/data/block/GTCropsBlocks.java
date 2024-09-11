package argent_matter.gtcrops.data.block;

import argent_matter.gtcrops.api.block.GTCropBlock;
import argent_matter.gtcrops.api.block.WeedsBlock;
import argent_matter.gtcrops.api.crop.CropType;
import argent_matter.gtcrops.api.item.CropSeedItem;
import argent_matter.gtcrops.api.registry.GTCropsRegistries;
import argent_matter.gtcrops.data.creativetab.GTCropsCreativeModeTabs;
import com.google.common.collect.ImmutableMap;
import com.gregtechceu.gtceu.api.addon.AddonFinder;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Map;

import static argent_matter.gtcrops.api.registry.GTCropsRegistries.REGISTRATE;

public class GTCropsBlocks {

    static {
        REGISTRATE.creativeModeTab(GTCropsCreativeModeTabs.GT_CROPS);
    }

    public static Map<CropType, BlockEntry<GTCropBlock>> CROP_BLOCKS;

    private static void createCrops() {
        var cropBlocksBuilder = new ImmutableMap.Builder<CropType, BlockEntry<GTCropBlock>>();
        for (CropType type : GTCropsRegistries.CROP_TYPES) {
            ResourceLocation id = type.id();
            IGTAddon addon = AddonFinder.getAddon(id.getNamespace());
            if (addon == null) {
                continue;
            }
            BlockEntry<GTCropBlock> block = addon.getRegistrate().block(id.getPath() + "_crop", p -> (GTCropBlock) type.createFunction().apply(type, p))
                    .properties(p -> p.noCollission()
                            .instabreak()
                            .mapColor(MapColor.PLANT)
                            .randomTicks()
                            .sound(SoundType.CROP)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable())
                    .setData(ProviderType.LANG, NonNullBiConsumer.noop())
                    .setData(ProviderType.BLOCKSTATE, NonNullBiConsumer.noop())
                    .setData(ProviderType.LOOT, NonNullBiConsumer.noop())
                    .addLayer(() -> RenderType::cutoutMipped)
                    .color(() -> () -> (state, level, pos, index) -> type.tintColor())
                    .item((b, p) -> new CropSeedItem(b, type, p))
                    .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop())
                    .build()
                    .register();
            cropBlocksBuilder.put(type, block);
        }
        CROP_BLOCKS = cropBlocksBuilder.build();
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
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().crop(ctx.getName(), prov.blockTexture(ctx.getEntry()))))
            .register();

    public static void init() {
        createCrops();
    }
}
