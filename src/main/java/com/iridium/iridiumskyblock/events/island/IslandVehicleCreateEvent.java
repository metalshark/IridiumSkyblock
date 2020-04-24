package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.jetbrains.annotations.NotNull;

public class IslandVehicleCreateEvent extends IslandEntityEvent {

    public IslandVehicleCreateEvent(final @NotNull Island island,
                                    final @NotNull Entity vehicle) {
        super(island, vehicle);
    }

    public @NotNull Vehicle getVehicle() {
        return (Vehicle) super.getEntity();
    }

}
