package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class IslandBlockStateEvent extends IslandBlockEvent {

    @Getter private final @NotNull BlockState newState;

    public IslandBlockStateEvent(final @NotNull Island island,
                                 final @NotNull Block block,
                                 final @NotNull BlockState newState) {
        super(island, block);
        this.newState = newState;
    }

}
