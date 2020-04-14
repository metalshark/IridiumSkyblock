package com.iridium.iridiumskyblock.db;

import com.google.gson.Gson;
import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.iterators.IslandChunkKeyIterator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JsonDatabaseManager extends DatabaseManager {

    static class DataModel {
        @Nullable public Map<Integer, Island> islands;
        @Nullable public Map<String, User> users;
        @Nullable public Map<List<Integer>, Set<Integer>> islandCache;
        @Nullable public Location nextLocation;
        public Direction direction = Direction.NORTH;
        public int current = 0;
        public int length = 1;
        public int nextID = 1;
    }

    @NotNull private final File file;
    @NotNull private final Gson gson = new Gson();
    @NotNull private final DataModel dataModel;

    public JsonDatabaseManager(@NotNull File file) {
        super();
        this.file = file;
        Gson gson = new Gson();
        dataModel = gson.fromJson(file.toString(), DataModel.class);
        if (dataModel.islands == null) dataModel.islands = new HashMap<>();
        if (dataModel.users == null) dataModel.users = new HashMap<>();
        if (dataModel.islandCache == null) dataModel.islandCache = new HashMap<>();
        if (dataModel.nextLocation == null) {
            final World islandWorld = IridiumSkyblock.getIslandManager().getWorld();
            dataModel.nextLocation = new Location(islandWorld, 0, 0, 0);
        }
    }

    @Override
    public void addIsland(@NotNull Island island) {
        assert dataModel.islands != null;
        dataModel.islands.put(island.getId(), island);
        save();
    }

    @Override
    public void removeIsland(@NotNull Island island) {
        final int id = island.getId();
        assert dataModel.islands != null;
        dataModel.islands.remove(id);
        new IslandChunkKeyIterator(island).forEachRemaining(coords -> {
            final List<Integer> chunkKey = createChunkKey(coords[0], coords[1]);
            removeIslandFromChunk(chunkKey, id);
        });
        save();
    }

    @Override
    public @Nullable Island getIslandByCoords(int x, int z) {
        final List<Integer> chunkKey = createChunkKey(x, z);
        final Set<Island> islands = getIslandsByChunk(chunkKey);

        for (Island island : islands) {
            if (island.isInIsland(x, z)) return island;
        }

        for (Island island : getIslands()) {
            if (!island.isInIsland(x, z)) continue;
            addIslandToChunk(chunkKey, island);
            return island;
        }

        return null;
    }

    @Override
    public @Nullable Island getIslandById(int id) {
        assert dataModel.islands != null;
        return dataModel.islands.get(id);
    }

    @Override
    public @NotNull Collection<Integer> getIslandIds() {
        return null;
    }

    @Override
    public @NotNull Collection<Island> getIslands() {
        assert dataModel.islands != null;
        return dataModel.islands.values();
    }

    @Override
    public int getNextIslandId() {
        return 0;
    }

    @Override
    protected @NotNull Iterator<Location> getNextIslandLocationIterator() {
        return null;
    }

    @Override
    public void addUser(@NotNull User user) {
        assert dataModel.users != null;
        dataModel.users.put(user.player, user);
        save();
    }

    @Override
    public void removeUser(@NotNull User user) {

    }

    @Override
    public @Nullable User getUserByUUID(@NotNull UUID uuid) {
        assert dataModel.users != null;
        return dataModel.users.get(uuid.toString());
    }

    @Override
    public @NotNull Collection<User> getUsers() {
        assert dataModel.users != null;
        return dataModel.users.values();
    }

    private void save() {
        try (final FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(dataModel, fileWriter);
        } catch (IOException e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

    public static List<Integer> createChunkKey(Chunk chunk) {
        return createChunkKey(chunk.getX(), chunk.getZ());
    }
    public static List<Integer> createChunkKey(int x, int z) {
        return Arrays.asList(x, z);
    }
    public void addIslandToChunk(@NotNull Chunk chunk, @NotNull Island island) {
        addIslandToChunk(createChunkKey(chunk), island);
    }
    public void addIslandToChunk(@NotNull List<Integer> chunkKey, @NotNull Island island) {
        addIslandToChunk(chunkKey, island.getId());
    };
    public void addIslandToChunk(@NotNull List<Integer> chunkKey, int islandId) {
        getIslandIdsByChunk(chunkKey).add(islandId);
        save();
    }
    public @NotNull Set<Island> getIslandsByChunk(@NotNull Chunk chunk) {
        return getIslandsByChunk(createChunkKey(chunk));
    }
    public @NotNull Set<Island> getIslandsByChunk(@NotNull List<Integer> chunkKey) {
        return getIslandIdsByChunk(chunkKey)
            .stream()
            .map(this::getIslandById)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
    public @NotNull Set<Integer> getIslandIdsByChunk(@NotNull Chunk chunk) {
        return getIslandIdsByChunk(createChunkKey(chunk));
    }
    public @NotNull Set<Integer> getIslandIdsByChunk(@NotNull List<Integer> chunkKey) {
        assert dataModel.islandCache != null;
        return dataModel.islandCache.computeIfAbsent(chunkKey, key -> new HashSet<>());
    }
    public void removeIslandFromChunk(@NotNull List<Integer> chunkKey, @NotNull Island island) {
        removeIslandFromChunk(chunkKey, island.getId());
    };
    public void removeIslandFromChunk(@NotNull List<Integer> chunkKey, int islandId) {
        getIslandIdsByChunk(chunkKey).remove(islandId);
    }
}
