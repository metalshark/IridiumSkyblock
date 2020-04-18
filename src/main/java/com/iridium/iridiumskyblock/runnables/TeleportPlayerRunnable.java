package com.iridium.iridiumskyblock.runnables;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class TeleportPlayerRunnable implements Runnable {

    private final @NotNull Location location;
    private final @NotNull Player player;

    @Override
    public void run() {
        player.teleport(location);
    }

}
