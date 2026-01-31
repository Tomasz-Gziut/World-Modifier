package com.biomewhitelist.mixin;

import com.biomewhitelist.BiomeWhitelistConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to override aquifer water generation to use custom sea level.
 * Targets the NoiseBasedAquifer inner class to intercept fluid computation.
 */
@Mixin(Aquifer.NoiseBasedAquifer.class)
public class AquiferMixin {

    private static final BlockState WATER = Blocks.WATER.defaultBlockState();

    /**
     * Intercepts the computeSubstance method to force water generation below custom sea level.
     * This method is called for every block during chunk generation to determine what block should be placed.
     */
    @Inject(method = "computeSubstance", at = @At("HEAD"), cancellable = true)
    private void biomewhitelist$overrideFluidLevel(DensityFunction.FunctionContext context, double density,
                                                    CallbackInfoReturnable<BlockState> cir) {
        if (!BiomeWhitelistConfig.isFilteringActive()) {
            return;
        }

        int y = context.blockY();
        int seaLevel = BiomeWhitelistConfig.getSeaLevel();

        // If we're below sea level and the density indicates this should be air/fluid (not solid)
        // density > 0 means solid block, density <= 0 means air or fluid
        if (y < seaLevel && density <= 0) {
            cir.setReturnValue(WATER);
        }
    }
}
