package argent_matter.gtcrops.api.item;

import argent_matter.gtcrops.api.crop.CropType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class CropSeedItem extends ItemNameBlockItem {
    private final CropType cropType;

    public CropSeedItem(Block block, CropType cropType, Item.Properties properties) {
        super(block, properties);
        this.cropType = cropType;
    }

    @Override
    public String getDescriptionId() {
        return "item.gtcrops.crop_seeds";
    }

    @Override
    public Component getDescription() {
        return Component.translatable(getDescriptionId(), cropType.getName());
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(), cropType.getName());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.translatable("tooltip.gtcrops.growth", cropType.getDefaultGrowth()));
        tooltip.add(Component.translatable("tooltip.gtcrops.gain", cropType.getDefaultGain()));
        tooltip.add(Component.translatable("tooltip.gtcrops.tier", cropType.getTier()));
    }
}
