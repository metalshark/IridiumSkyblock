package com.iridium.iridiumskyblock.plugins;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiverseCoreManager {

    private final @NotNull MVWorldManager mvWorldManager;

    public MultiverseCoreManager(final @NotNull Plugin plugin) {
        mvWorldManager = ((MultiverseCore) plugin).getMVWorldManager();
    }

    public void registerWorld(final @NotNull World world, final @NotNull String generator) {
        final @Nullable MultiverseWorld mvWorld = mvWorldManager.getMVWorld(world);
        if (mvWorld == null)
            mvWorldManager.addWorld(world.getName(), world.getEnvironment(),
                null, null, null, generator, false);
        else
            mvWorld.setGenerator(generator);
    }

}
