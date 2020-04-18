package com.iridium.iridiumskyblock.runnables;

import com.iridium.iridiumskyblock.Island;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class SendIslandBorderRunnable implements Runnable {

    private final @NotNull Island island;
    private final @NotNull Player player;

    @Override
    public void run() {
        island.sendBorder(player);
    }

}
