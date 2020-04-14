package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IslandCreateEvent extends IslandEvent {

    @Getter @NotNull private final Player player;

    public IslandCreateEvent(@NotNull Player player, @NotNull Island island)  {
        super(island);
        this.player = player;
    }

}
