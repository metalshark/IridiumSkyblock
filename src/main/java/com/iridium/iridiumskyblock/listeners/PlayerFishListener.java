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
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PlayerFishListener implements Listener {

    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerFish(@NotNull PlayerFishEvent event) {
        try {
            final @NotNull Player player = event.getPlayer();
            if (!islandManager.isIslandWorldEntity(player)) return;
            if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

            final @NotNull User user = User.getUser(player);
            final @Nullable Island island = user.getIsland();
            if (island == null) return;

            for (final @NotNull Mission mission : IridiumSkyblock.getMissions()) {
                final @NotNull String missionName = mission.name;
                final @Nullable MissionData level = island.getMissionLevel(missionName);
                if (level == null) continue;
                if (level.type != MissionType.FISH_CATCH) continue;

                island.addMissionAmount(missionName, 1);
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
