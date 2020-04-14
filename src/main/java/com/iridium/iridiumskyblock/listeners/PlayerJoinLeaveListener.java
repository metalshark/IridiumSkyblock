package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        try {
            @NotNull final Player player = event.getPlayer();
            @NotNull final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
            if (player.isOp()) {
                @NotNull final String latest = plugin.getLatest();
                if (plugin.getLatest() != null
                        && IridiumSkyblock.getConfiguration().notifyAvailableUpdate
                        && !latest.equals(plugin.getDescription().getVersion())) {
                    @NotNull final String prefix = IridiumSkyblock.getConfiguration().prefix;
                    player.sendMessage(Utils.color(prefix + " &7This message is only seen by opped players."));
                    player.sendMessage(Utils.color(prefix + " &7Newer version available: " + latest));
                }
            }

            @NotNull final Location location = player.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;

            @NotNull final User user = User.getUser(player);
            user.name = player.getName();

            if (user.flying && (user.getIsland() == null || user.getIsland().getFlightBooster() == 0)) {
                player.setAllowFlight(false);
                player.setFlying(false);
                user.flying = false;
            }
            user.bypassing = false;

            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            Bukkit.getScheduler().runTaskLater(plugin, () -> island.sendBorder(player), 1);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
