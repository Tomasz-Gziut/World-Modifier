package com.worldmodifier.mixin;

import com.worldmodifier.WorldModifierConfig;
import net.minecraft.world.level.levelgen.NoiseSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to override the noise settings to change the minimum Y level.
 * This affects terrain generation boundaries.
 */
@Mixin(NoiseSettings.class)
public class NoiseGeneratorSettingsMixin {

    /**
     * Intercepts minY() to return custom minimum Y level.
     */
    @Inject(method = "minY", at = @At("HEAD"), cancellable = true)
    private void worldmodifier$overrideMinY(CallbackInfoReturnable<Integer> cir) {
        int bedrockLevel = WorldModifierConfig.getBedrockLevel();
        // Round down to nearest 16 (Minecraft requires min_y to be divisible by 16)
        int newMinY = Math.floorDiv(bedrockLevel, 16) * 16;
        cir.setReturnValue(newMinY);
    }

    /**
     * Intercepts height() to return adjusted height.
     * Height = maxY - minY (must be divisible by 16)
     */
    @Inject(method = "height", at = @At("HEAD"), cancellable = true)
    private void worldmodifier$overrideHeight(CallbackInfoReturnable<Integer> cir) {
        int bedrockLevel = WorldModifierConfig.getBedrockLevel();
        int maxHeight = WorldModifierConfig.getMaxHeight();

        // Round minY down to nearest 16 (Minecraft requires divisible by 16)
        int newMinY = Math.floorDiv(bedrockLevel, 16) * 16;
        // Round maxHeight up to nearest 16 (Minecraft requires divisible by 16)
        int roundedMaxHeight = ((maxHeight + 15) / 16) * 16;

        // Height = maxY - minY
        int newHeight = roundedMaxHeight - newMinY;
        cir.setReturnValue(newHeight);
    }
}
