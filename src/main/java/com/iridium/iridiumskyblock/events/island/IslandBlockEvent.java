package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandBlockEvent extends IslandEvent {
    private static final HandlerList handlers = new HandlerList();

    @Getter private final @NotNull Block block;

    public IslandBlockEvent(final @NotNull Island island,
                            final @NotNull Block block) {
        super(island);
        this.block = block;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
