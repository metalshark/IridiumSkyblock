package com.iridium.iridiumskyblock.chunkgenerators;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class SkyblockGenerator extends ChunkGenerator {

    private final @NotNull Map<Environment, Biome> biomes;

    public SkyblockGenerator() {
        this.biomes =  Collections.unmodifiableMap(
            new HashMap<Environment, Biome>(){{
                put(Environment.NORMAL, Biome.PLAINS);
                put(Environment.NETHER, Biome.NETHER);
            }});
    }

    public SkyblockGenerator(final @NotNull Map<Environment, Biome> biomes) {
        this.biomes = biomes;
    }

    @Override
    public @NotNull ChunkData generateChunkData(final @NotNull World world,
                                                final @NotNull Random random,
                                                final int cx,
                                                final int cz,
                                                final @NotNull BiomeGrid biomeGrid) {
        final @NotNull ChunkData chunkData = createChunkData(world);
        final @Nullable Biome biome = biomes.get(world.getEnvironment());
        if (biome == null) return chunkData;

        IntStream.range(0, 15).forEach(x -> {
            IntStream.range(0, world.getMaxHeight()).forEach(y -> {
                IntStream.range(0, 15).forEach(z -> {
                    biomeGrid.setBiome(x, y, z, biome);
                });
            });
        });

        return chunkData;
    }

    @Override
    public boolean canSpawn(final @NotNull World world, final int x, final int z) {
        return true;
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(final @NotNull World world) {
        return Collections.emptyList();
    }
}