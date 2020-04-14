package com.iridium.iridiumskyblock.iterators;

import com.iridium.iridiumskyblock.Direction;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.Config;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class IslandLocationIterator implements Iterator<Location> {
    @NotNull private final Config config = IridiumSkyblock.getConfiguration();
    private final int distance = config.distance;

    @Getter @NotNull private final Location location;
    @Getter @NotNull private Direction direction;
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
    @NotNull public Location next() {
        switch (direction) {
            case NORTH:
                location.add(distance, 0, 0);
                break;
            case EAST:
                location.add(0, 0, distance);
                break;
            case SOUTH:
                location.subtract(distance, 0, 0);
                break;
            case WEST:
                location.subtract(0, 0, distance);
                break;
        }

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
