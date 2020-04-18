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
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PlayerExpChangeListener implements Listener {

    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerExpChange(@NotNull PlayerExpChangeEvent event) {
        try {
            final @NotNull Player player = event.getPlayer();
            final @NotNull Location location = player.getLocation();
            if (!islandManager.isIslandWorldLocation(location)) return;

            final @NotNull User user = User.getUser(player);
            final @Nullable Island island = user.getIsland();
            if (island == null) return;

            for (final @NotNull Mission mission : IridiumSkyblock.getMissions()) {
                final @NotNull String missionName = mission.name;
                final @Nullable MissionData level = island.getMissionLevel(missionName);
                if (level == null) continue;
                if (level.type != MissionType.EXPERIENCE) continue;

                island.addMissionAmount(mission.name, event.getAmount());
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
