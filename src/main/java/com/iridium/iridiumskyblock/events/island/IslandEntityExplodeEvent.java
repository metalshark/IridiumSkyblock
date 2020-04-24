package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IslandEntityExplodeEvent extends IslandEntityEvent {

    @Getter private final @NotNull Location location;
    @Getter private final @NotNull List<Block> blocks;
    @Getter private final float yield;

    public IslandEntityExplodeEvent(final @NotNull Island island,
                                    final @NotNull Entity entity,
                                    final @NotNull Location location,
                                    final @NotNull List<Block> blocks,
                                    final float yield) {
        super(island, entity);
        this.location = location;
        this.blocks = blocks;
        this.yield = yield;
    }

}
