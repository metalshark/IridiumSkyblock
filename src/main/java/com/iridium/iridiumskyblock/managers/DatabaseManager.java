package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class DatabaseManager {

    private final @NotNull IridiumSkyblock plugin;


    // Worlds
    public @NotNull Map<World.Environment, @NotNull String> getWorldNames() {
        throw new UnsupportedOperationException();
    }


    // Islands
    public @Nullable Island getIslandByName(final @NotNull String name) {
        throw new UnsupportedOperationException();
    }

    public @Nullable Island getIslandByLocation(final @NotNull Location location) {
        throw new UnsupportedOperationException();
    }

    public void addOrUpdateIsland(final @NotNull Island island) {
        throw new UnsupportedOperationException();
    }


    // Users
    public @Nullable User getUserByPlayer(final @NotNull Player player) {
        throw new UnsupportedOperationException();
    }

    public boolean isUserForbidden(final @NotNull Island island, final @NotNull User user, final @NotNull User.Permission permission) {
        throw new UnsupportedOperationException();
    }


    // Boosts
    public void addIslandBoost(final @NotNull Island island, final @NotNull Boost boost) {
        throw new UnsupportedOperationException();
    }

    public @Nullable Boost getIslandBoostByType(final @NotNull Island island, final @NotNull Boost.Type type) {
        throw new UnsupportedOperationException();
    }

    public void removeIslandBoost(final @NotNull Island island, final @NotNull Boost boost) {
        throw new UnsupportedOperationException();
    }


    // Missions
    public @NotNull Collection<Mission.Level> getIslandMissionsByType(final @NotNull Island island, final @NotNull Mission.Type type) {
        throw new UnsupportedOperationException();
    }

}
