package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.events.island.IslandEntityDeathEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class EntityDeathListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull Function<Player, User> getUserByPlayer;

    @EventHandler
    public void onEntityDeath(final @NotNull EntityDeathEvent event) {
        final @NotNull LivingEntity deceased = event.getEntity();
        final @Nullable Island deceasedIsland = getIslandByLocation.apply(deceased.getLocation());
        if (deceasedIsland == null) return;

        final @Nullable Player killer = deceased.getKiller();
        if (killer == null) return;

        final @Nullable User killingUser = getUserByPlayer.apply(killer);
        if (killingUser == null) return;

        new IslandEntityDeathEvent(deceasedIsland, deceased, event.getDrops(), event.getDroppedExp());
    }

}
