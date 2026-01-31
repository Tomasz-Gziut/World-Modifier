package com.worldmodifier.mixin;

import com.worldmodifier.WorldModifier;
import com.worldmodifier.WorldModifierConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Mixin to intercept biome selection in MultiNoiseBiomeSource.
 *
 * Design: Intercepts the getNoiseBiome method which is called during world generation
 * to determine which biome should be placed at a given location. If the selected biome
 * is not in the whitelist, it returns a replacement biome from the whitelist based on
 * the coordinates, ensuring consistent biome regions.
 */
@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {

    @Unique
    private Map<ResourceLocation, Holder<Biome>> worldmodifier$biomeHolderCache = new HashMap<>();

    /**
     * Intercepts biome selection to filter based on whitelist.
     *
     * @param x Biome coordinate x (1/4 of block coordinate)
     * @param y Biome coordinate y (1/4 of block coordinate)
     * @param z Biome coordinate z (1/4 of block coordinate)
     * @param sampler Climate sampler for noise-based selection
     * @param cir Callback to potentially modify return value
     */
    @Inject(method = "getNoiseBiome", at = @At("RETURN"), cancellable = true)
    private void worldmodifier$filterBiome(int x, int y, int z, Climate.Sampler sampler,
                                             CallbackInfoReturnable<Holder<Biome>> cir) {
        if (!WorldModifierConfig.isFilteringActive()) {
            return;
        }

        Holder<Biome> originalBiome = cir.getReturnValue();
        if (originalBiome == null) {
            return;
        }

        // Get the biome's resource location
        Optional<ResourceKey<Biome>> keyOpt = originalBiome.unwrapKey();
        if (keyOpt.isEmpty()) {
            return;
        }

        ResourceLocation biomeId = keyOpt.get().location();
        Set<ResourceLocation> whitelist = WorldModifierConfig.getWhitelistedBiomes();

        // Check if biome is allowed
        if (whitelist.contains(biomeId)) {
            return;
        }

        // Biome not in whitelist - use fallback biome
        Holder<Biome> replacement = worldmodifier$getFallbackBiome();
        if (replacement != null) {
            cir.setReturnValue(replacement);
        }
    }

    /**
     * Gets the fallback replacement biome holder.
     * Uses caching to avoid repeated lookups.
     */
    @Unique
    private Holder<Biome> worldmodifier$getFallbackBiome() {
        ResourceLocation fallbackBiome = WorldModifierConfig.getFirstWhitelistedBiome();

        // Check cache first
        if (worldmodifier$biomeHolderCache.containsKey(fallbackBiome)) {
            return worldmodifier$biomeHolderCache.get(fallbackBiome);
        }

        // Search through the biome source's possible biomes to find the biome holder
        BiomeSource self = (BiomeSource) (Object) this;
        for (Holder<Biome> biomeHolder : self.possibleBiomes()) {
            Optional<ResourceKey<Biome>> keyOpt = biomeHolder.unwrapKey();
            if (keyOpt.isPresent() && keyOpt.get().location().equals(fallbackBiome)) {
                worldmodifier$biomeHolderCache.put(fallbackBiome, biomeHolder);
                return biomeHolder;
            }
        }

        WorldModifier.LOGGER.warn("[MultiNoiseBiomeSourceMixin.getFallbackBiome]: Biome {} not found in biome source's possible biomes", fallbackBiome);
        return null;
    }
}
