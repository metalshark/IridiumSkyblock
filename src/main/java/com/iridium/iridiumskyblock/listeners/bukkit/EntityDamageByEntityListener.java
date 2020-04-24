package com.iridium.iridiumskyblock.listeners.bukkit;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.events.island.IslandEntityDamageByEntityEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public class EntityDamageByEntityListener implements Listener {

    private final @NotNull Function<Location, Island> getIslandByLocation;
    private final @NotNull Function<Player, User> getUserByPlayer;

    @EventHandler
    public void onEntityDamageByEntity(final @NotNull EntityDamageByEntityEvent event) {
        final @NotNull Entity victim = event.getEntity();
        final @Nullable Island island = getIslandByLocation.apply(victim.getLocation());
        if (island == null) return;

        final @NotNull Entity attacker = event.getDamager();
        @Nullable Mob attackerMob = (attacker instanceof Mob) ? (Mob) attacker : null;
        @Nullable Player attackerPlayer = (attacker instanceof Player) ? (Player) attacker : null;

        final @Nullable Arrow attackerArrow = (attacker instanceof Arrow) ? (Arrow) attacker : null;
        final @Nullable ProjectileSource shooter = (attackerArrow == null) ? null : attackerArrow.getShooter();
        if (shooter instanceof Mob) attackerMob = (Mob) shooter;
        if (shooter instanceof Player) attackerPlayer = (Player) shooter;
        final @Nullable User attackerUser = (attackerPlayer == null) ? null : getUserByPlayer.apply(attackerPlayer);

        final boolean attackerOnDamageeIsland = island.isOnIsland(attacker.getLocation());

        // Mobs on other islands cannot attack
        if (attackerMob != null && !attackerOnDamageeIsland) {
            event.setCancelled(true);
            return;
        }

        // Cannot damage others without permission
        if (attackerUser != null) {

            if (victim instanceof Animals && island.isUserForbidden(attackerUser, Permission.DAMAGE_ANIMAL)) {
                event.setCancelled(true);
                return;
            }

            if (victim instanceof Mob && island.isUserForbidden(attackerUser, Permission.DAMAGE_MOB)) {
                event.setCancelled(true);
                return;
            }

            if (victim instanceof Player && island.isUserForbidden(attackerUser, Permission.DAMAGE_PLAYER)) {
                event.setCancelled(true);
                return;
            }

        }

        new IslandEntityDamageByEntityEvent(island, victim, attacker, event.getCause(), event.getDamage());
    }

}
