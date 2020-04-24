package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class IslandBlockPlaceEvent extends IslandBlockEvent {

    @Getter private final @NotNull User user;

    public IslandBlockPlaceEvent(final @NotNull Island island,
                                 final @NotNull Block block,
                                 final @NotNull User user) {
        super(island, block);
        this.user = user;
    }

}
