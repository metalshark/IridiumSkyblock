package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.managers.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class IridiumSkyblock extends JavaPlugin {

    @Getter private BoostManager boostManager;
    @Getter private DatabaseManager databaseManager;
    @Getter private IslandManager islandManager;
    @Getter private MissionManager missionManager;
    @Getter private ReflectionManager reflectionManager;
    @Getter private UpgradeManager upgradeManager;
    @Getter private UserManager userManager;
    @Getter private WorldManager worldManager;

    @Override
    public void onEnable() {
        super.onEnable();

        reflectionManager = new ReflectionManager();
        databaseManager = new DatabaseManager(this);

        worldManager = new WorldManager(this);
        islandManager = new IslandManager(this);
        userManager = new UserManager(this);

        missionManager = new MissionManager(this);
        upgradeManager = new UpgradeManager(this);
        boostManager = new BoostManager(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        HandlerList.unregisterAll(this);
    }

    public static IridiumSkyblock getPlugin() {
        return getPlugin(IridiumSkyblock.class);
    }

    public @NotNull BukkitTask runTaskLater(final @NotNull Runnable task, final long delay) {
        return Bukkit.getScheduler().runTaskLater(this, task, delay);
    }

}
