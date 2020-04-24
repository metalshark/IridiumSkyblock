package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandEntitySpawnEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class EntitySpawnListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;

    @EventHandler
    public void onEntitySpawn(final @NotNull EntitySpawnEvent event) {
        final @Nullable Island island = getIslandByLocation.apply(event.getLocation());
        if (island == null) return;

        new IslandEntitySpawnEvent(island, event.getEntity());
    }

}
