package com.iridium.iridiumskyblock.iterators;

import com.iridium.iridiumskyblock.Island;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IslandChunkKeyIterator implements Iterator<int[]> {

    private final int minChunkX;
    private final int minChunkZ;
    private final int maxChunkX;
    private final int maxChunkZ;

    private int x;
    private int z;

    public IslandChunkKeyIterator(@NotNull Island island) {
        @NotNull final Location pos1 = island.getPos1();
        @NotNull final Chunk pos1Chunk = pos1.getChunk();
        minChunkX = pos1Chunk.getX();
        minChunkZ = pos1Chunk.getZ();

        @NotNull final Location pos2 = island.getPos2();
        @NotNull final Chunk pos2Chunk = pos2.getChunk();
        maxChunkX = pos2Chunk.getX();
        maxChunkZ = pos2Chunk.getX();

        init();
    }

    public void init() {
        x = minChunkX - 1;
        z = minChunkZ;
    }

    @Override
    public boolean hasNext() {
        return x < maxChunkX || z < maxChunkZ;
    }

    @Override
    @NotNull public int[] next() {
        if (x < maxChunkX) {
            x++;
        } else if (z < maxChunkZ) {
            z++;
            x = minChunkX;
        } else {
            throw new NoSuchElementException();
        }
        return new int[]{x, z};
    }

}
