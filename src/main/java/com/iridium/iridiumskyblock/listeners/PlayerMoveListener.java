package com.iridium.iridiumskyblock.listeners;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerMoveListener implements Listener {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        try {
            final @NotNull Player player = event.getPlayer();
            final @NotNull Location location = player.getLocation();
            if (!islandManager.isIslandWorldLocation(location)) return;

            // Handle the player falling out the world
            if (location.getY() < 0 && config.voidTeleport) {
                final @Nullable Island island = islandManager.getIslandByLocation(location);
                final @Nullable World world = location.getWorld();
                if (world == null) return;

                if (island != null) {
                    if (world.getName().equals(config.worldName))
                        island.teleportHome(player);
                    else if (world.getName().equals(config.netherWorldName))
                        island.teleportNetherHome(player);
                } else {
                    final @NotNull User user = User.getUser(player);
                    if (user.getIsland() != null) {
                        if (world.getName().equals(config.worldName))
                            user.getIsland().teleportHome(player);
                        else if (world.getName().equals(config.netherWorldName))
                            user.getIsland().teleportNetherHome(player);
                    } else if (islandManager.isIslandWorld(world)) {
                        @NotNull final Location spawnLocation = islandManager.getSpawnLocation(player);
                        player.teleport(spawnLocation);
                    }
                }
            }

            final @NotNull User user = User.getUser(player);
            final @Nullable Island island = user.getIsland();
            if (island == null) return;

            if (user.isFlying()
                    && (!island.isLocationInIsland(location) || island.getFlightBooster() == 0)
                    && !player.getGameMode().equals(GameMode.CREATIVE)
                    && !(player.hasPermission("IridiumSkyblock.Fly")
                        || player.hasPermission("iridiumskyblock.fly"))) {
                player.setAllowFlight(false);
                player.setFlying(false);
                user.setFlying(false);
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().flightDisabled
                        .replace("%prefix%", config.prefix)));
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
