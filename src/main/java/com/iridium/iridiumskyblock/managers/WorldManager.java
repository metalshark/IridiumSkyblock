package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.chunkgenerators.SkyblockGenerator;
import com.iridium.iridiumskyblock.plugins.MultiverseCoreManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

public class WorldManager implements Listener {

    private final @NotNull IridiumSkyblock plugin;
    @Getter private final @NotNull Map<@NotNull Environment, @NotNull World> worlds;

    public WorldManager(final @NotNull IridiumSkyblock plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        worlds = plugin.getDatabaseManager().getWorldNames().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> getOrCreateWorld(e.getValue(), e.getKey())));

        final @Nullable Plugin mvPlugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        if (mvPlugin != null && mvPlugin.isEnabled()) {
            final @NotNull MultiverseCoreManager mvManager = new MultiverseCoreManager(mvPlugin);
            worlds.values().forEach(world -> mvManager.registerWorld(world, plugin.getName()));
        }
    }

    private @NotNull World createWorld(final @NotNull Environment environment,
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

    private @NotNull World getOrCreateWorld(final @NotNull String name,
                                            final @NotNull Environment environment) {
        @Nullable World world = Bukkit.getWorld(name);
        if (world == null) world = createWorld(environment, name);

        final @NotNull Environment worldEnvironment = world.getEnvironment();
        if (worldEnvironment != environment)
            throw new RuntimeException("Environment of island world " + name + " is " + worldEnvironment
                + " expecting " + environment);

        world.getWorldBorder().setSize(Double.MAX_VALUE);

        return world;
    }

}
