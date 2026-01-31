package com.worldmodifier.mixin;

import com.worldmodifier.WorldModifierConfig;
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
    private void worldmodifier$overrideSeaLevel(CallbackInfoReturnable<Integer> cir) {
        if (WorldModifierConfig.isFilteringActive()) {
            cir.setReturnValue(WorldModifierConfig.getSeaLevel());
        }
    }
}
