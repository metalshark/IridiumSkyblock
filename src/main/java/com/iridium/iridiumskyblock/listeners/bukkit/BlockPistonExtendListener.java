package com.iridium.iridiumskyblock.listeners.bukkit;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandBlockPistonEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class BlockPistonExtendListener implements Listener {

    private static final @NotNull Map<BlockFace, Vector> vectors = ImmutableMap.<BlockFace, Vector>builder()
        .put(BlockFace.EAST,  new Vector( 1, 0, 0))
        .put(BlockFace.WEST,  new Vector(-1, 0, 0))
        .put(BlockFace.UP,    new Vector( 0, 1, 0))
        .put(BlockFace.DOWN,  new Vector( 0,-1, 0))
        .put(BlockFace.SOUTH, new Vector( 0, 0, 1))
        .put(BlockFace.NORTH, new Vector( 0, 0,-1))
        .build();

    private final @NotNull Function<Location, Island> getIslandByLocation;

    @EventHandler
    public void onBlockPistonExtend(final @NotNull BlockPistonExtendEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = getIslandByLocation.apply(block.getLocation());
        if (island == null) return;

        final @NotNull List<Block> blocks = event.getBlocks();
        final @NotNull BlockFace direction = event.getDirection();

        // Ensure blocks outside of the island are unaffected
        final @NotNull Vector vector = vectors.get(direction);
        if (blocks.stream()
            .map(Block::getLocation)
            .map(location -> location.add(vector))
            .anyMatch(location -> !island.isOnIsland(location))) {
            event.setCancelled(true);
            return;
        }

        new IslandBlockPistonEvent(island, block, blocks, direction);
    }

}
