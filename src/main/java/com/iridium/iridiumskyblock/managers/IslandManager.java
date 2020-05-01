package com.iridium.iridiumskyblock.managers;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.iterators.BoundingBoxBlockIterator;
import com.iridium.iridiumskyblock.iterators.IslandBoundingBoxIterator;
import com.iridium.iridiumskyblock.plugins.WorldEditManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("UnnecessaryReturnStatement")
public class IslandManager implements Listener {

    private final @NotNull IridiumSkyblock plugin;
    private final @NotNull IslandBoundingBoxIterator islandBoundingBoxIterator;
    private final @Nullable WorldEditManager worldEditManager;
    private final @Nullable Function<CreatureSpawner, Integer> getSpawnerAmount;

    public IslandManager(final @NotNull IridiumSkyblock plugin) {
        this.plugin = plugin;
        final int distance = 150;
        final @NotNull BoundingBox boundingBox = new BoundingBox(50, 0, 50, 100, 255, 100);
        final @NotNull BlockFace direction = BlockFace.NORTH;
        final int countInDirection = 0;
        final int maxInDirection = 1;
        islandBoundingBoxIterator = new IslandBoundingBoxIterator(distance, boundingBox,
            direction, countInDirection, maxInDirection);

        final @Nullable Plugin worldEditPlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        worldEditManager = (worldEditPlugin != null && worldEditPlugin.isEnabled())
            ? new WorldEditManager(worldEditPlugin) : null;

        getSpawnerAmount = null;
    }

    public @NotNull Island createIsland(final @NotNull Island.Configuration configuration) {
        return new Island(configuration, islandBoundingBoxIterator.next());
    }

    public @Nullable Island getIslandByLocation(final @NotNull Location location) {
        throw new UnsupportedOperationException();
    }

    public @NotNull Stream<Block> getIslandBlocks(final @NotNull Island island,
                                                  final @Nullable Predicate<Block> predicate) {
        @NotNull Stream<Block> stream = plugin.getWorldManager().getWorlds().values().stream()
            .flatMap(world -> {
                final @NotNull Iterator<@NotNull Block> iterator = new
                    BoundingBoxBlockIterator(world, island.getBoundingBox());
                final @NotNull Spliterator<@NotNull Block> spliterator =
                    Spliterators.spliteratorUnknownSize(iterator,
                        Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.SORTED);
                return StreamSupport.stream(spliterator, false);
            });
        if (predicate != null)
            stream = stream.filter(predicate);
        return stream;
    }

    public @NotNull Map<XMaterial, Integer> getIslandXMaterialCounts(final @NotNull Island island) {
        return getIslandBlocks(island, null)
            .collect(Collectors.groupingBy(
                block -> XMaterial.matchXMaterial(block.getType()),
                Collectors.summingInt(block -> {
                    final @NotNull BlockState blockState = block.getState();
                    if (!(blockState instanceof CreatureSpawner) || getSpawnerAmount == null) return 1;
                    return getSpawnerAmount.apply((CreatureSpawner) blockState);
                })));
    }

    public @NotNull Stream<Entity> getIslandEntities(final @NotNull Island island,
                                                     final @Nullable Predicate<Entity> predicate) {
        return plugin.getWorldManager().getWorlds().values().stream()
            .flatMap(world -> world.getNearbyEntities(island.getBoundingBox(), predicate).stream());
    }

    public @NotNull Map<EntityType, Integer> getIslandEntityTypeCounts(final @NotNull Island island) {
        return getIslandEntities(island, null)
            .collect(Collectors.groupingBy(
                Entity::getType,
                Collectors.summingInt(entity -> 1)
            ));
    }

    private static final @NotNull Map<BlockFace, Vector> pistonVectors = ImmutableMap.<BlockFace, Vector>builder()
        .put(BlockFace.EAST,  new Vector( 1, 0, 0))
        .put(BlockFace.WEST,  new Vector(-1, 0, 0))
        .put(BlockFace.UP,    new Vector( 0, 1, 0))
        .put(BlockFace.DOWN,  new Vector( 0,-1, 0))
        .put(BlockFace.SOUTH, new Vector( 0, 0, 1))
        .put(BlockFace.NORTH, new Vector( 0, 0,-1))
        .build();

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonExtend(final @NotNull BlockPistonExtendEvent event) {
        final @Nullable Island island = getIslandByLocation(event.getBlock().getLocation());
        if (island == null) return;

        // Ensure blocks outside of the island are unaffected
        final @NotNull Vector vector = pistonVectors.get(event.getDirection());
        if (event.getBlocks().stream()
            .map(Block::getLocation)
            .map(location -> location.add(vector))
            .anyMatch(island::isNotOnIsland)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonRetract(final @NotNull BlockPistonRetractEvent event) {
        final @Nullable Island island = getIslandByLocation(event.getBlock().getLocation());
        if (island == null) return;

        // Ensure blocks outside of the island are unaffected
        if (event.getBlocks().stream()
            .map(Block::getLocation)
            .anyMatch(island::isNotOnIsland)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityExplode(final @NotNull EntityExplodeEvent event) {
        final @Nullable Island island = getIslandByLocation(event.getLocation());
        if (island == null) return;

        if (island.getConfiguration().isExplosionsEnabled()) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeavesDecay(final @NotNull LeavesDecayEvent event) {
        final @Nullable Island island = getIslandByLocation(event.getBlock().getLocation());
        if (island == null) return;

        if (island.getConfiguration().isLeavesDecayEnabled()) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPortal(final @NotNull PlayerPortalEvent event) {
        final @Nullable Island island = getIslandByLocation(event.getFrom());
        if (island == null) return;

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCanCreatePortal(true);

            final @Nullable Location toLocation = event.getTo();
            if (toLocation == null) return;

            final @Nullable World toWorld = toLocation.getWorld();
            if (toWorld == null) return;

            final @NotNull BoundingBox boundingBox = island.getBoundingBox();
            event.setTo(new Location(toWorld,
                boundingBox.getCenterX(), boundingBox.getCenterY(), boundingBox.getCenterZ()));
        }
    }

    @EventHandler
    public void onPlayerTeleport(final @NotNull PlayerTeleportEvent event) {
        final @Nullable Location toLocation = event.getTo();
        if (toLocation == null) return;

        final @Nullable Island island = getIslandByLocation(toLocation);
        if (island == null) return;

        final @Nullable World toWorld = toLocation.getWorld();
        if (toWorld == null) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getReflectionManager().sendWorldBorder(
            event.getPlayer(), island.getConfiguration().getBorderColor(),
            toLocation.getWorld(), island.getBoundingBox()), 0L);
    }

}
