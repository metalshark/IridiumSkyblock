package com.iridium.iridiumskyblock.listeners;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BlockPistonListener implements Listener {

    private static final@NotNull  Map<BlockFace, int[]> offsets = ImmutableMap.<BlockFace, int[]>builder()
        .put(BlockFace.EAST,  new int[]{ 1, 0, 0})
        .put(BlockFace.WEST,  new int[]{-1, 0, 0})
        .put(BlockFace.UP,    new int[]{ 0, 1, 0})
        .put(BlockFace.DOWN,  new int[]{ 0,-1, 0})
        .put(BlockFace.SOUTH, new int[]{ 0, 0, 1})
        .put(BlockFace.NORTH, new int[]{ 0, 0,-1})
        .build();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockPistonExtend(@NotNull BlockPistonExtendEvent event) {
        try {
            final @NotNull Block block = event.getBlock();
            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            final @NotNull BlockFace face = event.getDirection();
            for (final @NotNull Block extendedBlock : event.getBlocks()) {
                final @NotNull Location extendedBlockLocation = extendedBlock.getLocation();
                final @NotNull int[] offset = offsets.get(face);
                extendedBlockLocation.add(offset[0], offset[1], offset[2]);
                if (!island.isLocationInIsland(extendedBlockLocation)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockPistonReact(@NotNull BlockPistonRetractEvent event) {
        try {
            final @NotNull Block block = event.getBlock();
            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            for (final @NotNull Block retractedBlock : event.getBlocks()) {
                if (!island.isBlockInIsland(retractedBlock)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
