package com.iridium.iridiumskyblock.managers;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumskyblock.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class DatabaseManager {

    private final @NotNull IridiumSkyblock plugin;

    public @NotNull Map<World.Environment, @NotNull String> getWorldNames() {
        return new ImmutableMap.Builder<World.Environment, @NotNull String>()
            .put(World.Environment.NORMAL, plugin.getName())
            .put(World.Environment.NETHER, plugin.getName() + "_nether")
            .build();
    }

    public @Nullable Boost getIslandBoostByType(final @NotNull Island island, final @NotNull Boost.Type type) {
        throw new UnsupportedOperationException();
    }

    public void addIslandBoost(final @NotNull Island island, final @NotNull Boost boost) {
        throw new UnsupportedOperationException();
    }

    public void removeIslandBoost(final @NotNull Island island, final @NotNull Boost boost) {
        throw new UnsupportedOperationException();
    }

    public @NotNull Collection<Mission.Level> getIslandMissionsByType(final @NotNull Island island, final @NotNull Mission.Type type) {
        throw new UnsupportedOperationException();
    }

    public boolean isUserForbidden(final @NotNull Island island, final @NotNull User user, final @NotNull User.Permission permission) {
        throw new UnsupportedOperationException();
    }

}
