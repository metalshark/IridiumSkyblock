package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.runnables.BoostSpawnerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpawnerSpawnListener implements Listener {

    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull BukkitScheduler scheduler = Bukkit.getScheduler();

    @EventHandler
    @SuppressWarnings("unused")
    public void onSpawnerSpawn(@NotNull SpawnerSpawnEvent event) {
        try {
            final @NotNull Location location = event.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            if (island.getSpawnerBooster() == 0) return;

            final @NotNull CreatureSpawner spawner = event.getSpawner();
            final @NotNull Runnable task = new BoostSpawnerRunnable(spawner);
            scheduler.scheduleSyncDelayedTask(plugin, task, 0);
        } catch (Exception e) {
            plugin.sendErrorMessage(e);
        }
    }

}
