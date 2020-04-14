package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.MissionType;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(@NotNull EntityDeathEvent event) {
        try {
            @NotNull final LivingEntity entity = event.getEntity();
            @Nullable final Player killer = entity.getKiller();
            if (killer == null) return;

            @NotNull final Location location = killer.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;

            @NotNull final User user = User.getUser(killer);
            @NotNull final Island userIsland = user.getIsland();
            if (userIsland == null) return;

            for (@NotNull Mission mission : IridiumSkyblock.getMissions().missions) {
                @NotNull final Map<String, Integer> levels = userIsland.getMissionLevels();
                levels.putIfAbsent(mission.name, 1);

                @NotNull final MissionData level = mission.levels.get(levels.get(mission.name));
                if (level.type != MissionType.ENTITY_KILL) continue;

                @NotNull final List<String> conditions = level.conditions;
                if (conditions.isEmpty() || conditions.contains(entity.toString()))
                    userIsland.addMission(mission.name, 1);
            }

            if (userIsland.getExpBooster() != 0)
                event.setDroppedExp(event.getDroppedExp() * 2);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
