package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
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

    @EventHandler
    public void onEntitySpawn(@NotNull EntitySpawnEvent event) {
        @NotNull final Entity entity = event.getEntity();
        @NotNull final Location location = entity.getLocation();
        @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
        @Nullable final Island island = islandManager.getIslandViaLocation(location);
        if (island == null) return;

        if (!IridiumSkyblock.getConfiguration().blockedEntities.contains(event.getEntityType())) return;

        IridiumSkyblock.getInstance().entities.put(entity.getUniqueId(), island);
        monitorEntity(entity);
    }

    @EventHandler
    public void onVehicleSpawn(@NotNull VehicleCreateEvent event) {
        @NotNull final Vehicle vehicle = event.getVehicle();
        @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
        @NotNull final Location location = vehicle.getLocation();
        @Nullable final Island island = islandManager.getIslandViaLocation(location);
        if (island == null) return;

        if (!IridiumSkyblock.getConfiguration().blockedEntities.contains(vehicle.getType())) return;

        IridiumSkyblock.getInstance().entities.put(vehicle.getUniqueId(), island);
        monitorEntity(vehicle);
    }

    public void monitorEntity(@NotNull Entity entity) {
        if (entity.isDead()) return;

        @NotNull final UUID uuid = entity.getUniqueId();
        @NotNull final Island startingIsland = IridiumSkyblock.getInstance().entities.get(uuid);
        if (startingIsland.isInIsland(entity.getLocation())) {
            //The entity is still in the island, so make a scheduler to check again
            Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), () -> monitorEntity(entity), 20);
        } else {
            //The entity is not in the island, so remove it
            entity.remove();
            IridiumSkyblock.getInstance().entities.remove(uuid);
        }
    }

}
