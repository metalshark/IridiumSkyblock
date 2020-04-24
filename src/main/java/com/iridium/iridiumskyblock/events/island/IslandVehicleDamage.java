package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class IslandVehicleDamage extends IslandEntityEvent {

    @Getter private final @NotNull Entity attacker;
    @Getter private final double damage;

    public IslandVehicleDamage(final @NotNull Island island,
                               final @NotNull Vehicle vehicle,
                               final @NotNull Entity attacker,
                               final double damage) {
        super(island, vehicle);
        this.attacker = attacker;
        this.damage = damage;
    }

    public @NotNull Vehicle getVehicle() {
        return (Vehicle) super.getEntity();
    }

}
