package com.iridium.iridiumskyblock.db;

import com.google.gson.Gson;
import com.iridium.iridiumskyblock.Direction;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.iterators.IslandChunkKeyIterator;
import com.iridium.iridiumskyblock.iterators.IslandLocationIterator;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JsonDatabaseManager /*extends DatabaseManager*/ {

    private static class IslandManagerModel {
        @NotNull public Map<Integer, Island> islands = new HashMap<>();
        @NotNull public Map<String, User> users = new HashMap<>();
        @NotNull public Map<List<Integer>, Set<Integer>> islandCache = new HashMap<>();
        @Nullable public Location nextLocation;
        @NotNull public Direction direction = Direction.NORTH;
        public int current = 0;
        public int length = 1;
        public int nextID = 1;
    }

    @NotNull private final File file;
    @NotNull private final Gson gson = new Gson();
    @NotNull private final IslandManagerModel dataModel;
    @NotNull private final Iterator<Location> nextLocationIterator;

    public JsonDatabaseManager(@NotNull File file) {
        super();
        this.file = file;
        Gson gson = new Gson();
        dataModel = gson.fromJson(file.toString(), IslandManagerModel.class);
        if (dataModel.nextLocation == null) {
            final World islandWorld = IridiumSkyblock.getIslandManager().getWorld();
            dataModel.nextLocation = new Location(islandWorld, 0, 0, 0);
        }
        nextLocationIterator = new IslandLocationIterator(dataModel.nextLocation, dataModel.direction, dataModel.current, dataModel.length);
    }

}
