package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.MissionType;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.XMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Crops;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockBreakListener implements Listener {

    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        try {
            if (event.isCancelled()) return;
            final @NotNull Block block = event.getBlock();
            final @Nullable Island island = islandManager.getIslandByBlock(block);
            if (island == null) return;

            final @NotNull Player player = event.getPlayer();
            final @NotNull User user = User.getUser(player);
            if (!island.getPermissionsByUser(user).breakBlocks) {
                event.setCancelled(true);
                return;
            }

            if (user.getIslandId() != island.getId()) return;

            for (final @NotNull Mission mission : IridiumSkyblock.getMissions()) {
                final @NotNull String missionName = mission.name;
                final @Nullable MissionData missionData = island.getMissionLevel(missionName);
                if (missionData == null) continue;
                if (missionData.type != MissionType.BLOCK_BREAK) continue;

                final @NotNull List<String> conditions = missionData.conditions;
                if (
                    conditions.isEmpty()
                    || conditions.contains(XMaterial.matchXMaterial(block.getType()).name())
                    || (
                        block.getState().getData() instanceof Crops
                        && conditions.contains(((Crops) block.getState().getData()).getState().toString()))
                )
                    island.addMissionAmount(missionName, 1);
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onMonitorBreakBlock(@NotNull BlockBreakEvent event) {
        try {
            final @NotNull Block block = event.getBlock();
            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            if (Utils.isBlockValuable(block))
                island.removeValuableBlock(block);

            island.removeFailedGenerator(location);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
