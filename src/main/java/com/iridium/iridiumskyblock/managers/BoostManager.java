package com.iridium.iridiumskyblock.managers;

import com.cryptomorin.xseries.XBlock;
import com.iridium.iridiumskyblock.Boost;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoostManager implements Listener {

    private final @NotNull IridiumSkyblock plugin;

    public BoostManager(final @NotNull IridiumSkyblock plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockGrow(final @NotNull BlockGrowEvent event) {
        final @Nullable Block block = event.getBlock();
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(block.getLocation());
        if (island == null) return;

        final @Nullable Boost boost = plugin.getDatabaseManager().getIslandBoostByType(island, Boost.Type.FARMING);
        if (boost == null) return;

        if (!XBlock.isCrops(block.getType())) return;
        XBlock.setAge(block, CropState.RIPE.ordinal());
        event.setCancelled(true); // Prevent the new crop state from being overridden
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerExpChange(final @NotNull PlayerExpChangeEvent event) {
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(event.getPlayer().getLocation());
        if (island == null) return;

        final @Nullable Boost boost = plugin.getDatabaseManager().getIslandBoostByType(island, Boost.Type.EXPERIENCE);
        if (boost == null) return;

        event.setAmount(event.getAmount() * 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerSpawn(final @NotNull SpawnerSpawnEvent event) {
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(event.getLocation());
        if (island == null) return;

        final @Nullable Boost boost = plugin.getDatabaseManager().getIslandBoostByType(island, Boost.Type.SPAWNER);
        if (boost == null) return;

        final @NotNull CreatureSpawner spawner = event.getSpawner();
        Bukkit.getScheduler().runTaskLater(plugin, () -> spawner.setDelay(spawner.getDelay() / 2), 0L);
    }

}
