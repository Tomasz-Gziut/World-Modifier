package com.biomewhitelist.mixin;

import com.biomewhitelist.BiomeWhitelistConfig;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to override the sea level in world generation.
 */
@Mixin(NoiseBasedChunkGenerator.class)
public class SeaLevelMixin {

    @Inject(method = "getSeaLevel", at = @At("HEAD"), cancellable = true)
    private void biomewhitelist$overrideSeaLevel(CallbackInfoReturnable<Integer> cir) {
        if (BiomeWhitelistConfig.isFilteringActive()) {
            cir.setReturnValue(BiomeWhitelistConfig.getSeaLevel());
        }
    }
}
