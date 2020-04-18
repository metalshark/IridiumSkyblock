package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.nms.NMS;
import com.iridium.iridiumskyblock.runnables.UpdateBlockStateRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class EntityExplodeListener implements Listener {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private static final @NotNull NMS nms = IridiumSkyblock.getNms();
    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull BukkitScheduler scheduler = Bukkit.getScheduler();

    @EventHandler
    @SuppressWarnings("unused")
    public void onEntityExplode(@NotNull EntityExplodeEvent event) {
        try {
            final @NotNull Entity entity = event.getEntity();
            if (!islandManager.isIslandWorldEntity(entity)) return;

            if (!config.allowExplosions)
                event.setCancelled(true);
        } catch (Exception ex) {
            plugin.sendErrorMessage(ex);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onMonitorEntityExplode(@NotNull EntityExplodeEvent event) {
        try {
            @NotNull final Entity entity = event.getEntity();
            @NotNull final Location location = entity.getLocation();
            if (!islandManager.isIslandWorldLocation(location)) return;

            Island island = islandManager.getIslandByEntity(entity);
            if (island != null && island.isLocationInIsland(location)) {
                event.setCancelled(true);
                island.removeEntity(entity);
                return;
            }

            island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            for (final @NotNull Block block : event.blockList()) {
                if (!island.isLocationInIsland(block.getLocation())) {
                    nms.setBlockFast(block, 0, (byte) 0);
                    final @NotNull BlockState blockState = block.getState();
                    final @NotNull Runnable task = new UpdateBlockStateRunnable(blockState);
                    scheduler.scheduleSyncDelayedTask(plugin, task);
                }

                if (!Utils.isBlockValuable(block)) continue;

                if (!(block.getState() instanceof CreatureSpawner))
                    island.removeValuableBlock(block);

                if (island.isUpdating())
                    island.addTempValue(block.getLocation());
            }
            island.calculateValue();
        } catch (Exception ex) {
            plugin.sendErrorMessage(ex);
        }
    }

}
