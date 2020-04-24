package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class IslandSpawnerSpawnEvent extends IslandEntityEvent {

    @Getter private final @NotNull CreatureSpawner spawner;

    public IslandSpawnerSpawnEvent(final @NotNull Island island,
                                   final @NotNull Entity entity,
                                   final @NotNull CreatureSpawner spawner) {
        super(island, entity);
        this.spawner = spawner;
    }

}
