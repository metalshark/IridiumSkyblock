package com.iridium.iridiumskyblock.iterators;

import com.iridium.iridiumskyblock.Direction;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.Config;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IslandLocationIterator implements Iterator<Location> {
    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final int distance = config.distance;
    private static final @NotNull Map<Direction, int[]> directionOffsets = Collections.unmodifiableMap(new HashMap<Direction, int[]>(){{
        put(Direction.NORTH, new int[]{distance, 0, 0});
        put(Direction.EAST, new int[]{0, 0, distance});
        put(Direction.SOUTH, new int[]{-distance, 0, 0});
        put(Direction.WEST, new int[]{0, 0, -distance});
    }});

    @Getter private final @NotNull Location location;
    @Getter private @NotNull Direction direction;
    @Getter private int countInDirection;
    @Getter private int maxInDirection;

    public IslandLocationIterator(@NotNull Location location, @NotNull Direction direction, int countInDirection, int maxInDirection) {
        this.location = location;
        this.direction = direction;
        this.countInDirection = countInDirection;
        this.maxInDirection = maxInDirection;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public @NotNull Location next() {
        final @NotNull int[] directionOffset = directionOffsets.get(direction);
        location.add(directionOffset[0], directionOffset[1], directionOffset[2]);
        countInDirection++;

        if (countInDirection == maxInDirection) {
            direction = direction.next();
            countInDirection = 0;
            if (direction == Direction.SOUTH || direction == Direction.NORTH) {
                maxInDirection++;
            }
        }

        return location;
    }
}
