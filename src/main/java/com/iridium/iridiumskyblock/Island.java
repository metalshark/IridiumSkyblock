package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.enumerators.Permission;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Island {

    @Getter private final @NotNull IslandConfiguration configuration;
    @Getter private final int x;
    @Getter private final int z;
    @Getter @Setter private int size;
    @Getter private final @NotNull Map<Environment, World> worlds;

    public Island(final @NotNull IslandConfiguration configuration,
                  final int x,
                  final int z,
                  final int size,
                  final @NotNull Map<Environment, World> worlds) {
        this.configuration = configuration;
        this.x = x;
        this.z = z;
        this.size = size;
        this.worlds = worlds;
    }

    public boolean getCancelBlockGrow(final @NotNull Block block) {
        return false;
    }

    public @NotNull Location getCenter(final @NotNull World world) {
        return new Location(world, x, 1, z);
    }

    public @NotNull Location getHome(final @NotNull World world) {
        return getCenter(world);
    }

    public int getMinX() {
        return x - size;
    }

    public int getMinZ() {
        return z - size;
    }

    public int getMaxX() {
        return x + size;
    }

    public int getMaxZ() {
        return z + size;
    }

    public boolean isOnIsland(final @NotNull Location location) {
        final @Nullable World world = location.getWorld();
        if (world == null) return false;
        if (!worlds.containsValue(world)) return false;

        final int blockX = location.getBlockX();
        final int blockZ = location.getBlockZ();
        return (blockX >= getMinX() && blockX <= getMaxX() && blockZ >= getMinZ() && blockZ <= getMaxZ());
    }

    public boolean isUserForbidden(final @NotNull User user, final @NotNull Permission permission) {
        return true;
    }

}
