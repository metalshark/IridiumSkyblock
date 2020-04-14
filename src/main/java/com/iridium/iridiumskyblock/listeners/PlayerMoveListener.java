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

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        try {
            @NotNull final Player player = event.getPlayer();
            @NotNull final Location location = player.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;

            @NotNull final Config config = IridiumSkyblock.getConfiguration();

            if (location.getY() < 0 && config.voidTeleport) {
                @Nullable final Island island = islandManager.getIslandViaLocation(location);
                @Nullable final World world = location.getWorld();
                if (world == null) return;

                if (island != null) {
                    if (world.getName().equals(config.worldName))
                        island.teleportHome(player);
                    else
                        island.teleportNetherHome(player);
                } else {
                    @NotNull final User user = User.getUser(player);
                    if (user.getIsland() != null) {
                        if (world.getName().equals(config.worldName))
                            user.getIsland().teleportHome(player);
                        else if (world.getName().equals(config.netherWorldName))
                            user.getIsland().teleportNetherHome(player);
                    } else if (islandManager.isIslandWorld(world)) {
                        if (Bukkit.getPluginManager().isPluginEnabled("EssentialsSpawn")) {
                            @NotNull final PluginManager pluginManager = Bukkit.getPluginManager();
                            @Nullable final EssentialsSpawn essentialsSpawn = (EssentialsSpawn) pluginManager.getPlugin("EssentialsSpawn");
                            @Nullable final Essentials essentials = (Essentials) pluginManager.getPlugin("Essentials");
                            if (essentials != null && essentialsSpawn != null)
                                player.teleport(essentialsSpawn.getSpawn(essentials.getUser(player).getGroup()));
                        } else
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                    }
                }
            }

            @NotNull final User user = User.getUser(player);
            @NotNull final Island island = user.getIsland();
            if (island == null) return;

            if (user.flying
                    && (!island.isInIsland(location) || island.getFlightBooster() == 0)
                    && !player.getGameMode().equals(GameMode.CREATIVE)
                    && !(player.hasPermission("IridiumSkyblock.Fly")
                        || player.hasPermission("iridiumskyblock.fly"))) {
                player.setAllowFlight(false);
                player.setFlying(false);
                user.flying = false;
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().flightDisabled
                        .replace("%prefix%", config.prefix)));
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
