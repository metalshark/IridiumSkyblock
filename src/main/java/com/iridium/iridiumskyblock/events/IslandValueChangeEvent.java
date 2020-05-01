package com.iridium.iridiumskyblock.events;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class IslandValueChangeEvent extends Event {

    private static final @NotNull HandlerList handlers = new HandlerList();

    @Getter private final @NotNull Island island;
    @Getter private final int value;

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}
