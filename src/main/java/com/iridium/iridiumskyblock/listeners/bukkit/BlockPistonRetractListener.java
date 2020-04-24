package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandBlockPistonEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class BlockPistonRetractListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;

    @EventHandler
    public void onBlockPistonExtend(final @NotNull BlockPistonExtendEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = getIslandByLocation.apply(block.getLocation());
        if (island == null) return;

        final @NotNull List<Block> blocks = event.getBlocks();
        final @NotNull BlockFace direction = event.getDirection();

        // Ensure blocks outside of the island are unaffected
        if (blocks.stream()
            .map(Block::getLocation)
            .anyMatch(location -> !island.isOnIsland(location))) {
            event.setCancelled(true);
            return;
        }

        new IslandBlockPistonEvent(island, block, blocks, direction);
    }

}
