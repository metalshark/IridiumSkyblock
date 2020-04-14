package com.iridium.iridiumskyblock.db;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class DatabaseManager {

    public abstract @Nullable Island getIslandById(int id);
    public abstract @NotNull List<Island> getIslands();

    public abstract @Nullable User getUserByUUID(@NotNull UUID uuid);
    public abstract @NotNull Set<User> getUsers();

    public abstract void addIsland(@NotNull Island island);

}
