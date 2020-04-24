package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

public class IslandUserTeleportEvent extends IslandUserEvent {

    @Getter private final @NotNull Location from;
    @Getter private final @NotNull Location to;
    @Getter private final @NotNull TeleportCause cause;

    public IslandUserTeleportEvent(final @NotNull Island island,
                                   final @NotNull User user,
                                   final @NotNull Location from,
                                   final @NotNull Location to,
                                   final @NotNull TeleportCause cause) {
        super(island, user);
        this.from = from;
        this.to = to;
        this.cause = cause;
    }

}
