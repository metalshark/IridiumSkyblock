package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class IslandEvent extends Event {

    @NotNull private static final HandlerList handlers = new HandlerList();

    @Getter
    @NotNull private final Island island;

    @Override
    @NotNull public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
