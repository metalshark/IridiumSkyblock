package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandLeavesDecayEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class LeavesDecayListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull Consumer<Event> callEvent;

    @EventHandler
    public void onLeavesDecay(final @NotNull LeavesDecayEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = getIslandByLocation.apply(block.getLocation());
        if (island == null) return;

        if (!island.getConfiguration().isLeavesDecayEnabled()) {
            event.setCancelled(true);
            return;
        }

        final @NotNull IslandLeavesDecayEvent islandEvent = new IslandLeavesDecayEvent(island, block);
        callEvent.accept(islandEvent);
        if (islandEvent.isCancelled())
            event.setCancelled(true);
    }

}
