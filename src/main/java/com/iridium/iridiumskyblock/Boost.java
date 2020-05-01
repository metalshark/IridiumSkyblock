package com.iridium.iridiumskyblock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class Boost {

    public enum Type {
        EXPERIENCE,
        FARMING,
        FLIGHT,
        SPAWNER
    }

    @Getter private final int crystals;
    @Getter private final int vault;
    @Getter private final @NotNull Type type;
    @Getter private final int seconds;
    @Getter private final int slot;

}
