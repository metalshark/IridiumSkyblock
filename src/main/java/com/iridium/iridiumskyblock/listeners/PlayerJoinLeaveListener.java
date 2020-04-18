package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.runnables.SendIslandBorderRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerJoinLeaveListener implements Listener {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull BukkitScheduler scheduler = Bukkit.getScheduler();

    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin(@NotNull PlayerJoinEvent event) {
        try {
            final @NotNull Player player = event.getPlayer();
            if (player.isOp()) {
                final @NotNull String latest = plugin.getLatest();
                if (plugin.getLatest() != null
                        && config.notifyAvailableUpdate
                        && !latest.equals(plugin.getDescription().getVersion())) {
                    final @NotNull String prefix = config.prefix;
                    player.sendMessage(Utils.color(prefix + " &7This message is only seen by opped players."));
                    player.sendMessage(Utils.color(prefix + " &7Newer version available: " + latest));
                }
            }

            final @NotNull Location location = player.getLocation();
            if (!islandManager.isIslandWorldLocation(location)) return;

            final @NotNull User user = User.getUser(player);
            user.setName(player.getName());

            if (user.isFlying() && (user.getIsland() == null || user.getIsland().getFlightBooster() == 0)) {
                player.setAllowFlight(false);
                player.setFlying(false);
                user.setFlying(false);
            }
            user.setBypassing(false);

            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            final @NotNull Runnable task = new SendIslandBorderRunnable(island, player);
            scheduler.runTaskLater(plugin, task, 1);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
