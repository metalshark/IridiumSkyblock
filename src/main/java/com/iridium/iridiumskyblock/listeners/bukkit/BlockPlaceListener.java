package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.events.island.IslandBlockPlaceEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class BlockPlaceListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull Function<Player, User> getUserByPlayer;

    @EventHandler
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = getIslandByLocation.apply(block.getLocation());
        if (island == null) return;

        final @NotNull Player player = event.getPlayer();
        final @Nullable User user = getUserByPlayer.apply(player);
        if (user == null) {
            event.setCancelled(true);
            return;
        }

        if (island.isUserForbidden(user, Permission.BLOCK_PLACE)) {
            event.setCancelled(true);
            return;
        }

        new IslandBlockPlaceEvent(island, block, user);
    }

}
