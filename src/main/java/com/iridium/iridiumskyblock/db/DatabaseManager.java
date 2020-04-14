package com.iridium.iridiumskyblock.db;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class DatabaseManager {

    public abstract void addIsland(@NotNull Island island);
    public abstract void removeIsland(@NotNull Island island);
    public abstract @Nullable Island getIslandByCoords(int x, int z);
    public abstract @Nullable Island getIslandById(int id);
    public abstract @NotNull Collection<Integer> getIslandIds();
    public abstract @NotNull Collection<Island> getIslands();
    public abstract int getNextIslandId();
    protected abstract @NotNull Iterator<Location> getNextIslandLocationIterator();
    public @NotNull Location getNextIslandLocation() {
        return getNextIslandLocationIterator().next();
    };

    public abstract void addUser(@NotNull User user);
    public abstract void removeUser(@NotNull User user);
    public abstract @Nullable User getUserByUUID(@NotNull UUID uuid);
    public abstract @NotNull Collection<User> getUsers();

}
