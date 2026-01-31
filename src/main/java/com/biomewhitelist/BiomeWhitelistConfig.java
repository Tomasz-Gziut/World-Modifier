package com.biomewhitelist;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Configuration for biome whitelist.
 *
 * Contract:
 * - whitelistedBiomes: List of biome resource locations (e.g., "minecraft:plains")
 * - enabled: Master toggle for the mod functionality
 * - Non-whitelisted biomes are replaced with a random biome from the whitelist
 *
 * Invariant: If whitelist is empty and enabled=true, ALL biomes are allowed (no filtering).
 */
public class BiomeWhitelistConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELISTED_BIOMES;
    public static final ForgeConfigSpec.IntValue SEA_LEVEL;

    // Cached set for fast lookup - rebuilt when config reloads
    private static Set<ResourceLocation> whitelistCache = Collections.emptySet();
    private static List<ResourceLocation> whitelistList = Collections.emptyList();

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Biome Whitelist Configuration");
        builder.push("general");

        ENABLED = builder
                .comment("Enable biome whitelist filtering. When false, world generates normally.")
                .define("enabled", true);

        WHITELISTED_BIOMES = builder
                .comment(
                        "List of biomes that are allowed to generate.",
                        "Use full resource locations like 'minecraft:plains' or 'modid:custom_biome'.",
                        "If empty, all biomes are allowed (whitelist disabled).",
                        "",
                        "Common vanilla biomes:",
                        "  minecraft:plains, minecraft:forest, minecraft:desert,",
                        "  minecraft:taiga, minecraft:swamp, minecraft:jungle,",
                        "  minecraft:snowy_plains, minecraft:ocean, minecraft:river,",
                        "  minecraft:beach, minecraft:mountains (now minecraft:stony_peaks),",
                        "  minecraft:savanna, minecraft:badlands, minecraft:dark_forest,",
                        "  minecraft:birch_forest, minecraft:flower_forest,",
                        "  minecraft:meadow, minecraft:grove, minecraft:snowy_slopes,",
                        "  minecraft:frozen_peaks, minecraft:jagged_peaks,",
                        "  minecraft:cherry_grove, minecraft:deep_dark",
                        "",
                        "Example for a plains-only world: [\"minecraft:plains\", \"minecraft:river\", \"minecraft:ocean\"]"
                )
                .defineListAllowEmpty(
                        List.of("whitelistedBiomes"),
                        () -> List.of(
                                "minecraft:ocean",
                                "minecraft:deep_ocean",
                                "minecraft:warm_ocean",
                                "minecraft:lukewarm_ocean",
                                "minecraft:deep_lukewarm_ocean",
                                "minecraft:cold_ocean",
                                "minecraft:deep_cold_ocean",
                                "minecraft:frozen_ocean",
                                "minecraft:deep_frozen_ocean"
                        ),
                        obj -> obj instanceof String s && ResourceLocation.tryParse(s) != null
                );

        SEA_LEVEL = builder
                .comment(
                        "Custom sea level for world generation.",
                        "Default Minecraft sea level is 63.",
                        "Higher values = more water, lower values = less water.",
                        "Note: This affects where water generates, not terrain height.",
                        "Range: 0-320"
                )
                .defineInRange("seaLevel", 100, 0, 320);

        builder.pop();
        SPEC = builder.build();
    }

    /**
     * Rebuilds the whitelist cache from config values.
     * Called after config load/reload.
     */
    public static void rebuildCache() {
        Set<ResourceLocation> newCache = new HashSet<>();
        List<ResourceLocation> newList = new ArrayList<>();
        for (String biome : WHITELISTED_BIOMES.get()) {
            ResourceLocation loc = ResourceLocation.tryParse(biome);
            if (loc != null) {
                newCache.add(loc);
                newList.add(loc);
            } else {
                BiomeWhitelist.LOGGER.warn("[BiomeWhitelistConfig.rebuildCache]: Invalid biome resource location: {}", biome);
            }
        }
        whitelistCache = Collections.unmodifiableSet(newCache);
        whitelistList = Collections.unmodifiableList(newList);
        BiomeWhitelist.LOGGER.info("[BiomeWhitelistConfig.rebuildCache]: Whitelist contains {} biomes, sea level: {}",
                whitelistCache.size(), SEA_LEVEL.get());
    }

    /**
     * @return true if the mod filtering is enabled and whitelist is non-empty
     */
    public static boolean isFilteringActive() {
        return ENABLED.get() && !whitelistCache.isEmpty();
    }

    /**
     * @return unmodifiable set of whitelisted biome resource locations
     */
    public static Set<ResourceLocation> getWhitelistedBiomes() {
        return whitelistCache;
    }

    /**
     * @param biome the biome to check
     * @return true if biome is in whitelist (or whitelist is empty/disabled)
     */
    public static boolean isBiomeAllowed(ResourceLocation biome) {
        if (!isFilteringActive()) {
            return true;
        }
        return whitelistCache.contains(biome);
    }

    /**
     * Gets the first biome from the whitelist as a fallback replacement.
     * This is used when the original biome isn't in the whitelist.
     *
     * @return the first biome from the whitelist
     */
    public static ResourceLocation getFirstWhitelistedBiome() {
        if (whitelistList.isEmpty()) {
            return new ResourceLocation("minecraft", "plains");
        }
        return whitelistList.get(0);
    }

    /**
     * @return the configured sea level
     */
    public static int getSeaLevel() {
        return SEA_LEVEL.get();
    }
}
