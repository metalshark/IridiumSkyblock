package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.CropState;
import org.bukkit.Location;
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
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class Mission implements Listener {

    public enum Type {
        BLOCK_BREAK,
        BLOCK_PLACE,
        ENTITY_KILL,
        FISH_CATCH,
        VALUE_INCREASE,
        EXPERIENCE
    }

    @RequiredArgsConstructor
    public static class Level {

        @Getter private final int crystals;
        @Getter private final int vault;
        @Getter private final int amount;
        @Getter private final Type type;
        @Getter private final Set<String> requirements; // Sets are optimised for calling contains

    }

    private final @NotNull Function<Location, Island> getIslandByLocation;

    private static boolean isRequiredBlock(final @NotNull Level level, final @NotNull Block block) {
        if (level.requirements.isEmpty()) return true;

        // Check the block is one of the required types
        final @NotNull Material material = block.getType();
        final @NotNull XMaterial xMaterial = XMaterial.matchXMaterial(material);
        if (level.requirements.contains(xMaterial.name())) return true;

        // Check the crop is in one of the required states
        if (!XBlock.isCrops(material)) return false;
        final @NotNull CropState cropState = CropState.values()[XBlock.getAge(block)];
        return level.requirements.contains(cropState.toString());
    }

    private static boolean isRequiredEntity(final @NotNull Level level, final @NotNull Entity entity) {
        if (level.requirements.isEmpty()) return true;

        // Check the entity is one of the required types
        final @NotNull EntityType entityType = entity.getType();
        return (level.requirements.contains(entityType.toString()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = getIslandByLocation.apply(block.getLocation());
        if (island == null) return;

        island.getMissionsByType(Type.BLOCK_BREAK).stream()
                .filter(level -> isRequiredBlock(level, block))
                .forEach(level -> island.advanceMission(level, 1));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final @NotNull Block block = event.getBlock();
        final @Nullable Island island = getIslandByLocation.apply(block.getLocation());
        if (island == null) return;

        island.getMissionsByType(Type.BLOCK_PLACE).stream()
                .filter(level -> isRequiredBlock(level, block))
                .forEach(level -> island.advanceMission(level, 1));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(final @NotNull EntityDeathEvent event) {
        final @NotNull LivingEntity deceased = event.getEntity();
        final @Nullable Island island = getIslandByLocation.apply(deceased.getLocation());
        if (island == null) return;

        island.getMissionsByType(Type.ENTITY_KILL).stream()
                .filter(level -> isRequiredEntity(level, deceased))
                .forEach(level -> island.advanceMission(level, 1));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerFish(final @NotNull PlayerFishEvent event) {
        final @Nullable Island island = getIslandByLocation.apply(event.getPlayer().getLocation());
        if (island == null) return;

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        final @Nullable Entity entity = event.getCaught();
        if (entity == null) return;

        island.getMissionsByType(Type.FISH_CATCH).stream()
                .filter(level -> isRequiredEntity(level, entity))
                .forEach(level -> island.advanceMission(level, 1));
    }

}
