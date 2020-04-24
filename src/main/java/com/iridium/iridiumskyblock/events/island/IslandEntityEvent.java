package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class IslandEntityEvent extends IslandEvent {

    @Getter private final @NotNull Entity entity;

    public IslandEntityEvent(final @NotNull Island island,
                             final @NotNull Entity entity) {
        super(island);
        this.entity = entity;
    }

}
