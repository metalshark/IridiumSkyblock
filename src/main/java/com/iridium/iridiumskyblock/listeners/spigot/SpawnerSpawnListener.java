package com.iridium.iridiumskyblock.listeners.spigot;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandSpawnerSpawnEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class SpawnerSpawnListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull BiConsumer<Runnable, Long> runTaskLater;

    @EventHandler
    public void onSpawnerSpawn(final @NotNull SpawnerSpawnEvent event) {
        final @Nullable Island island = getIslandByLocation.apply(event.getLocation());
        if (island == null) return;

        new IslandSpawnerSpawnEvent(island, event.getEntity(), event.getSpawner());
    }

}
