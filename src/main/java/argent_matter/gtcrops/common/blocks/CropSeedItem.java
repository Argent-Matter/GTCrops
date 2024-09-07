package argent_matter.gtcrops.common.blocks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class CropSeedItem extends BlockItem {
    private final CropType cropType;

    public CropSeedItem(Block block, CropType cropType, Item.Properties properties) {
        super(block, properties);
        this.cropType = cropType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (this.getBlock() instanceof GTCropBlock cropBlock) {
            tooltip.add(Component.translatable("tooltip.gtcrops.growth", cropBlock.defaultBlockState().getValue(GTCropBlock.GROWTH)));
            tooltip.add(Component.translatable("tooltip.gtcrops.gain", cropBlock.defaultBlockState().getValue(GTCropBlock.GAIN)));
            tooltip.add(Component.translatable("tooltip.gtcrops.tier", cropType.getTier()));
        }
    }
}