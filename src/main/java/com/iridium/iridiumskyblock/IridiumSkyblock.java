package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.listeners.bukkit.*;
import com.iridium.iridiumskyblock.listeners.spigot.SpawnerSpawnListener;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.managers.UserManager;
import com.iridium.iridiumskyblock.utilities.MinecraftReflection;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class IridiumSkyblock extends JavaPlugin {

    @Getter private MinecraftReflection minecraftReflection;

    @Getter private IslandManager islandManager;
    @Getter private UserManager userManager;

    @Override
    public void onEnable() {
        super.onEnable();

        minecraftReflection = new MinecraftReflection();

        islandManager = new IslandManager();
        userManager = new UserManager();

        final @NotNull Consumer<Event> callEvent = Bukkit.getPluginManager()::callEvent;
        final @NotNull Function<Location, Island> getIslandByLocation = islandManager::getIslandByLocation;
        final @NotNull Function<Player, User> getUserByPlayer = userManager::getUserByPlayer;
        final @NotNull BiConsumer<Runnable, Long> runTaskLater = (task, delay) ->
            Bukkit.getScheduler().runTaskLater(this, task, delay);

        Arrays.asList(
            new BlockBreakListener(getIslandByLocation, getUserByPlayer, callEvent),
            new BlockFormListener(getIslandByLocation),
            new BlockFromToListener(getIslandByLocation),
            new BlockGrowListener(getIslandByLocation),
            new BlockPistonExtendListener(getIslandByLocation),
            new BlockPistonRetractListener(getIslandByLocation),
            new BlockPlaceListener(getIslandByLocation, getUserByPlayer),
            new EntityDamageByEntityListener(getIslandByLocation, getUserByPlayer),
            new EntityDeathListener(getIslandByLocation, getUserByPlayer),
            new EntityExplodeListener(getIslandByLocation, minecraftReflection, runTaskLater),
            new EntityPickupItemListener(getIslandByLocation, getUserByPlayer),
            new EntitySpawnListener(getIslandByLocation),
            new LeavesDecayListener(getIslandByLocation, callEvent),
            new PlayerFishListener(getIslandByLocation, getUserByPlayer),
            new PlayerTeleportListener(getIslandByLocation, minecraftReflection, runTaskLater, getUserByPlayer),
            new SpawnerSpawnListener(getIslandByLocation, runTaskLater),
            new VehicleCreateListener(getIslandByLocation),
            new VehicleDamageListener(getIslandByLocation, getUserByPlayer, callEvent)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    @SuppressWarnings("unused")
    public IridiumSkyblock() {
        super();
    }

    @SuppressWarnings("unused")
    public IridiumSkyblock(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        HandlerList.unregisterAll(this);
    }

    public static IridiumSkyblock getPlugin() {
        return getPlugin(IridiumSkyblock.class);
    }

}
