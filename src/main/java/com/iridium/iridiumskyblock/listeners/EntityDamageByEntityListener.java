package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Location;
import org.bukkit.World;
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

    @EventHandler
    @SuppressWarnings("UnnecessaryReturnStatement")
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        try {
            @NotNull final Entity damagee = event.getEntity();
            @NotNull final Location damageeLocation = damagee.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(damageeLocation);
            if (island == null) return;

            @NotNull final Entity damager = event.getDamager();

            // Using suppliers to defer work if unnecessary
            // This includes seemingly innocuous downcast operations
            @NotNull final Supplier<Player> damageePlayerSupplier = () -> (Player) damagee;
            @NotNull final Supplier<User> damageeUserSupplier = () -> User.getUser(damageePlayerSupplier.get());
            @NotNull final Supplier<Island> damageeIslandSupplier = () -> damageeUserSupplier.get().getIsland();
            @NotNull final Supplier<Arrow> arrowSupplier = () -> (Arrow) damager;
            @NotNull final Supplier<ProjectileSource> projectileSourceSupplier = () -> arrowSupplier.get().getShooter();
            @NotNull final Supplier<Player> shooterSupplier = () -> (Player) projectileSourceSupplier.get();
            @NotNull final Supplier<User> shootingUserSupplier = () -> User.getUser(Objects.requireNonNull(shooterSupplier.get()));
            @NotNull final Supplier<Player> damagingPlayerSupplier = () -> (Player) damager;
            @NotNull final Supplier<User> damagingUserSupplier = () -> User.getUser(damagingPlayerSupplier.get());

            // Deals with two players pvping in IridiumSkyblock world
            if (IridiumSkyblock.getConfiguration().disablePvPOnIslands
                    && damagee instanceof Player
                    && damager instanceof Player) {
                event.setCancelled(true);
                return;
            }

            // Deals with A player getting damaged by a bow fired from a player in IridiumSkyblock world
            if (IridiumSkyblock.getConfiguration().disablePvPOnIslands
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
                    && !island.getPermissions(shootingUserSupplier.get()).killMobs) {
                event.setCancelled(true);
                return;
            }

            // Deals with a player attacking animals that are not from their island
            if (damager instanceof Player
                    && !(damagee instanceof Player)
                    && !island.getPermissions(damagingUserSupplier.get()).killMobs) {
                event.setCancelled(true);
                return;
            }

            //Deals with a mob attacking a player that doesn't belong to the island (/is home traps?)
            if (IridiumSkyblock.getConfiguration().disablePvPOnIslands
                    && damagee instanceof Player
                    && !(damager instanceof Player)) {
                if (damageeIslandSupplier.get() != null) {
                    if (!damageeIslandSupplier.get().isInIsland(damager.getLocation())) {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    event.setCancelled(true);
                    return;
                }
            }

            // Deals with two allies pvping
            if (IridiumSkyblock.getConfiguration().disablePvPBetweenIslandMembers
                    && damagee instanceof Player
                    && damager instanceof Player
                    && damageeIslandSupplier.get() != null
                    && damageeIslandSupplier.get().equals(damagingUserSupplier.get().getIsland())) {
                event.setCancelled(true);
                return;
            }

            // Deals with two allies pvping with bows
            if (IridiumSkyblock.getConfiguration().disablePvPBetweenIslandMembers
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
    public void onVehicleDamage(@NotNull VehicleDamageEvent event) {
        try {
            @NotNull final Vehicle vehicle = event.getVehicle();
            @NotNull final Location location = vehicle.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            @Nullable final Entity attacker = event.getAttacker();
            if (!(attacker instanceof Player)) return;

            @NotNull final Player attackerPlayer = (Player) attacker;
            @NotNull final User attackerUser = User.getUser(attackerPlayer);

            if (!island.getPermissions(attackerUser).killMobs)
                event.setCancelled(true);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

}
