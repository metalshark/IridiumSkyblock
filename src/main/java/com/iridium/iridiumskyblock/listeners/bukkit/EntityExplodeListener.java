package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandEntityExplodeEvent;
import com.iridium.iridiumskyblock.utilities.MinecraftReflection;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class EntityExplodeListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull MinecraftReflection minecraftReflection;
    private final @NotNull BiConsumer<Runnable, Long> runTaskLater;

    @EventHandler
    private void onEntityExplode(final @NotNull EntityExplodeEvent event) {
        final @NotNull Location location = event.getLocation();
        final @Nullable Island island = getIslandByLocation.apply(location);
        if (island == null) return;

        if (!island.getConfiguration().isExplosionsEnabled()) {
            event.setCancelled(true);
            return;
        }

        final @NotNull List<Block> blocks = event.blockList();
        blocks.stream()
            .filter(block -> !island.isOnIsland(block.getLocation()))
            .forEach(block -> {
                minecraftReflection.setBlockFast(block, 0, (byte) 0x00);
                final @NotNull BlockState blockState = block.getState();
                runTaskLater.accept(() -> blockState.update(true, true), 0L);
            });

        new IslandEntityExplodeEvent(island, event.getEntity(), location, blocks, event.getYield());
    }

}
