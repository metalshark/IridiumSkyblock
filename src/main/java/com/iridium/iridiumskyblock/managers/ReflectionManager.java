package com.iridium.iridiumskyblock.managers;

import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class ReflectionManager {

    public void setBlockFast(final @NotNull Block block,
                             final int blockId,
                             final byte data) {
        throw new UnsupportedOperationException();
    }

    public void sendChunk(final @NotNull Player player,
                          final @NotNull Chunk chunk) {
        throw new UnsupportedOperationException();
    }

    public void sendWorldBorder(final @NotNull Player player,
                                final @NotNull Color color,
                                final @NotNull World world,
                                final @NotNull BoundingBox boundingBox) {
        throw new UnsupportedOperationException();
    }

    public void sendSubTitle(final @NotNull Player player,
                             final @NotNull String message,
                             final int fadeIn,
                             final int displayTime,
                             final int fadeOut) {
        throw new UnsupportedOperationException();
    }

    public void sendTitle(final @NotNull Player player,
                          final @NotNull String message,
                          final int fadeIn,
                          final int displayTime,
                          final int fadeOut) {
        throw new UnsupportedOperationException();
    }

}
