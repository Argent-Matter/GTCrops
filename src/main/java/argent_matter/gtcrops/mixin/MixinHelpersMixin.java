package argent_matter.gtcrops.mixin;
import argent_matter.gtcrops.data.datagen.loot.GTCropsLoot;
import com.gregtechceu.gtceu.core.MixinHelpers;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = MixinHelpers.class, remap = false)
public class MixinHelpersMixin {

    @Shadow @Final private static VanillaBlockLoot BLOCK_LOOT;

    @Inject(method = "generateGTDynamicLoot", at = @At("HEAD"))
    private static void gtcrops$injectLoot(Map<ResourceLocation, LootTable> lootTables, CallbackInfo ci) {
        GTCropsLoot.addLoot(lootTables, BLOCK_LOOT);
    }
}
