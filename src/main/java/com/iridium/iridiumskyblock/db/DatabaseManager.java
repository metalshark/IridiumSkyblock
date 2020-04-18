package com.iridium.iridiumskyblock.db;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class DatabaseManager {

    public abstract void addUser(@NotNull User user);
    public abstract @Nullable User getUserByUUID(@NotNull UUID uuid);
    public abstract void updateUser(@NotNull User user);

    public abstract void addIsland(@NotNull Island island);
    public abstract @Nullable Island getIslandByCoords(int x, int z);
    public abstract @Nullable Island getIslandById(int id);
    public abstract void removeIsland(@NotNull Island island);

    public abstract @NotNull Set<Integer> getIslandIds();
    public abstract @NotNull Set<Island> getIslands();

    public abstract int getNextIslandId();
    public abstract @NotNull Location getNextIslandLocation();

    public abstract boolean isIslandFailedGenerator(@NotNull Island island, @NotNull Location location);
    public abstract void addIslandFailedGenerator(@NotNull Island island, @NotNull Location location);
    public abstract void removeIslandFailedGenerator(@NotNull Island island, @NotNull Location location);

    public abstract void addIslandMember(@NotNull Island island, @NotNull User user);
    public abstract @NotNull Set<User> getIslandMembers(@NotNull Island island);
    public abstract @NotNull Set<UUID> getIslandMemberUuids(@NotNull Island island);
    public abstract void removeIslandMembers(@NotNull Island island);

    public abstract int getIslandMissionLevel(@NotNull Island island, @NotNull String missionName);
    public abstract int setIslandMissionLevel(@NotNull Island island, @NotNull String missionName, int level);

    public abstract int getIslandMissionAmount(@NotNull Island island, @NotNull String missionName);
    public abstract int setIslandMissionAmount(@NotNull Island island, @NotNull String missionName, int amount);

    public abstract @NotNull Map<String, Integer> getIslandMissionLevels(@NotNull Island island);
    public abstract void resetIslandMissionsLevels(@NotNull Island island);

    public abstract Set<UUID> getIslandEntityUuids(@NotNull Island island);
    public abstract int getIslandIdByEntityUuid(@NotNull UUID uuid);

}
