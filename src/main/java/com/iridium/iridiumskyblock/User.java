package com.iridium.iridiumskyblock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor
public class User {

    public enum Permission {
        BLOCK_BREAK,
        BLOCK_PLACE,
        CATCH_FISH,
        DAMAGE_ANIMAL,
        DAMAGE_MOB,
        DAMAGE_PLAYER,
        DAMAGE_VEHICLE,
        NETHER_PORTAL,
        PICKUP_ITEM
    }

    private final @NotNull UUID uuid;
    @Getter @Setter private @Nullable Island island;

}
