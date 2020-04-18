package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class EntityDamageByEntityListener implements Listener {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        try {
            final @NotNull Entity damagee = event.getEntity();
            final @NotNull Location damageeLocation = damagee.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(damageeLocation);
            if (island == null) return;

            final @NotNull Entity damager = event.getDamager();

            // Using suppliers to defer work if unnecessary
            // This includes seemingly innocuous downcast operations
            final @NotNull Supplier<Player> damageePlayerSupplier = () -> (Player) damagee;
            final @NotNull Supplier<User> damageeUserSupplier = () -> User.getUser(damageePlayerSupplier.get());
            final @NotNull Supplier<Island> damageeIslandSupplier = () -> damageeUserSupplier.get().getIsland();
            final @NotNull Supplier<Arrow> arrowSupplier = () -> (Arrow) damager;
            final @NotNull Supplier<ProjectileSource> projectileSourceSupplier = () -> arrowSupplier.get().getShooter();
            final @NotNull Supplier<Player> shooterSupplier = () -> (Player) projectileSourceSupplier.get();
            final @NotNull Supplier<User> shootingUserSupplier = () -> User.getUser(Objects.requireNonNull(shooterSupplier.get()));
            final @NotNull Supplier<Player> damagingPlayerSupplier = () -> (Player) damager;
            final @NotNull Supplier<User> damagingUserSupplier = () -> User.getUser(damagingPlayerSupplier.get());

            // Deals with two players pvping in IridiumSkyblock world
            if (config.disablePvPOnIslands
                    && damagee instanceof Player
                    && damager instanceof Player) {
                event.setCancelled(true);
                return;
            }

            // Deals with A player getting damaged by a bow fired from a player in IridiumSkyblock world
            if (config.disablePvPOnIslands
                    && damagee instanceof Player
                    && damager instanceof Arrow
                    && projectileSourceSupplier.get() instanceof Player) {
                event.setCancelled(true);
                return;
            }

            // Deals with a player attacking animals with bows that are not from their island
            if (damager instanceof Arrow
                    && !(damagee instanceof Player)
                    && projectileSourceSupplier.get() instanceof Player
                    && !island.getPermissionsByUser(shootingUserSupplier.get()).killMobs) {
                event.setCancelled(true);
                return;
            }

            // Deals with a player attacking animals that are not from their island
            if (damager instanceof Player
                    && !(damagee instanceof Player)
                    && !island.getPermissionsByUser(damagingUserSupplier.get()).killMobs) {
                event.setCancelled(true);
                return;
            }

            //Deals with a mob attacking a player that doesn't belong to the island (/is home traps?)
            if (config.disablePvPOnIslands
                    && damagee instanceof Player
                    && !(damager instanceof Player)) {
                if (damageeIslandSupplier.get() != null) {
                    if (!damageeIslandSupplier.get().isEntityInIsland(damager)) {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    event.setCancelled(true);
                    return;
                }
            }

            // Deals with two allies pvping
            if (config.disablePvPBetweenIslandMembers
                    && damagee instanceof Player
                    && damager instanceof Player
                    && damageeIslandSupplier.get() != null
                    && damageeIslandSupplier.get().equals(damagingUserSupplier.get().getIsland())) {
                event.setCancelled(true);
                return;
            }

            // Deals with two allies pvping with bows
            if (config.disablePvPBetweenIslandMembers
                    && damagee instanceof Player
                    && damager instanceof Arrow
                    && projectileSourceSupplier.get() instanceof Player
                    && damageeIslandSupplier.get() != null
                    && damageeIslandSupplier.get().equals(damagingUserSupplier.get().getIsland())) {
                event.setCancelled(true);
                return;
            }
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onVehicleDamage(@NotNull VehicleDamageEvent event) {
        try {
            final @NotNull Vehicle vehicle = event.getVehicle();
            final @Nullable Island island = islandManager.getIslandByEntity(vehicle);
            if (island == null) return;

            final @Nullable Entity attacker = event.getAttacker();
            if (!(attacker instanceof Player)) return;

            final @NotNull Player attackerPlayer = (Player) attacker;
            final @NotNull User attackerUser = User.getUser(attackerPlayer);

            if (!island.getPermissionsByUser(attackerUser).killMobs)
                event.setCancelled(true);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

}
