package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.events.island.IslandCaughtFishEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class PlayerFishListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull Function<Player, User> getUserByPlayer;

    @EventHandler
    public void onPlayerFish(final @NotNull PlayerFishEvent event) {
        final @NotNull Player player = event.getPlayer();
        final @Nullable Island island = getIslandByLocation.apply(player.getLocation());
        if (island == null) return;

        final @Nullable Entity caught = event.getCaught();
        if (caught == null) return;

        if (!island.isOnIsland(caught.getLocation())) {
            event.setCancelled(true);
            return;
        }

        final @NotNull State state = event.getState();
        if (state != State.CAUGHT_FISH) return;

        final @Nullable User user = getUserByPlayer.apply(player);
        if (user == null) return;

        if (island.isUserForbidden(user, Permission.CATCH_FISH)) {
            event.setCancelled(true);
            return;
        }

        new IslandCaughtFishEvent(island, caught, event.getHook(), event.getState(), user);
    }

}
