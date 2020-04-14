package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.configs.Config;
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
import org.bukkit.World;
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
import java.util.Map;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        try {
            @NotNull final Block block = event.getBlock();
            @NotNull final Location location = block.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            @NotNull final Player player = event.getPlayer();
            @NotNull final User user = User.getUser(player);

            @NotNull final Material material = block.getType();
            @NotNull final XMaterial xmaterial = XMaterial.matchXMaterial(material);
            @NotNull final Config config = IridiumSkyblock.getConfiguration();
            @NotNull final Integer max = config.limitedBlocks.get(xmaterial);
            if (max != null) {
                if (island.valuableBlocks.getOrDefault(xmaterial.name(), 0) >= max) {
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().blockLimitReached
                        .replace("%prefix%", config.prefix)));
                    event.setCancelled(true);
                    return;
                }
            }

            if (user.islandID == island.getId()) {
                for (@NotNull Mission mission : IridiumSkyblock.getMissions().missions) {
                    @NotNull final Map<String, Integer> levels = island.getMissionLevels();
                    levels.putIfAbsent(mission.name, 1);

                    @NotNull final MissionData level = mission.levels.get(levels.get(mission.name));
                    if (level == null) continue;
                    if (level.type != MissionType.BLOCK_PLACE) continue;

                    @NotNull final List<String> conditions = level.conditions;

                    if (
                            conditions.isEmpty()
                            ||
                            conditions.contains(xmaterial.name())
                            ||
                            conditions.contains(((Crops) block.getState().getData()).getState().toString())
                    )
                        island.addMission(mission.name, 1);
                }
            }

            if (!island.getPermissions(user).placeBlocks)
                event.setCancelled(true);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMonitorBlockPlace(@NotNull BlockPlaceEvent event) {
        try {
            @NotNull final Block block = event.getBlock();
            @NotNull final Location location = block.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            if (!Utils.isBlockValuable(block)) return;

            @NotNull final Material material = block.getType();
            @NotNull final XMaterial xmaterial = XMaterial.matchXMaterial(material);
            island.valuableBlocks.compute(xmaterial.name(), (name, original) -> {
                if (original == null) return 1;
                return original + 1;
            });
            if (island.updating)
                island.tempValues.add(location);
            island.calculateIslandValue();
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
