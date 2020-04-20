package com.iridium.iridiumskyblock.iterators;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class IslandChunkIterator implements Iterator<Chunk> {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    private final @NotNull Iterator<World> worldsIterator;
    private @NotNull World world;

    private final @NotNull IslandChunkKeyIterator chunkKeyIterator;

    public IslandChunkIterator(@NotNull Island island) {
        final @NotNull Set<World> worlds = new HashSet<World>(){{
            add(islandManager.getWorld());
        }};
        if (config.netherIslands)
            worlds.add(islandManager.getNetherWorld());
        worldsIterator = worlds.iterator();
        world = worldsIterator.next();
        chunkKeyIterator = new IslandChunkKeyIterator(island);
    }

    @Override
    public boolean hasNext() {
        return chunkKeyIterator.hasNext() || worldsIterator.hasNext();
    }

    @Override
    public @NotNull Chunk next() {
        if (!chunkKeyIterator.hasNext()) {
            world = worldsIterator.next(); // Will throw NoSuchElement exception when we run out of worlds
            chunkKeyIterator.init();
        }
        final @NotNull int[] chunkKey = chunkKeyIterator.next();
        final int x = chunkKey[0];
        final int z = chunkKey[1];
        return world.getChunkAt(x, z);
    }

}
