package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Messages;
import com.iridium.iridiumskyblock.configs.Schematics;
import com.iridium.iridiumskyblock.configs.Upgrades;
import com.iridium.iridiumskyblock.configs.Upgrades.IslandUpgrade;
import com.iridium.iridiumskyblock.configs.Upgrades.Upgrade;
import com.iridium.iridiumskyblock.db.DatabaseManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IslandManager {

    @Getter @Nullable private final World world;
    @Getter @Nullable private final World netherWorld;

    public IslandManager() {
        @NotNull final Config config = IridiumSkyblock.getConfiguration();
        @NotNull final String worldName = config.worldName;
        @Nullable final World world = Bukkit.getWorld(worldName);
        this.world = (world == null) ? makeWorld(Environment.NORMAL, worldName) : world;

        if (config.netherIslands) {
            @NotNull final String netherWorldName = config.netherWorldName;
            @Nullable final World netherWorld = Bukkit.getWorld(netherWorldName);
            this.netherWorld = (netherWorld == null) ? makeWorld(Environment.NETHER, netherWorldName) : netherWorld;
        } else
            netherWorld = null;
    }

    private @Nullable World makeWorld(@NotNull Environment env, @NotNull String name) {
        @NotNull final WorldCreator wc = new WorldCreator(name);
        wc.type(WorldType.FLAT);
        wc.generateStructures(false);
        wc.generator(new SkyblockGenerator());
        wc.environment(env);
        return wc.createWorld();
    }

    public void createIsland(@NotNull Player player) {
        @NotNull final Config config = IridiumSkyblock.getConfiguration();

        @NotNull final User user = User.getUser(player);
        if (user.lastCreate != null
            && new Date().before(user.lastCreate)
            && config.createCooldown
            && !user.bypassing) {
            //The user cannot create an island
            final long time = (user.lastCreate.getTime() - System.currentTimeMillis()) / 1000;
            final int day = (int) TimeUnit.SECONDS.toDays(time);
            final int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400));
            final int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
            final int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);

            @NotNull final Messages messages = IridiumSkyblock.getMessages();
            player.sendMessage(Utils.color(messages.createCooldown
                .replace("%days%", day + "")
                .replace("%hours%", hours + "")
                .replace("%minutes%", minute + "")
                .replace("%seconds%", second + "")
                .replace("%prefix%", config.prefix)));
            return;
        }

        @NotNull final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, config.regenCooldown);
        user.lastCreate = calendar.getTime();

        @NotNull final DatabaseManager databaseManager = IridiumSkyblock.getDatabaseManager();
        @NotNull final Location nextLocation = databaseManager.getNextIslandLocation();
        @NotNull final Upgrades upgrades = IridiumSkyblock.getUpgrades();
        @NotNull final Upgrade sizeUpgrade = upgrades.sizeUpgrade;
        @NotNull final Map<Integer, IslandUpgrade> sizeUpgrades = sizeUpgrade.upgrades;
        @NotNull final IslandUpgrade sizeUpgrade1 = sizeUpgrades.get(1);
        final int size = sizeUpgrade1.size;
        final double halfSize = size / 2.00;

        @NotNull final Location pos1 = nextLocation.clone().subtract(halfSize, 0, halfSize);
        @NotNull final Location pos2 = nextLocation.clone().add(halfSize, 0, halfSize);
        @NotNull final Location center = nextLocation.clone().add(0, 100, 0);
        @NotNull final Location home = nextLocation.clone();

        @NotNull final Location netherhome = home.clone();

        if (config.netherIslands)
            netherhome.setWorld(netherWorld);

        @NotNull final Island island = new Island(player, pos1, pos2, center, home, netherhome, databaseManager.getNextIslandId());
        databaseManager.addIsland(island);
        user.role = Role.Owner;

        @NotNull final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
        if (plugin.schems.size() == 1) {
            for (@NotNull Schematics.FakeSchematic schematic : plugin.schems.keySet()) {
                island.setSchematic(schematic.name);
                island.setHome(island.getHome().add(schematic.x, schematic.y, schematic.z));
                island.setNetherhome(island.getNetherhome().add(schematic.x, schematic.y, schematic.z));
            }
            island.pasteSchematic(player, false);
        } else {
            player.openInventory(island.getSchematicSelectGUI().getInventory());
        }
    }

    public @Nullable Island getIslandViaLocation(@NotNull Location location) {
        if (!isIslandWorld(location)) return null;

        @NotNull final DatabaseManager databaseManager = IridiumSkyblock.getDatabaseManager();
        final int x = (int) location.getX();
        final int z = (int) location.getZ();
        return databaseManager.getIslandByCoords(x, z);
    }

    public @Nullable Island getIslandViaId(int id) {
        @NotNull final DatabaseManager databaseManager = IridiumSkyblock.getDatabaseManager();
        return databaseManager.getIslandById(id);
    }

    public boolean isIslandWorld(@NotNull Location location) {
        @NotNull final World world = location.getWorld();
        if (world == null) return false;
        return isIslandWorld(world);
    }

    public boolean isIslandWorld(@NotNull World world) {
        @NotNull final String name = world.getName();
        return isIslandWorld(name);
    }

    public boolean isIslandWorld(@NotNull String name) {
        @NotNull final Config config = IridiumSkyblock.getConfiguration();
        return (name.equals(config.worldName) || (config.netherIslands && name.equals(config.netherWorldName)));
    }

    public void removeIsland(@NotNull Island island) {
        @NotNull final DatabaseManager databaseManager = IridiumSkyblock.getDatabaseManager();
        databaseManager.removeIsland(island);
    }
}
