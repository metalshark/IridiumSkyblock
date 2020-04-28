package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandConfiguration;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.chunkgenerators.SkyblockGenerator;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.iterators.IslandLocationIterator;
import org.bukkit.block.BlockFace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class IslandManager {

    final @NotNull Map<Environment, String> worldNames;
    final @NotNull Map<Environment, World> worlds;
    final @NotNull Iterator<Location> nextLocation;

    public IslandManager() {
        worldNames = Collections.unmodifiableMap(new HashMap<Environment, String>(){{
            put(Environment.NORMAL, "IridiumSkyblock");
            put(Environment.NETHER, "IridiumSkyblock_nether");
        }});
        worlds = Collections.unmodifiableMap(worldNames.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> getOrCreateWorld(e.getValue(), e.getKey())
            )));
        final int distance = 150;
        final @NotNull Location location = new Location(worlds.get(Environment.NORMAL), -distance, 0, 0);
        final @NotNull BlockFace direction = BlockFace.NORTH;
        final int countInDirection = 0;
        final int maxInDirection = 1;
        nextLocation = new IslandLocationIterator(distance, location, direction, countInDirection, maxInDirection);
    }

    public @NotNull Island createIsland(final @NotNull IslandConfiguration configuration) {
        final @NotNull Location location = nextLocation.next();
        final @NotNull BiFunction<User, Permission, Boolean> isUserForbidden = (user, permission) -> false;
        return new Island(configuration, location.getBlockX(), location.getBlockZ(), 50, worlds, isUserForbidden);
    }

    private @NotNull World createWorld(final @NotNull World.Environment environment,
                                        final @NotNull String name) {
        final @NotNull SkyblockGenerator generator = new SkyblockGenerator();
        final @NotNull WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.environment(environment);
        worldCreator.generateStructures(false);
        worldCreator.generator(generator);
        worldCreator.type(WorldType.FLAT);
        final @Nullable World world = worldCreator.createWorld();
        if (world == null)
            throw new RuntimeException("Unable to create island world " + name);
        return world;
    }

    public @Nullable Island getIslandByLocation(final @NotNull Location location) {
        return null;
    }

    private @NotNull World getOrCreateWorld(final @NotNull String name,
                                            final @NotNull Environment environment) {
        @Nullable World world = Bukkit.getWorld(name);
        if (world == null) world = createWorld(environment, name);

        /*
        final @NotNull Environment worldEnvironment = world.getEnvironment();
        if (worldEnvironment != environment)
            throw new RuntimeException("Environment of island world " + name + " is " + worldEnvironment + " expecting " + environment);

        world.getWorldBorder().setSize(Double.MAX_VALUE);
        */

        return world;
    }

}
