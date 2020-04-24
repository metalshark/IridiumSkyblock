package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

public class IslandUserEvent extends IslandEvent {

    @Getter private final @NotNull User user;

    public IslandUserEvent(final @NotNull Island island,
                           final @NotNull User user) {
        super(island);
        this.user = user;
    }

}
