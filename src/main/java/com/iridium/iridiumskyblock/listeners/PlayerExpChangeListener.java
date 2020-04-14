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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerExpChangeListener implements Listener {

    @EventHandler
    public void onPlayerExpChange(@NotNull PlayerExpChangeEvent event) {
        try {
            @NotNull final Player player = event.getPlayer();
            @NotNull final Location location = player.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;

            @NotNull final User user = User.getUser(player);
            @NotNull final Island island = user.getIsland();
            if (island == null) return;

            for (@NotNull Mission mission : IridiumSkyblock.getMissions().missions) {
                @NotNull final Map<String, Integer> levels = island.getMissionLevels();
                levels.putIfAbsent(mission.name, 1);

                @NotNull final MissionData level = mission.levels.get(levels.get(mission.name));
                if (level.type == MissionType.EXPERIENCE)
                    island.addMission(mission.name, event.getAmount());
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
