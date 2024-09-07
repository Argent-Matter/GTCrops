package argent_matter.gtcrops.mixin;

import argent_matter.gtcrops.data.datagen.model.GTCropsModels;
import com.gregtechceu.gtceu.common.data.GTModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GTModels.class, remap = false)
public class GTModelsMixin {

    @Inject(method = "registerMaterialFluidModels", at = @At("HEAD"))
    private static void gtcrops$loadCropModels(CallbackInfo ci) {
        GTCropsModels.generateCropModels();
    }
}
