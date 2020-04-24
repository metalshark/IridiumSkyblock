package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.events.island.IslandUserTeleportEvent;
import com.iridium.iridiumskyblock.utilities.MinecraftReflection;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class PlayerTeleportListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull MinecraftReflection minecraftReflection;
    private final @NotNull BiConsumer<Runnable, Long> runTaskLater;
    private final @NotNull Function<Player, User> getUserByPlayer;

    @EventHandler
    public void onPlayerTeleport(final @NotNull PlayerTeleportEvent event) {
        final @Nullable Location toLocation = event.getTo();
        if (toLocation == null) return;

        final @Nullable Island island = getIslandByLocation.apply(toLocation);
        if (island == null) return;

        final @NotNull Player player = event.getPlayer();
        final @Nullable World toWorld = toLocation.getWorld();
        if (toWorld == null) return;

        final @NotNull Location center = island.getCenter(toLocation.getWorld());
        runTaskLater.accept(() -> minecraftReflection
            .sendWorldBorder(player, island.getConfiguration().getBorderColor(), island.getSize(), center), 0L);

        final @Nullable User user = getUserByPlayer.apply(player);
        if (user == null) return;

        final @NotNull Location fromLocation = event.getFrom();
        if (!island.isOnIsland(fromLocation)) {

            // Prevent teleporting between islands
            if (getIslandByLocation.apply(fromLocation) != null)
                event.setCancelled(true);

            return;
        }

        new IslandUserTeleportEvent(island, user, fromLocation, toLocation, event.getCause());
    }

}
