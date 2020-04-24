package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandBlockFromToEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class BlockFromToListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;

    @EventHandler
    public void onBlockFromTo(final @NotNull BlockFromToEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = getIslandByLocation.apply(block.getLocation());
        if (island == null) return;

        new IslandBlockFromToEvent(island, block, event.getToBlock());
    }

}
