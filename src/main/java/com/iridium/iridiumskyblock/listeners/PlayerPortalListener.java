package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public class PlayerPortalListener implements Listener {

    private final static @NotNull Config config = IridiumSkyblock.getConfiguration();
    private final static @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private final static @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();

    private final static boolean supportsMinecraftVersion15 = XMaterial.supports(15);

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerPortal(@NotNull PlayerPortalEvent event) {
        try {
            final @NotNull Location fromLocation = event.getFrom();
            final @Nullable Island island = islandManager.getIslandByLocation(fromLocation);
            if (island == null) return;

            if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) return;

            if (!config.netherIslands) {
                event.setCancelled(true);
                return;
            }

            final @NotNull Player player = event.getPlayer();
            final @NotNull User user = User.getUser(player);
            if (!island.getPermissionsByUser(user).useNetherPortal) {
                event.setCancelled(true);
                return;
            }

            if (supportsMinecraftVersion15)
                event.setCanCreatePortal(true);
            else {
                try {
                    PlayerPortalEvent.class.getMethod("useTravelAgent", boolean.class).invoke(event, true);
                    Class.forName("org.bukkit.TravelAgent")
                            .getMethod("setCanCreatePortal", boolean.class)
                            .invoke(PlayerPortalEvent.class.getMethod("getPortalTravelAgent").invoke(event), true);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                    plugin.sendErrorMessage(e);
                }
            }

            final @Nullable World world = fromLocation.getWorld();
            if (world == null) return;

            final @NotNull String worldName = world.getName();
            if (worldName.equals(config.worldName))
                event.setTo(island.getNetherhome());
            else if (worldName.equals(config.netherWorldName))
                event.setTo(island.getHome());
        } catch (Exception e) {
            plugin.sendErrorMessage(e);
        }
    }

}
