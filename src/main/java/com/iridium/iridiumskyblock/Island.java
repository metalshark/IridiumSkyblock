package com.iridium.iridiumskyblock;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class Island {

    @Accessors(chain = true)
    public static class Configuration {

        @Getter @Setter private Color borderColor;
        @Getter @Setter private boolean explosionsEnabled;
        @Getter @Setter private boolean leavesDecayEnabled;
        @Getter @Setter private boolean pvpEnabled;

    }

    @Getter private final @NotNull Configuration configuration;
    @Getter private final @NotNull BoundingBox boundingBox;

    public Island(final @NotNull Configuration configuration,
                  final @NotNull BoundingBox boundingBox) {
        this.configuration = configuration;
        this.boundingBox = boundingBox;
    }

    public boolean isNotOnIsland(final @NotNull Location location) {
        return boundingBox.contains(location.toVector());
    }

}
