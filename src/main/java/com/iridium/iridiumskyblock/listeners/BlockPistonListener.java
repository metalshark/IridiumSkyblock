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

    @NotNull private static final Map<BlockFace, int[]> offsets = ImmutableMap.<BlockFace, int[]>builder()
        .put(BlockFace.EAST,  new int[]{ 1, 0, 0})
        .put(BlockFace.WEST,  new int[]{-1, 0, 0})
        .put(BlockFace.UP,    new int[]{ 0, 1, 0})
        .put(BlockFace.DOWN,  new int[]{ 0,-1, 0})
        .put(BlockFace.SOUTH, new int[]{ 0, 0, 1})
        .put(BlockFace.NORTH, new int[]{ 0, 0,-1})
        .build();

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        try {
            @NotNull final Block block = event.getBlock();
            @NotNull final Location location = block.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            @NotNull final BlockFace face = event.getDirection();
            for (@NotNull Block extendedBlock : event.getBlocks()) {
                @NotNull final Location extendedBlockLocation = extendedBlock.getLocation();
                @NotNull final int[] offset = offsets.get(face);
                extendedBlockLocation.add(offset[0], offset[1], offset[2]);
                if (!island.isInIsland(extendedBlockLocation)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

    @EventHandler
    public void onBlockPistonReact(@NotNull BlockPistonRetractEvent event) {
        try {
            @NotNull final Block block = event.getBlock();
            @NotNull final Location location = block.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            for (@NotNull Block retractedBlock : event.getBlocks()) {
                @NotNull final Location retractedBlockLocation = retractedBlock.getLocation();
                if (!island.isInIsland(retractedBlockLocation)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
