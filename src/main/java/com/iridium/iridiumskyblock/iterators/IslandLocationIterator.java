package com.iridium.iridiumskyblock.iterators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IslandLocationIterator implements Iterator<Location> {

    private static final @NotNull Map<BlockFace, BlockFace> nextDirection = Collections.unmodifiableMap(
        new HashMap<BlockFace, BlockFace>(){{
            put(BlockFace.NORTH, BlockFace.EAST);
            put(BlockFace.EAST, BlockFace.SOUTH);
            put(BlockFace.SOUTH, BlockFace.WEST);
            put(BlockFace.WEST, BlockFace.NORTH);
        }});

    @Getter private final @NotNull Location location;
    @Getter private @NotNull BlockFace direction;
    @Getter private int countInDirection;
    @Getter private int maxInDirection;

    private final @NotNull Map<BlockFace, Vector> vectors;

    public IslandLocationIterator(final int distance,
                                  final @NotNull Location location,
                                  final @NotNull BlockFace direction,
                                  final int countInDirection,
                                  final int maxInDirection) {
        this.location = location;
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
    public @NotNull Location next() {
        final @NotNull Vector vector = vectors.get(direction);
        location.add(vector);
        countInDirection++;

        if (countInDirection == maxInDirection) {
            direction = nextDirection.get(direction);
            countInDirection = 0;
            if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
                maxInDirection++;
            }
        }

        return location;
    }
}
