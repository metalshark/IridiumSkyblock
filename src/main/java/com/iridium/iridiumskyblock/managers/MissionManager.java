package com.iridium.iridiumskyblock.managers;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Mission;
import com.iridium.iridiumskyblock.events.IslandValueChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MissionManager implements Listener {

    private final @NotNull IridiumSkyblock plugin;

    public MissionManager(final @NotNull IridiumSkyblock plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private static boolean isRequiredBlock(final @NotNull Mission.Level level, final @NotNull Block block) {
        final @NotNull Set<String> requirements = level.getRequirements();
        if (requirements.isEmpty()) return true;

        // Check the block is one of the required types
        final @NotNull Material material = block.getType();
        final @NotNull XMaterial xMaterial = XMaterial.matchXMaterial(material);
        if (requirements.contains(xMaterial.name())) return true;

        // Check the crop is in one of the required states
        if (!XBlock.isCrops(material)) return false;
        final int age = XBlock.getAge(block);
        final @NotNull CropState cropState = CropState.values()[age];
        return requirements.contains(cropState.toString());
    }

    private static boolean isRequiredEntity(final @NotNull Mission.Level level, final @NotNull Entity entity) {
        final @NotNull Set<String> requirements = level.getRequirements();
        if (requirements.isEmpty()) return true;

        // Check the entity is one of the required types
        final @NotNull EntityType entityType = entity.getType();
        return (requirements.contains(entityType.toString()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = plugin.getDatabaseManager().getIslandByLocation(block.getLocation());
        if (island == null) return;

        plugin.getDatabaseManager().getIslandMissionsByType(island, Mission.Type.BLOCK_BREAK).stream()
            .filter(level -> isRequiredBlock(level, block))
            .forEach(level -> level.advance(island, 1));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = plugin.getDatabaseManager().getIslandByLocation(block.getLocation());
        if (island == null) return;

        plugin.getDatabaseManager().getIslandMissionsByType(island, Mission.Type.BLOCK_PLACE).stream()
            .filter(level -> isRequiredBlock(level, block))
            .forEach(level -> level.advance(island, 1));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(final @NotNull EntityDeathEvent event) {
        final @NotNull LivingEntity entity = event.getEntity();
        final @Nullable Island island = plugin.getDatabaseManager().getIslandByLocation(entity.getLocation());
        if (island == null) return;

        plugin.getDatabaseManager().getIslandMissionsByType(island, Mission.Type.ENTITY_KILL).stream()
            .filter(level -> isRequiredEntity(level, entity))
            .forEach(level -> level.advance(island, 1));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandValueChange(final @NotNull IslandValueChangeEvent event) {
        final @NotNull Island island = event.getIsland();
        plugin.getDatabaseManager().getIslandMissionsByType(island, Mission.Type.VALUE_INCREASE).stream()
            .filter(level -> event.getValue() >= level.getAmount())
            .forEach(level -> level.complete(island));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerExpChange(final @NotNull PlayerExpChangeEvent event) {
        final @Nullable Island island = plugin.getDatabaseManager().getIslandByLocation(event.getPlayer().getLocation());
        if (island == null) return;

        final int amount = event.getAmount();
        plugin.getDatabaseManager().getIslandMissionsByType(island, Mission.Type.EXPERIENCE)
            .forEach(level -> level.advance(island, amount));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(final @NotNull PlayerFishEvent event) {
        final @Nullable Island island = plugin.getDatabaseManager().getIslandByLocation(event.getPlayer().getLocation());
        if (island == null) return;

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        final @Nullable Entity entity = event.getCaught();
        if (entity == null) return;

        plugin.getDatabaseManager().getIslandMissionsByType(island, Mission.Type.FISH_CATCH).stream()
            .filter(level -> isRequiredEntity(level, entity))
            .forEach(level -> level.advance(island, 1));
    }

}
