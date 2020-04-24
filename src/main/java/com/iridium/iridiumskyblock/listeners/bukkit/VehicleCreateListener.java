package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.events.island.IslandVehicleCreateEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class VehicleCreateListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;

    @EventHandler
    public void onVehicleCreate(final @NotNull VehicleCreateEvent event) {
        final @NotNull Vehicle vehicle = event.getVehicle();
        final @Nullable Island island = getIslandByLocation.apply(vehicle.getLocation());
        if (island == null) return;

        new IslandVehicleCreateEvent(island, vehicle);
    }

}
