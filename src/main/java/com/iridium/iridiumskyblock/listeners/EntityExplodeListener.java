package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onEntityExplode(@NotNull EntityExplodeEvent event) {
        try {
            @NotNull final Entity entity = event.getEntity();
            @NotNull final Location location = entity.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;

            if (!IridiumSkyblock.getConfiguration().allowExplosions)
                event.setCancelled(true);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMonitorEntityExplode(@NotNull EntityExplodeEvent event) {
        try {
            @NotNull final Entity entity = event.getEntity();
            @NotNull final Location location = entity.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;

            @NotNull final UUID uuid = entity.getUniqueId();
            @NotNull final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
            @NotNull final Map<UUID, Island> entities = plugin.entities;
            Island island = entities.get(uuid);
            if (island != null && island.isInIsland(location)) {
                event.setCancelled(true);
                entity.remove();
                entities.remove(uuid);
                return;
            }

            island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            for (@NotNull Block block : event.blockList()) {
                if (!island.isInIsland(block.getLocation())) {
                    @NotNull final BlockState state = block.getState();
                    IridiumSkyblock.nms.setBlockFast(block, 0, (byte) 0);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> state.update(true, true));
                }

                if (!Utils.isBlockValuable(block)) continue;

                if (!(block.getState() instanceof CreatureSpawner)) {
                    @NotNull final Material material = block.getType();
                    @NotNull final XMaterial xmaterial = XMaterial.matchXMaterial(material);
                    island.valuableBlocks.computeIfPresent(xmaterial.name(), (name, original) -> original - 1);
                }

                if (island.updating)
                    island.tempValues.remove(block.getLocation());
            }
            island.calculateIslandValue();
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

}
