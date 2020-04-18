package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.MissionType;
import com.iridium.iridiumskyblock.User;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityDeathListener implements Listener {

    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onEntityDeath(@NotNull EntityDeathEvent event) {
        try {
            final @NotNull LivingEntity entity = event.getEntity();
            final @Nullable Player killer = entity.getKiller();
            if (killer == null) return;

            if (!islandManager.isIslandWorldEntity(killer)) return;

            final @NotNull User user = User.getUser(killer);
            final @Nullable Island userIsland = user.getIsland();
            if (userIsland == null) return;

            for (final @NotNull Mission mission : IridiumSkyblock.getMissions()) {
                final @Nullable MissionData level = userIsland.getMissionLevel(mission.name);
                if (level == null) continue;
                if (level.type != MissionType.ENTITY_KILL) continue;

                final @NotNull List<String> conditions = level.conditions;
                if (conditions.isEmpty() || conditions.contains(entity.toString()))
                    userIsland.addMissionAmount(mission.name, 1);
            }

            if (userIsland.getExpBooster() != 0)
                event.setDroppedExp(event.getDroppedExp() * 2);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
