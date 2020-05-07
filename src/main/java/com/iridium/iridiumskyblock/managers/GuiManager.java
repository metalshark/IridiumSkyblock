package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class GuiManager {

    private final @NotNull IridiumSkyblock plugin;

    public void openIslandMenu(final @NotNull Player player, final @NotNull Island island) {
        throw new UnsupportedOperationException();
    }

}
