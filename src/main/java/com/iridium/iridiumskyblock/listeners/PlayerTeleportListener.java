package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.runnables.SendIslandBorderRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerTeleportListener implements Listener {

    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull BukkitScheduler scheduler = Bukkit.getScheduler();

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        try {
            final @Nullable Location toLocation = event.getTo();
            if (toLocation == null) return;

            final @Nullable Island island = islandManager.getIslandByLocation(toLocation);
            if (island == null) return;

            final @NotNull Player player = event.getPlayer();
            final @NotNull Runnable task = new SendIslandBorderRunnable(island, player);
            scheduler.scheduleSyncDelayedTask(plugin, task, 1);
        } catch (Exception e) {
            plugin.sendErrorMessage(e);
        }
    }

}
