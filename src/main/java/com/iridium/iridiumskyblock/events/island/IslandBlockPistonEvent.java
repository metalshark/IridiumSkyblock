package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IslandBlockPistonEvent extends IslandBlockEvent {

    @Getter private final @NotNull List<Block> blocks;
    @Getter private final @NotNull BlockFace direction;

    public IslandBlockPistonEvent(final @NotNull Island island,
                                  final @NotNull Block block,
                                  final @NotNull List<Block> blocks,
                                  final @NotNull BlockFace direction) {
        super(island, block);
        this.blocks = blocks;
        this.direction = direction;
    }

}
