package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.XMaterial;
import com.iridium.iridiumskyblock.configs.Upgrades;
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

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull BukkitScheduler scheduler = Bukkit.getScheduler();
    private static final @NotNull Upgrades upgrades = IridiumSkyblock.getUpgrades();

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockFromTo(@NotNull BlockFromToEvent event) {
        try {
            final @NotNull Block block = event.getBlock();
            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            final @NotNull Block toBlock = event.getToBlock();
            final @NotNull Material toMaterial = toBlock.getType();
            if (!(toMaterial.equals(Material.COBBLESTONE) || toMaterial.equals(Material.STONE)))
                return;

            final @NotNull Location toLocation = toBlock.getLocation();
            final @NotNull Material material = block.getType();
            if (material.equals(Material.WATER) || material.equals(Material.LAVA)) {
                final @Nullable Island toIsland = islandManager.getIslandByLocation(toLocation);
                if (island != toIsland)
                    event.setCancelled(true);
            }

            if (!upgrades.oresUpgrade.enabled) return;

            if (event.getFace() == BlockFace.DOWN) return;

            if (!isSurroundedByWater(toLocation))
                return;

            final int oreLevel = island.getOreLevel();
            final @Nullable World world = location.getWorld();
            if (world == null) return;

            final @NotNull String worldName = world.getName();
            @NotNull List<String> islandOreUpgrades;
            if (worldName.equals(config.worldName)) islandOreUpgrades = IridiumSkyblock.getOreUpgrades(oreLevel);
            else if (worldName.equals(config.netherWorldName)) islandOreUpgrades = IridiumSkyblock.getNetherOreUpgrades(oreLevel);
            else return;

            final @NotNull Runnable task = new OreUpgradeRunnable(toBlock, islandOreUpgrades, material, island, location);
            scheduler.runTask(plugin, task);
        } catch (Exception ex) {
            plugin.sendErrorMessage(ex);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockFrom(@NotNull BlockFormEvent event) {
        try {
            final @NotNull Block block = event.getBlock();
            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            final @NotNull BlockState blockState = event.getNewState();
            final @NotNull Material material = blockState.getType();
            if (!material.equals(Material.OBSIDIAN)) return;

            island.addFailedGenerator(location);
        } catch (Exception ex) {
            plugin.sendErrorMessage(ex);
        }
    }

    public boolean isSurroundedByWater(@NotNull Location location) {
        final @Nullable World world = location.getWorld();
        if (world == null) return false;

        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final @NotNull int[][] coords = {
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

        for (final @NotNull int[] coord : coords) {
            final @NotNull Block block = world.getBlockAt(coord[0], coord[1], coord[2]);
            final @NotNull Material material = block.getType();
            final @NotNull String name = material.name();
            if (name.contains("WATER")) return true;
        }
        return false;
    }

}
