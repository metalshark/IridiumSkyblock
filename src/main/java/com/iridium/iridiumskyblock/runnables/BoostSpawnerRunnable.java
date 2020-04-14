package com.iridium.iridiumskyblock.runnables;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.CreatureSpawner;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class BoostSpawnerRunnable implements Runnable {

    @NotNull private final CreatureSpawner spawner;

    @Override
    public void run() {
        int delay = spawner.getDelay();
        delay /= 2; // Half the delay when boosting
        spawner.setDelay(delay);
    }

}
