package com.iridium.iridiumskyblock.iterators;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class IslandChunkIterator implements Iterator<Chunk> {

    @NotNull private final Iterator<World> worldsIterator;
    @NotNull private World world;

    @NotNull private final IslandChunkKeyIterator chunkKeyIterator;

    public IslandChunkIterator(@NotNull Island island) {
        @NotNull final Config config = IridiumSkyblock.getConfiguration();
        @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();

        @NotNull final Set<World> worlds = new HashSet<>();
        @NotNull final World islandWorld = Objects.requireNonNull(islandManager.getWorld());
        worlds.add(islandWorld);
        if (config.netherIslands) {
            final World netherIslandWorld = islandManager.getNetherWorld();
            worlds.add(netherIslandWorld);
        }
        worldsIterator = worlds.iterator();
        world = worldsIterator.next();

        chunkKeyIterator = new IslandChunkKeyIterator(island);
    }

    @Override
    public boolean hasNext() {
        return chunkKeyIterator.hasNext() || worldsIterator.hasNext();
    }

    @Override
    @NotNull public Chunk next() {
        int[] chunkKey;
        try {
            chunkKey = chunkKeyIterator.next();
        } catch (NoSuchElementException ignored) {
            world = worldsIterator.next();
            chunkKeyIterator.init();
            chunkKey = chunkKeyIterator.next();
        }
        final int x = chunkKey[0];
        final int z = chunkKey[1];
        return world.getChunkAt(x, z);
    }

}
