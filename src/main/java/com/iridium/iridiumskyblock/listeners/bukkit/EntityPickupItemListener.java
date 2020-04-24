package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.events.island.IslandEntityPickupItemEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class EntityPickupItemListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull Function<Player, User> getUserByPlayer;

    @EventHandler
    public void onEntityPickupItem(final @NotNull EntityPickupItemEvent event) {
        final @NotNull Item item = event.getItem();
        final @Nullable Island island = getIslandByLocation.apply(item.getLocation());
        if (island == null) return;

        final @NotNull Entity entity = event.getEntity();
        if (!island.isOnIsland(entity.getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (!(entity instanceof Player)) return;
        final @NotNull Player player = (Player) entity;
        final @NotNull User user = getUserByPlayer.apply(player);
        if (!island.isUserForbidden(user, Permission.PICKUP_ITEM)) {
            event.setCancelled(true);
            return;
        }

        new IslandEntityPickupItemEvent(island, entity, item, event.getRemaining());
    }

}
