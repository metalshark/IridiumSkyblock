package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityPickupItemListener implements Listener {

    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onEntityPickupItem(@NotNull PlayerPickupItemEvent event) {
        try {
            final @NotNull Item item = event.getItem();
            final @Nullable Island island = islandManager.getIslandByItem(item);
            if (island == null) return;

            final @NotNull Player player = event.getPlayer();
            final @NotNull User user = User.getUser(player);
            if (!island.getPermissionsByUser(user).pickupItems)
                event.setCancelled(true);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

}
