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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpawnerSpawnListener implements Listener {

    @EventHandler
    public void onSpawnerSpawn(@NotNull SpawnerSpawnEvent event) {
        try {
            @NotNull final Location location = event.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            if (island.getSpawnerBooster() == 0) return;

            @NotNull final CreatureSpawner spawner = event.getSpawner();
            @NotNull final Runnable task = new BoostSpawnerRunnable(spawner);
            Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), task, 0);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
