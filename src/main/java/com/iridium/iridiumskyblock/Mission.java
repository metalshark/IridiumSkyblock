package com.iridium.iridiumskyblock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Mission {

    public enum Type {
        BLOCK_BREAK,
        BLOCK_PLACE,
        ENTITY_KILL,
        FISH_CATCH,
        VALUE_INCREASE,
        EXPERIENCE
    }

    @RequiredArgsConstructor
    public static class Level {

        @Getter private final int crystals;
        @Getter private final int vault;
        @Getter private final int amount;
        @Getter private final Type type;
        @Getter private final Set<String> requirements; // Sets are optimised for calling contains

        public void advance(final @NotNull Island island, final int amount) {
            throw new UnsupportedOperationException();
        }

        public void complete(final @NotNull Island island) {
            throw new UnsupportedOperationException();
        }

    }

}
