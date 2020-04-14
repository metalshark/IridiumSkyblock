package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class IslandDemoteEvent extends IslandEvent {

    @Getter @NotNull private final User target;
    @Getter @NotNull private final User demoter;
    @Getter @NotNull private final Role role;
    @Getter @Setter private boolean cancelled;

    public IslandDemoteEvent(@NotNull Island island, @NotNull User target, @NotNull User demoter, @NotNull Role role) {
        super(island);
        this.target = target;
        this.demoter = demoter;
        this.role = role;
    }

}
