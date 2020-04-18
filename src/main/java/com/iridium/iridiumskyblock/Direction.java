package com.iridium.iridiumskyblock;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public enum Direction implements Serializable {

    NORTH, EAST, SOUTH, WEST;

    @NotNull public Direction next() {
        if (this == NORTH) return EAST;
        if (this == EAST) return SOUTH;
        if (this == SOUTH) return WEST;
        return NORTH;
    }

}
