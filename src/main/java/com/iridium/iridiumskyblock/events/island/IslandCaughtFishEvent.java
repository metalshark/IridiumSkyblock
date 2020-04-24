package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.jetbrains.annotations.NotNull;

public class IslandCaughtFishEvent extends IslandEntityEvent {

    @Getter private final @NotNull User user;
    @Getter private final @NotNull FishHook hook;
    @Getter private final @NotNull State state;

    public IslandCaughtFishEvent(final @NotNull Island island,
                                 final @NotNull Entity caught,
                                 final @NotNull FishHook hook,
                                 final @NotNull State state,
                                 final @NotNull User user) {
        super(island, caught);
        this.hook = hook;
        this.state = state;
        this.user = user;
    }

    public @NotNull Entity getCaught() {
        return super.getEntity();
    }

}
