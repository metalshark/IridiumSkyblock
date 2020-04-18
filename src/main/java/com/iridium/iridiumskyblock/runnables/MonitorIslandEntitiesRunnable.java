package com.iridium.iridiumskyblock.runnables;

import com.iridium.iridiumskyblock.Island;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class MonitorIslandEntitiesRunnable implements Runnable {

    private final @NotNull Island island;

    @Override
    public void run() {
        island.getEntities()
            .stream()
            .filter(island::isEntityInIsland)
            .forEach(entity -> {
                entity.remove();
                island.removeEntity(entity);
            });
    }

}
