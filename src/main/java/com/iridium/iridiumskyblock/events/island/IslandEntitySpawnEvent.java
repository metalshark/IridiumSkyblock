package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class IslandEntitySpawnEvent extends IslandEntityEvent {

    public IslandEntitySpawnEvent(final @NotNull Island island,
                                  final @NotNull Entity spawnee) {
        super(island, spawnee);
    }

}
