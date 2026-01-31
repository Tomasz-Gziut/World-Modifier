package com.worldmodifier.mixin;

import com.worldmodifier.WorldModifierConfig;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to override the dimension type's min/max build height.
 * This prevents players from placing blocks below the configured bedrock level.
 */
@Mixin(DimensionType.class)
public class DimensionTypeMixin {

    /**
     * Intercepts minY() to return custom minimum Y level.
     */
    @Inject(method = "minY", at = @At("HEAD"), cancellable = true)
    private void worldmodifier$overrideMinY(CallbackInfoReturnable<Integer> cir) {
        if (!WorldModifierConfig.isFilteringActive()) {
            return;
        }

        int bedrockLevel = WorldModifierConfig.getBedrockLevel();
        // Round to nearest 16 (Minecraft requires min_y to be divisible by 16)
        int newMinY = (bedrockLevel / 16) * 16;

        // Only modify if the new min_y is higher than default -64
        if (newMinY > -64) {
            cir.setReturnValue(newMinY);
        }
    }

    /**
     * Intercepts height() to return adjusted height.
     */
    @Inject(method = "height", at = @At("HEAD"), cancellable = true)
    private void worldmodifier$overrideHeight(CallbackInfoReturnable<Integer> cir) {
        if (!WorldModifierConfig.isFilteringActive()) {
            return;
        }

        int bedrockLevel = WorldModifierConfig.getBedrockLevel();
        int newMinY = (bedrockLevel / 16) * 16;

        // Only modify if the new min_y is higher than default -64
        if (newMinY > -64) {
            // Default height is 384 (from -64 to 320)
            // New height = 320 - newMinY
            int newHeight = 320 - newMinY;
            cir.setReturnValue(newHeight);
        }
    }
}
