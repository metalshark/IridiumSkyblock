package com.iridium.iridiumskyblock;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
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
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class IslandManager {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull DatabaseManager databaseManager = IridiumSkyblock.getDatabaseManager();
    private static final @NotNull Messages messages = IridiumSkyblock.getMessages();
    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull PluginManager pluginManager = Bukkit.getPluginManager();

    @Getter private final @NotNull World world = getOrCreateWorld(config.worldName, Environment.NORMAL);
    @Getter private final @Nullable World netherWorld = (config.netherIslands) ? getOrCreateWorld(config.netherWorldName, Environment.NETHER) : null;

    private @Nullable Function<Player, Location> spawnLocationFunction;

    private @Nullable World createWorld(@NotNull Environment environment, @NotNull String name) {
        final @NotNull WorldCreator wc = new WorldCreator(name);
        wc.type(WorldType.FLAT);
        wc.generateStructures(false);
        final @NotNull SkyblockGenerator generator = new SkyblockGenerator();
        wc.generator(generator);
        wc.environment(environment);
        return wc.createWorld();
    }

    private @NotNull World getOrCreateWorld(@NotNull String name, @NotNull Environment environment) {
        final @NotNull String worldName = config.worldName;
        @Nullable World world = Bukkit.getWorld(worldName);
        if (world == null) world = createWorld(environment, worldName);
        if (world == null)
            throw new RuntimeException("Unable to get or create island world " + worldName);
        world.getWorldBorder().setSize(Double.MAX_VALUE);
        return world;
    }

    public void createIsland(@NotNull Player player) {
        final @NotNull User user = User.getUser(player);
        final @Nullable Date userLastCreatedDate = user.getLastCreate();
        if (userLastCreatedDate != null
            && new Date().before(userLastCreatedDate)
            && config.createCooldown
            && !user.isBypassing()) {
            //The user cannot create an island
            final long time = (userLastCreatedDate.getTime() - System.currentTimeMillis()) / 1000;
            final int day = (int) TimeUnit.SECONDS.toDays(time);
            final int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400));
            final int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
            final int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);

            player.sendMessage(Utils.color(messages.createCooldown
                .replace("%days%", day + "")
                .replace("%hours%", hours + "")
                .replace("%minutes%", minute + "")
                .replace("%seconds%", second + "")
                .replace("%prefix%", config.prefix)));
            return;
        }

        final @NotNull Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, config.regenCooldown);
        user.setLastCreate(calendar.getTime());

        final @NotNull Location nextLocation = databaseManager.getNextIslandLocation();
        final @NotNull Upgrades upgrades = IridiumSkyblock.getUpgrades();
        final @NotNull Upgrade sizeUpgrade = upgrades.sizeUpgrade;
        final @NotNull Map<Integer, IslandUpgrade> sizeUpgrades = sizeUpgrade.upgrades;
        final @NotNull IslandUpgrade sizeUpgrade1 = sizeUpgrades.get(1);
        final int size = sizeUpgrade1.size;
        final double halfSize = size / 2.00;

        final @NotNull Location pos1 = nextLocation.clone().subtract(halfSize, 0, halfSize);
        final @NotNull Location pos2 = nextLocation.clone().add(halfSize, 0, halfSize);
        final @NotNull Location center = nextLocation.clone().add(0, 100, 0);
        final @NotNull Location home = nextLocation.clone();

        final @NotNull Location netherhome = home.clone();

        if (config.netherIslands)
            netherhome.setWorld(netherWorld);

        final @NotNull Island island = new Island(player, pos1, pos2, center, home, netherhome, databaseManager.getNextIslandId());
        databaseManager.addIsland(island);
        user.setRole(Role.Owner);

        if (plugin.getSchems().size() == 1) {
            for (final @NotNull Schematics.FakeSchematic schematic : plugin.getSchems().keySet()) {
                island.setSchematic(schematic.name);
                island.setHome(island.getHome().add(schematic.x, schematic.y, schematic.z));
                island.setNetherhome(island.getNetherhome().add(schematic.x, schematic.y, schematic.z));
            }
            island.pasteSchematic(player, false);
        } else {
            player.openInventory(island.getSchematicSelectGUI().getInventory());
        }
    }

    public @Nullable Island getIslandByBlock(@NotNull Block block) {
        final @NotNull Location location = block.getLocation();
        return getIslandByLocation(location);
    }

    public @Nullable Island getIslandByItem(@NotNull Item item) {
        final @NotNull Location location = item.getLocation();
        return getIslandByLocation(location);
    }

    public @Nullable Island getIslandByLocation(@NotNull Location location) {
        if (!isIslandWorldLocation(location)) return null;

        final int x = (int) location.getX();
        final int z = (int) location.getZ();
        return databaseManager.getIslandByCoords(x, z);
    }

    public @Nullable Island getIslandById(int id) {
        return databaseManager.getIslandById(id);
    }

    public @NotNull Collection<Island> getIslands() {
        return databaseManager.getIslands();
    }

    public boolean isIslandWorldBlock(@NotNull Block block) {
        final @Nullable Location location = block.getLocation();
        return isIslandWorldLocation(location);
    }

    public boolean isIslandWorldEntity(@NotNull Entity entity) {
        final @Nullable Location location = entity.getLocation();
        return isIslandWorldLocation(location);
    }

    public boolean isIslandWorldLocation(@NotNull Location location) {
        final @Nullable World world = location.getWorld();
        if (world == null) return false;
        return isIslandWorld(world);
    }

    public boolean isIslandWorld(@NotNull World world) {
        final @NotNull String name = world.getName();
        return isIslandWorldName(name);
    }

    public boolean isIslandWorldName(@NotNull String name) {
        return (name.equals(config.worldName) || (config.netherIslands && name.equals(config.netherWorldName)));
    }

    public void removeIsland(@NotNull Island island) {
        databaseManager.removeIsland(island);
    }

    private static @NotNull Function<Player, Location> getSpawnLocationFunction() {
        if (pluginManager.isPluginEnabled("EssentialsSpawn")) {
            final @Nullable EssentialsSpawn essentialsSpawn = (EssentialsSpawn) pluginManager.getPlugin("EssentialsSpawn");
            final @Nullable Essentials essentials = (Essentials) pluginManager.getPlugin("Essentials");
            if (essentials != null && essentialsSpawn != null)
                return player -> essentialsSpawn.getSpawn(essentials.getUser(player).getGroup());
        }

        final @NotNull World world = Bukkit.getWorlds().get(0);
        return player -> world.getSpawnLocation();
    }

    public @NotNull Location getSpawnLocation(@NotNull Player player) {
        if (spawnLocationFunction == null) spawnLocationFunction = getSpawnLocationFunction();
        return spawnLocationFunction.apply(player);
    }

    public Island getIslandByEntity(@NotNull Entity entity) {
        final int islandId = getIslandIdByEntity(entity);
        return getIslandById(islandId);
    }

    public Island getIslandByEntityUuid(@NotNull UUID uuid) {
        final int islandId = getIslandIdByEntityUuid(uuid);
        return getIslandById(islandId);
    }

    public int getIslandIdByEntity(@NotNull Entity entity) {
        return getIslandIdByEntityUuid(entity.getUniqueId());
    }

    public int getIslandIdByEntityUuid(@NotNull UUID uuid) {
        return databaseManager.getIslandIdByEntityUuid(uuid);
    }

}
