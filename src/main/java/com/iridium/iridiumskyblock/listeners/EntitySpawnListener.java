package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EntitySpawnListener implements Listener {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onEntitySpawn(@NotNull EntitySpawnEvent event) {
        final @NotNull Entity entity = event.getEntity();
        final @Nullable Island island = islandManager.getIslandByEntity(entity);
        if (island == null) return;

        if (!config.blockedEntities.contains(event.getEntityType())) return;

        island.addEntity(entity);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onVehicleSpawn(@NotNull VehicleCreateEvent event) {
        final @NotNull Vehicle vehicle = event.getVehicle();
        final @Nullable Island island = islandManager.getIslandByEntity(vehicle);
        if (island == null) return;

        if (!config.blockedEntities.contains(vehicle.getType())) return;

        island.addEntity(vehicle);
    }

}
