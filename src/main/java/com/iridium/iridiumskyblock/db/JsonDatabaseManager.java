package com.iridium.iridiumskyblock.db;

import com.google.gson.Gson;
import com.iridium.iridiumskyblock.Direction;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class JsonDatabaseManager extends DatabaseManager {

    static class DataModel {
        @Nullable public Map<Integer, Island> islands;
        @Nullable public Map<UUID, User> users;
        @Nullable public Map<List<Integer>, List<Integer>> islandCache;
        @Nullable public Integer length;
        @Nullable public Integer current;
        @Nullable public Direction direction;
        @Nullable public Location nextLocation;
        @Nullable public Integer nextID;
    }

    @NotNull private final File file;
    @NotNull private final Gson gson = new Gson();
    @NotNull private final DataModel dataModel;

    public JsonDatabaseManager(@NotNull File file) {
        super();
        this.file = file;
        Gson gson = new Gson();
        dataModel = gson.fromJson(file.toString(), DataModel.class);
    }

    @Override
    public @Nullable Island getIslandById(int id) {
        return null;
    }

    @Override
    public @NotNull List<Island> getIslands() {
        return new ArrayList<>();
    }

    @Override
    public @Nullable User getUserByUUID(@NotNull UUID uuid) {
        return null;
    }

    @Override
    public @NotNull Set<User> getUsers() {
        return new HashSet<>();
    }

    @Override
    public void addIsland(@NotNull Island island) {

    }
}
