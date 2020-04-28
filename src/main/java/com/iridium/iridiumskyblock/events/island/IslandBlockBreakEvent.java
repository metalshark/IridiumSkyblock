package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class IslandBlockBreakEvent extends IslandBlockEvent {

    @Getter private final @NotNull User user;
    @Getter @Setter private boolean dropItems;
    @Getter @Setter private int experienceToDrop;

    public IslandBlockBreakEvent(final @NotNull Island island,
                                 final @NotNull Block block,
                                 final @NotNull User user,
                                 final int experienceToDrop,
                                 final boolean dropItems) {
        super(island, block);
        this.user = user;
        this.experienceToDrop = experienceToDrop;
        this.dropItems = dropItems;
    }

}
