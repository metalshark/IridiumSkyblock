package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.events.island.IslandUserPortalEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class PlayerPortalListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull Function<Player, User> getUserByPlayer;

    @EventHandler
    public void onPlayerPortal(final @NotNull PlayerPortalEvent event) {
        final @Nullable Location fromLocation = event.getFrom();
        final @Nullable Island island = getIslandByLocation.apply(fromLocation);
        if (island == null) return;

        final @NotNull Player player = event.getPlayer();
        final @Nullable User user = getUserByPlayer.apply(player);
        if (user == null) {
            event.setCancelled(true);
            return;
        }

        final @NotNull TeleportCause cause = event.getCause();
        if (cause != TeleportCause.NETHER_PORTAL) return;

        if (island.isUserForbidden(user, Permission.NETHER_PORTAL)) {
            event.setCancelled(true);
            return;
        }

        event.setCanCreatePortal(true);

        final @Nullable Location toLocation = event.getTo();
        if (toLocation == null) return;

        final @Nullable World toWorld = toLocation.getWorld();
        if (toWorld == null) return;

        final @NotNull Location homeLocation = island.getHome(toWorld);

        event.setTo(homeLocation);

        new IslandUserPortalEvent(island, user, fromLocation, toLocation, event.getCause());
    }

}
