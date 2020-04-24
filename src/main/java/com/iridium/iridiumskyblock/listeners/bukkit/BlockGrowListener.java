package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandBlockGrowEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class BlockGrowListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;

    @EventHandler
    public void onBlockGrow(final @NotNull BlockGrowEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = getIslandByLocation.apply(block.getLocation());
        if (island == null) return;

        if (island.getCancelBlockGrow(block))
            event.setCancelled(true);

        new IslandBlockGrowEvent(island, block, event.getNewState());
    }

}
