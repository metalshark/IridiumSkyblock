package com.iridium.iridiumskyblock.plugins;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class WorldEditManager {

    private final @NotNull WorldEdit worldEdit;

    public WorldEditManager(final @NotNull Plugin plugin) {
        worldEdit = ((WorldEditPlugin) plugin).getWorldEdit();
    }

}
