package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class IslandBlockFormEvent extends IslandBlockStateEvent {

    public IslandBlockFormEvent(final @NotNull Island island,
                                final @NotNull Block block,
                                final @NotNull BlockState newState) {
        super(island, block, newState);
    }

}
