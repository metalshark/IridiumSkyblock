package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Messages;
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Crops;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockPlaceListener implements Listener {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private static final @NotNull Messages messages = IridiumSkyblock.getMessages();

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        try {
            final @NotNull Block block = event.getBlock();
            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            final @NotNull Player player = event.getPlayer();
            final @NotNull User user = User.getUser(player);
            if (!island.getPermissionsByUser(user).placeBlocks) {
                event.setCancelled(true);
                return;
            }

            final @NotNull Material material = block.getType();
            final @NotNull XMaterial xmaterial = XMaterial.matchXMaterial(material);
            final @NotNull Integer max = config.limitedBlocks.get(xmaterial);
            if (max != null) {
                if (island.getValuableBlockCountByName(xmaterial.name()) >= max) {
                    player.sendMessage(Utils.color(messages.blockLimitReached
                        .replace("%prefix%", config.prefix)));
                    event.setCancelled(true);
                    return;
                }
            }

            if (user.getIslandId() != island.getId()) return;

            for (final @NotNull Mission mission : IridiumSkyblock.getMissions()) {
                final @NotNull String missionName = mission.name;
                final @Nullable MissionData missionData = island.getMissionLevel(missionName);
                if (missionData == null) continue;
                if (missionData.type != MissionType.BLOCK_PLACE) continue;

                final @NotNull List<String> conditions = missionData.conditions;
                if (
                    conditions.isEmpty()
                        || conditions.contains(xmaterial.name())
                        || conditions.contains(((Crops) block.getState().getData()).getState().toString())
                )
                    island.addMissionAmount(missionName, 1);
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onMonitorBlockPlace(@NotNull BlockPlaceEvent event) {
        try {
            final @NotNull Block block = event.getBlock();
            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            if (!Utils.isBlockValuable(block)) return;
            island.addValuableBlock(block);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
