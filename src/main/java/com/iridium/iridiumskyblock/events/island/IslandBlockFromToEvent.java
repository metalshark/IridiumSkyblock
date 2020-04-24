package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class IslandBlockFromToEvent extends IslandBlockEvent {

    @Getter private final @NotNull Block toBlock;

    public IslandBlockFromToEvent(final @NotNull Island island,
                                  final @NotNull Block block,
                                  final @NotNull Block toBlock) {
        super(island, block);
        this.toBlock = toBlock;
    }

}
