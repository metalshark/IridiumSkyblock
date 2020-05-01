package com.iridium.iridiumskyblock.iterators;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class BoundingBoxBlockIterator implements Iterator<Block> {

    private final @NotNull World world;

    private final int minX;
    private final int minY;

    private final int maxX;
    private final int maxY;
    private final int maxZ;

    private int nextX;
    private int nextY;
    private int nextZ;

    public BoundingBoxBlockIterator(final @NotNull World world,
                                    final @NotNull BoundingBox boundingBox) {
        this.world = world;

        minX = (int) boundingBox.getMinX();
        minY = (int) boundingBox.getMinY();

        maxX = (int) boundingBox.getMaxX();
        maxY = (int) boundingBox.getMaxY();
        maxZ = (int) boundingBox.getMaxZ();

        nextX = minX;
        nextY = minY;
        nextZ = (int) boundingBox.getMinZ();
    }

    @Override
    public boolean hasNext() {
        return !(nextX == maxX && nextY == maxY && nextZ == maxZ);
    }

    @Override
    public Block next() {
        final @NotNull Block block = world.getBlockAt(nextX, nextY, nextZ);

        nextX++;
        if (nextX <= maxX) return block;

        nextX = minX;
        nextY++;
        if (nextY <= maxY) return block;

        nextY = minY;
        nextZ++;
        if (nextZ <= maxZ) return block;

        throw new java.util.NoSuchElementException();
    }

}
