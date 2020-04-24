package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.events.island.IslandLeavesDecayEvent;
import com.iridium.iridiumskyblock.events.island.IslandVehicleDamage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class VehicleDamageListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull Function<Player, User> getUserByPlayer;
    private final @NotNull Consumer<Event> callEvent;

    @EventHandler
    public void onVehicleDamage(final @NotNull VehicleDamageEvent event) {
        final @NotNull Vehicle vehicle = event.getVehicle();
        final @Nullable Island vehicleIsland = getIslandByLocation.apply(vehicle.getLocation());
        if (vehicleIsland == null) return;

        final @Nullable Entity attacker = event.getAttacker();
        if (attacker == null) return;

        @Nullable Mob attackerMob = (attacker instanceof Mob) ? (Mob) attacker : null;
        @Nullable Player attackerPlayer = (attacker instanceof Player) ? (Player) attacker : null;

        final @Nullable Arrow attackerArrow = (attacker instanceof Arrow) ? (Arrow) attacker : null;
        final @Nullable ProjectileSource shooter = (attackerArrow == null) ? null : attackerArrow.getShooter();
        if (shooter instanceof Mob) attackerMob = (Mob) shooter;
        if (shooter instanceof Player) attackerPlayer = (Player) shooter;
        final @Nullable User attackerUser = (attackerPlayer == null) ? null : getUserByPlayer.apply(attackerPlayer);

        // Mobs on other islands cannot attack
        if (attackerMob != null && !vehicleIsland.isOnIsland(attacker.getLocation())) {
            event.setCancelled(true);
            return;
        }

        // Cannot damage others without permission
        if (attackerUser != null && vehicleIsland.isUserForbidden(attackerUser, Permission.DAMAGE_VEHICLE)) {
            event.setCancelled(true);
            return;
        }

        final @NotNull IslandVehicleDamage islandEvent = new IslandVehicleDamage(vehicleIsland, vehicle, attacker, event.getDamage());
        callEvent.accept(islandEvent);
        if (islandEvent.isCancelled())
            event.setCancelled(true);
    }

}
