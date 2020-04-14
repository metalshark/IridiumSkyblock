package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class IslandPromoteEvent extends IslandEvent {

    @Getter @NotNull private final User target;
    @Getter @NotNull private final User promoter;
    @Getter @NotNull private final Role role;
    @Getter @Setter private boolean cancelled;

    public IslandPromoteEvent(@NotNull Island island, @NotNull User target, @NotNull User promoter, @NotNull Role role) {
        super(island);
        this.target = target;
        this.promoter = promoter;
        this.role = role;
    }

}
