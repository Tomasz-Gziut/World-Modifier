package com.worldmodifier.mixin;

import com.worldmodifier.WorldModifierConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to place bedrock at the new world bottom.
 */
@Mixin(NoiseBasedChunkGenerator.class)
public class BedrockMixin {

    private static final BlockState BEDROCK = Blocks.BEDROCK.defaultBlockState();

    /**
     * Injects after applyCarvers to place bedrock at the bottom of the world.
     */
    @Inject(method = "applyCarvers", at = @At("TAIL"))
    private void worldmodifier$placeBedrockFloor(WorldGenRegion region,
                                                  long seed,
                                                  RandomState randomState,
                                                  net.minecraft.world.level.biome.BiomeManager biomeManager,
                                                  StructureManager structureManager,
                                                  ChunkAccess chunk,
                                                  GenerationStep.Carving carving,
                                                  CallbackInfo ci) {
        // Only run after the AIR carving step (the last one)
        if (carving != GenerationStep.Carving.AIR) {
            return;
        }

        int bedrockLevel = WorldModifierConfig.getBedrockLevel();
        int minY = Math.floorDiv(bedrockLevel, 16) * 16; // Round down to 16
        int chunkX = chunk.getPos().getMinBlockX();
        int chunkZ = chunk.getPos().getMinBlockZ();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Place flat bedrock layer at the bottom
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                pos.set(chunkX + x, minY, chunkZ + z);
                chunk.setBlockState(pos, BEDROCK, false);
            }
        }
    }
}
