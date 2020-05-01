package com.iridium.iridiumskyblock.iterators;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IslandBoundingBoxIterator implements Iterator<BoundingBox> {

    private static final @NotNull Map<BlockFace, BlockFace> nextDirection = ImmutableMap.<BlockFace, BlockFace>builder()
        .put(BlockFace.NORTH, BlockFace.EAST)
        .put(BlockFace.EAST, BlockFace.SOUTH)
        .put(BlockFace.SOUTH, BlockFace.WEST)
        .put(BlockFace.WEST, BlockFace.NORTH)
        .build();

    @Getter private final @NotNull BoundingBox boundingBox;
    @Getter private @NotNull BlockFace direction;
    @Getter private int countInDirection;
    @Getter private int maxInDirection;

    private final @NotNull Map<BlockFace, Vector> vectors;

    public IslandBoundingBoxIterator(final int distance,
                                     final @NotNull BoundingBox boundingBox,
                                     final @NotNull BlockFace direction,
                                     final int countInDirection,
                                     final int maxInDirection) {
        this.boundingBox = boundingBox;
        this.direction = direction;
        this.countInDirection = countInDirection;
        this.maxInDirection = maxInDirection;
        this.vectors = Collections.unmodifiableMap(new HashMap<BlockFace, Vector>(){{
            put(BlockFace.NORTH, new Vector(distance, 0, 0));
            put(BlockFace.EAST, new Vector(0, 0, distance));
            put(BlockFace.SOUTH, new Vector(-distance, 0, 0));
            put(BlockFace.WEST, new Vector(0, 0, -distance));
        }});
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public @NotNull BoundingBox next() {
        final @NotNull Vector vector = vectors.get(direction);
        boundingBox.shift(vector);
        countInDirection++;

        if (countInDirection == maxInDirection) {
            direction = nextDirection.get(direction);
            countInDirection = 0;
            if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
                maxInDirection++;
            }
        }

        return boundingBox.clone();
    }
}
