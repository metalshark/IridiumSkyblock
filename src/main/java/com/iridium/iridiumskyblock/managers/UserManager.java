package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnnecessaryReturnStatement")
public class UserManager implements Listener {

    private final @NotNull IridiumSkyblock plugin;

    public UserManager(final @NotNull IridiumSkyblock plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public @Nullable User getUserByPlayer(final @NotNull Player player) {
        throw new UnsupportedOperationException();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(event.getBlock().getLocation());
        if (island == null) return;

        final @Nullable User user = getUserByPlayer(event.getPlayer());
        if (user == null) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getDatabaseManager().isUserForbidden(island, user, User.Permission.BLOCK_BREAK)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(event.getBlock().getLocation());
        if (island == null) return;

        // Only users can place blocks on islands
        final @Nullable User user = getUserByPlayer(event.getPlayer());
        if (user == null) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getDatabaseManager().isUserForbidden(island, user, User.Permission.BLOCK_PLACE)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(final @NotNull EntityDamageByEntityEvent event) {
        final @NotNull Entity victim = event.getEntity();
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(victim.getLocation());
        if (island == null) return;

        final @NotNull Entity attacker = event.getDamager();
        @Nullable Mob attackerMob = (attacker instanceof Mob) ? (Mob) attacker : null;
        @Nullable Player attackerPlayer = (attacker instanceof Player) ? (Player) attacker : null;

        // Get the shooting player/mob if an arrow caused the damage
        if (attacker instanceof Arrow) {
            final @Nullable Arrow attackerArrow = (Arrow) attacker;
            final @Nullable ProjectileSource shooter = attackerArrow.getShooter();
            if (shooter instanceof Mob) attackerMob = (Mob) shooter;
            if (shooter instanceof Player) attackerPlayer = (Player) shooter;
        }

        final @Nullable User attackerUser = (attackerPlayer == null) ? null : getUserByPlayer(attackerPlayer);

        // Mobs on other islands cannot attack
        if (attackerMob != null && island.isNotOnIsland(attacker.getLocation())) {
            event.setCancelled(true);
            return;
        }

        // Cannot damage others without permission
        if (attackerUser != null) {

            if (victim instanceof Animals
                && plugin.getDatabaseManager().isUserForbidden(island, attackerUser, User.Permission.DAMAGE_ANIMAL)) {
                event.setCancelled(true);
                return;
            }

            if (victim instanceof Mob
                && plugin.getDatabaseManager().isUserForbidden(island, attackerUser, User.Permission.DAMAGE_MOB)) {
                event.setCancelled(true);
                return;
            }

            if (victim instanceof Player
                && plugin.getDatabaseManager().isUserForbidden(island, attackerUser, User.Permission.DAMAGE_PLAYER)) {
                event.setCancelled(true);
                return;
            }

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityPickupItem(final @NotNull EntityPickupItemEvent event) {
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(event.getItem().getLocation());
        if (island == null) return;

        final @NotNull Entity entity = event.getEntity();
        if (island.isNotOnIsland(entity.getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (!(entity instanceof Player)) return;
        final @NotNull Player player = (Player) entity;
        final @Nullable User user = getUserByPlayer(player);
        if (user == null) return;

        if (plugin.getDatabaseManager().isUserForbidden(island, user, User.Permission.PICKUP_ITEM)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFish(final @NotNull PlayerFishEvent event) {
        final @NotNull Player player = event.getPlayer();
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(player.getLocation());
        if (island == null) return;

        final @Nullable Entity caught = event.getCaught();
        if (caught == null) return;

        if (island.isNotOnIsland(caught.getLocation())) {
            event.setCancelled(true);
            return;
        }

        final @NotNull PlayerFishEvent.State state = event.getState();
        if (state != PlayerFishEvent.State.CAUGHT_FISH) return;

        final @Nullable User user = getUserByPlayer(player);
        if (user == null) return;

        if (plugin.getDatabaseManager().isUserForbidden(island, user, User.Permission.CATCH_FISH)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPortal(final @NotNull PlayerPortalEvent event) {
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(event.getFrom());
        if (island == null) return;

        final @Nullable User user = getUserByPlayer(event.getPlayer());
        if (user == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
            && plugin.getDatabaseManager().isUserForbidden(island, user, User.Permission.NETHER_PORTAL)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(final @NotNull PlayerTeleportEvent event) {
        final @Nullable Location toLocation = event.getTo();
        if (toLocation == null) return;

        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(event.getTo());
        if (island == null) return;

        // Prevent teleporting between islands
        final @NotNull Location fromLocation = event.getFrom();
        if (island.isNotOnIsland(fromLocation) && plugin.getIslandManager().getIslandByLocation(fromLocation) != null) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDamage(final @NotNull VehicleDamageEvent event) {
        final @Nullable Island island = plugin.getIslandManager().getIslandByLocation(event.getVehicle().getLocation());
        if (island == null) return;

        final @Nullable Entity attacker = event.getAttacker();
        if (attacker == null) return;

        @Nullable Mob attackerMob = (attacker instanceof Mob) ? (Mob) attacker : null;
        @Nullable Player attackerPlayer = (attacker instanceof Player) ? (Player) attacker : null;

        if (attacker instanceof Arrow) {
            final @Nullable Arrow attackerArrow = (Arrow) attacker;
            final @Nullable ProjectileSource shooter = attackerArrow.getShooter();
            if (shooter instanceof Mob) attackerMob = (Mob) shooter;
            if (shooter instanceof Player) attackerPlayer = (Player) shooter;
        }
        final @Nullable User attackerUser = (attackerPlayer == null) ? null : getUserByPlayer(attackerPlayer);

        // Mobs on other islands cannot attack
        if (attackerMob != null && island.isNotOnIsland(attacker.getLocation())) {
            event.setCancelled(true);
            return;
        }

        // Cannot damage others without permission
        if (attackerUser != null
            && plugin.getDatabaseManager().isUserForbidden(island, attackerUser, User.Permission.DAMAGE_VEHICLE)) {
            event.setCancelled(true);
            return;
        }
    }

}
