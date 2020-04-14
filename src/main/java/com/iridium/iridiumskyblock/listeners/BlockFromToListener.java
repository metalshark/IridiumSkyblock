package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.XMaterial;
import com.iridium.iridiumskyblock.runnables.OreUpgradeRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class BlockFromToListener implements Listener {

    @EventHandler
    public void onBlockFromTo(@NotNull BlockFromToEvent event) {
        try {
            @NotNull final Block block = event.getBlock();
            @NotNull final Location location = block.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            @NotNull final Block toBlock = event.getToBlock();
            @NotNull final Material toMaterial = toBlock.getType();
            if (!(toMaterial.equals(Material.COBBLESTONE) || toMaterial.equals(Material.STONE)))
                return;

            @NotNull final Location toLocation = toBlock.getLocation();
            @NotNull final Material material = block.getType();
            if (material.equals(Material.WATER) || material.equals(Material.LAVA)) {
                @Nullable final Island toIsland = islandManager.getIslandViaLocation(toLocation);
                if (island != toIsland)
                    event.setCancelled(true);
            }

            if (!IridiumSkyblock.getUpgrades().oresUpgrade.enabled) return;

            if (event.getFace() == BlockFace.DOWN) return;

            if (!isSurroundedByWater(toLocation))
                return;

            final int oreLevel = island.getOreLevel();
            @Nullable final World world = location.getWorld();
            if (world == null) return;

            @NotNull final String worldName = world.getName();
            @NotNull final Config config = IridiumSkyblock.getConfiguration();
            @NotNull List<String> islandOreUpgrades;
            if (worldName.equals(config.worldName)) islandOreUpgrades = IridiumSkyblock.oreUpgradeCache.get(oreLevel);
            else if (worldName.equals(config.netherWorldName)) islandOreUpgrades = IridiumSkyblock.netherOreUpgradeCache.get(oreLevel);
            else return;

            @NotNull final BukkitScheduler scheduler = Bukkit.getScheduler();
            @NotNull final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
            @NotNull final Runnable task = new OreUpgradeRunnable(toBlock, islandOreUpgrades, material, island, location);
            scheduler.runTask(plugin, task);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    @EventHandler
    public void onBlockFrom(@NotNull BlockFormEvent event) {
        try {
            @NotNull final Block block = event.getBlock();
            @NotNull final Location location = block.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            if (!event.getNewState().getType().equals(Material.OBSIDIAN)) return;

            island.failedGenerators.add(location);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    public boolean isSurroundedByWater(Location location) {
        @Nullable final World world = location.getWorld();
        if (world == null) return false;

        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final int[][] coords = {
                // At the same elevation
                {x + 1, y, z},
                {x - 1, y, z},
                {x, y, z + 1},
                {x, y, z - 1},
                // Above
                {x + 1, y + 1, z},
                {x - 1, y + 1, z},
                {x, y + 1, z + 1},
                {x, y + 1, z - 1},
                // Below
                {x + 1, y - 1, z},
                {x - 1, y - 1, z},
                {x, y - 1, z + 1},
                {x, y - 1, z - 1}
        };

        for (int[] coord : coords) {
            @NotNull final Block block = world.getBlockAt(coord[0], coord[1], coord[2]);
            @NotNull final Material material = block.getType();
            @NotNull final String name = material.name();
            if (name.contains("WATER")) return true;
        }
        return false;
    }

}
