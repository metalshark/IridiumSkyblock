package com.iridium.iridiumskyblock.iterators;

import com.cryptomorin.xseries.XMaterial;
import lombok.SneakyThrows;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoundingBoxXMaterialIterator implements Iterator<XMaterial> {

    private final @NotNull World world;
    private final @NotNull Map<Integer, Map<Integer, ChunkSnapshot>> chunks;

    private final int minX;
    private final int minY;

    private final int maxX;
    private final int maxY;
    private final int maxZ;

    private int nextX;
    private int nextY;
    private int nextZ;

    private final @Nullable Function<Location, CompletableFuture<Integer>> getSpawnerAmountAsync;
    private final @NotNull Collection<CompletableFuture<Integer>> spawnerAmountFutures;
    private int additionalSpawners = 0;

    public BoundingBoxXMaterialIterator(final @NotNull World world, final @NotNull BoundingBox boundingBox,
                                        final @Nullable Function<Location, CompletableFuture<Integer>> getSpawnerAmountAsync) {
        this.world = world;
        this.getSpawnerAmountAsync = getSpawnerAmountAsync;

        minX = (int) boundingBox.getMinX();
        minY = (int) boundingBox.getMinY();
        final int minZ = (int) boundingBox.getMinZ();

        maxX = (int) boundingBox.getMaxX();
        maxY = (int) boundingBox.getMaxY();
        maxZ = (int) boundingBox.getMaxZ();

        nextX = minX;
        nextY = minY;
        nextZ = (int) boundingBox.getMinZ();

        chunks = IntStream.range(minX / 16, maxX / 16).boxed()
            .collect(Collectors.toMap(Function.identity(), x -> IntStream.range(minZ / 16, maxZ / 16).boxed()
                .collect(Collectors.toMap(Function.identity(), z -> world
                    .getChunkAt(x, z)
                    .getChunkSnapshot(false, false, false)
                ))
            ));
        spawnerAmountFutures = new ArrayList<>();
    }

    @Override
    public boolean hasNext() {
        return !(nextX == maxX && nextY == maxY && nextZ == maxZ);
    }

    @SneakyThrows
    @Override
    public XMaterial next() {
        final @NotNull ChunkSnapshot chunkSnapshot = chunks.get(nextX / 16).get(nextZ / 16);
        final @NotNull Material material = chunkSnapshot.getBlockType(nextX % 16, nextY, nextZ % 16);
        final @NotNull XMaterial xmaterial = XMaterial.matchXMaterial(material);
        if (getSpawnerAmountAsync != null && xmaterial == XMaterial.SPAWNER)
            spawnerAmountFutures.add(getSpawnerAmountAsync.apply(new Location(world, nextX, nextY, nextZ)));

        nextX++;
        if (nextX <= maxX) return xmaterial;

        nextX = minX;
        nextY++;
        if (nextY <= maxY) return xmaterial;

        nextY = minY;
        nextZ++;
        if (nextZ <= maxZ) return xmaterial;

        if (!spawnerAmountFutures.isEmpty()) {
            additionalSpawners = spawnerAmountFutures.stream()
                .mapToInt(future -> Integer.min(future.join() - 1, 0))
                .sum();
            spawnerAmountFutures.clear();
        }

        if (additionalSpawners != 0) {
            additionalSpawners--;
            return XMaterial.SPAWNER;
        }

        throw new java.util.NoSuchElementException();
    }

}
