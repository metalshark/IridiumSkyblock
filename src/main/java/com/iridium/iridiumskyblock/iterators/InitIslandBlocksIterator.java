package com.iridium.iridiumskyblock.iterators;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class InitIslandBlocksIterator implements Iterator<Long> {
    private @NotNull final Config config = IridiumSkyblock.getConfiguration();
    private @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private @NotNull final IridiumSkyblock plugin = IridiumSkyblock.getInstance();

    private @NotNull final Island island;

    private final double islandMinX;
    private final double islandMinZ;
    private final double islandMaxX;
    private final double islandMaxZ;

    private @NotNull World currentWorld;
    private double currentX;
    private double currentY;
    private double currentZ;

    private final double maxWorldHeight;

    private long currentBlock = 0;

    public InitIslandBlocksIterator(@NotNull Island island) {
        this.island = island;

        final @NotNull Location pos1 = island.getPos1();
        islandMinX = pos1.getX();
        islandMinZ = pos1.getZ();

        final @NotNull Location pos2 = island.getPos2();
        islandMaxX = pos2.getX();
        islandMaxZ = pos2.getZ();

        currentWorld = Objects.requireNonNull(islandManager.getWorld());
        maxWorldHeight = currentWorld.getMaxHeight();

        currentX = islandMinX;
        currentY = 0;
        currentZ = islandMinZ;
    }

    @Override
    public boolean hasNext() {
        return currentX < islandMaxX
                || currentZ < islandMaxZ
                || currentY < maxWorldHeight
                || (config.netherIslands && currentWorld.getName().equals(config.worldName));
    }

    @Override
    public @NotNull Long next() {
        if (currentX < islandMaxX) {
            currentX++;
        } else if (currentZ < islandMaxZ) {
            currentX = islandMinX;
            currentZ++;
        } else if (currentY < maxWorldHeight) {
            currentX = islandMinX;
            currentZ = islandMinZ;
            currentY++;
        } else if (config.netherIslands && currentWorld.getName().equals(config.worldName)) {
            currentWorld = Objects.requireNonNull(islandManager.getNetherWorld());
            currentX = islandMinX;
            currentY = 0;
            currentZ = islandMinZ;
        } else {
            throw new NoSuchElementException();
        }

        if (plugin.isUpdatingBlocks()) {
            final @NotNull Location location = new Location(currentWorld, currentX, currentY, currentZ);
            final @NotNull Block block = location.getBlock();
            if (Utils.isBlockValuable(block) && !(block.getState() instanceof CreatureSpawner))
                island.addTempValue(location);
        }

        return currentBlock++;
    }
}
