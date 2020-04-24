package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class IslandEntityPickupItemEvent extends IslandEntityEvent {

    @Getter private final @NotNull Item item;
    @Getter private final int remaining;

    public IslandEntityPickupItemEvent(final @NotNull Island island,
                                       final @NotNull Entity entity,
                                       final @NotNull Item item,
                                       final int remaining) {
        super(island, entity);
        this.item = item;
        this.remaining = remaining;
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return (LivingEntity) super.getEntity();
    }

}
