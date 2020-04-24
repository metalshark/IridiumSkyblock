package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class IslandEntityDamageByEntityEvent extends IslandEntityEvent {

    @Getter private final @NotNull DamageCause cause;
    @Getter private final @NotNull Entity attacker;
    @Getter private final double damage;

    public IslandEntityDamageByEntityEvent(final @NotNull Island island,
                                           final @NotNull Entity victim,
                                           final @NotNull Entity attacker,
                                           final @NotNull DamageCause cause,
                                           final double damage) {
        super(island, victim);
        this.attacker = attacker;
        this.cause = cause;
        this.damage = damage;
    }

    public @NotNull Entity getVictim() {
        return super.getEntity();
    }

}
