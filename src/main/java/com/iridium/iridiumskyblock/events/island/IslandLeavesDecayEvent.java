package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandLeavesDecayEvent extends IslandBlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public IslandLeavesDecayEvent(final @NotNull Island island,
                                  final @NotNull Block block) {
        super(island, block);
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
