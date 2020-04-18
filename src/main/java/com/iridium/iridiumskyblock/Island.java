package com.iridium.iridiumskyblock;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.iridium.iridiumskyblock.api.IslandCreateEvent;
import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
import com.iridium.iridiumskyblock.configs.*;
import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.db.DatabaseManager;
import com.iridium.iridiumskyblock.gui.*;
import com.iridium.iridiumskyblock.iterators.IslandChunkIterator;
import com.iridium.iridiumskyblock.nms.NMS;
import com.iridium.iridiumskyblock.runnables.InitIslandBlocksRunnable;
import com.iridium.iridiumskyblock.runnables.InitIslandBlocksWithSenderRunnable;
import com.iridium.iridiumskyblock.runnables.MonitorIslandEntitiesRunnable;
import com.iridium.iridiumskyblock.support.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Island {

    @RequiredArgsConstructor
    @NotNull public static class Warp {
        @Getter private final @NotNull Location location;
        @Getter private final @NotNull String name;
        @Getter private final @NotNull String password;
    }

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull DatabaseManager databaseManager = IridiumSkyblock.getDatabaseManager();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private static final @NotNull Messages messages = IridiumSkyblock.getMessages();
    private static final @NotNull NMS nms = IridiumSkyblock.getNms();
    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull PluginManager pluginManager = Bukkit.getPluginManager();
    private static final @NotNull BukkitScheduler scheduler = Bukkit.getScheduler();
    private static final @NotNull Upgrades upgrades = IridiumSkyblock.getUpgrades();

    @Getter private @NotNull UUID owner;
    @Getter private @NotNull Location pos1;
    @Getter private @NotNull Location pos2;
    @Getter private @NotNull Location center;
    @Getter @Setter private @NotNull Location home;
    @Setter private @Nullable Location netherhome;

    @Getter private @Nullable UpgradeGUI upgradeGUI;
    @Getter private @Nullable BoosterGUI boosterGUI;
    @Getter private @Nullable MissionsGUI missionsGUI;
    @Getter private @Nullable MembersGUI membersGUI;
    @Getter private @Nullable WarpGUI warpGUI;
    @Getter private @Nullable BorderColorGUI borderColorGUI;
    @Getter private @Nullable SchematicSelectGUI schematicSelectGUI;
    @Getter private @Nullable PermissionsGUI permissionsGUI;
    @Getter private @Nullable IslandMenuGUI islandMenuGUI;
    @Getter private @Nullable CoopGUI coopGUI;
    @Getter private @Nullable BankGUI bankGUI;
    @Getter private @Nullable BiomeGUI biomeGUI;

    @Getter private final int id;

    @Getter @Setter private int spawnerBooster;
    @Getter @Setter private int farmingBooster;
    @Getter @Setter private int expBooster;
    @Getter @Setter private int flightBooster;

    private int boosterid;

    @Getter @Setter private int crystals;

    @Getter private int sizeLevel;
    @Getter @Setter private int memberLevel;
    @Getter @Setter private int warpLevel;
    @Getter @Setter private int oreLevel;

    private int generateID;

    @Getter private double value;

    private Map<String, Integer> valuableBlocks;
    private Set<Location> tempValues;
    private Map<String, Integer> spawners;

    @Getter private final List<Warp> warps;

    private double startvalue;

    @Getter @Setter private boolean visit;

    @Getter @Setter private Color borderColor;

    private Map<Role, Permissions> permissions;

    @Getter @Setter private String schematic;

    private Set<User> bans;

    private Set<User> votes;

    private Set<Integer> coop;

    private Set<Integer> coopInvites;

    @Setter private String name;

    @Getter @Setter private double money;
    @Getter @Setter private int experience;

    @Getter private XBiome biome;

    private Set<Location> failedGenerators;

    private Date lastRegen;

    private int initBlocks;

    @Getter private boolean updating = false;

    private int percent = 0;

    @Getter private final int monitorEntitiesTask;

    public Island(@NotNull Player owner, @NotNull Location pos1, @NotNull Location pos2, @NotNull Location center,
                  @NotNull Location home, @Nullable Location netherhome, int id) {
        final @NotNull User user = User.getUser(owner);
        user.setRole(Role.Owner);
        this.biome = config.defaultBiome;
        valuableBlocks = new HashMap<>();
        spawners = new HashMap<>();
        tempValues = new HashSet<>();
        this.owner = user.getUuid();
        this.name = user.getName();
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.center = center;
        this.home = home;
        this.netherhome = netherhome;
        this.id = id;
        spawnerBooster = 0;
        farmingBooster = 0;
        expBooster = 0;
        flightBooster = 0;
        crystals = 0;
        sizeLevel = 1;
        memberLevel = 1;
        warpLevel = 1;
        oreLevel = 1;
        value = 0;
        warps = new ArrayList<>();
        startvalue = -1;
        borderColor = IridiumSkyblock.getBorder().startingColor;
        visit = config.defaultIslandPublic;
        permissions = new HashMap<>(config.defaultPermissions);
        this.coop = new HashSet<>();
        this.bans = new HashSet<>();
        this.votes = new HashSet<>();
        init();
        databaseManager.addIsland(this);
        databaseManager.addIslandMember(this, user);
        pluginManager.callEvent(new IslandCreateEvent(owner, this));

        final @NotNull Runnable task = new MonitorIslandEntitiesRunnable(this);
        monitorEntitiesTask = scheduler.scheduleSyncRepeatingTask(plugin, task, 20, 20);
    }

    public long getBlockCount() {
        final double minX = pos1.getX();
        final double maxX = pos2.getX();
        final double width = maxX - minX;

        final double minZ = pos1.getZ();
        final double maxZ = pos2.getZ();
        final double depth = maxZ - minZ;

        final @NotNull World islandWorld = islandManager.getWorld();
        final double maxY = islandWorld.getMaxHeight();

        return (long) (width * maxY * depth);
    }

    public void initBlocks() {
        updating = true;

        final Runnable task = new InitIslandBlocksRunnable(this, config.blocksPerTick, () -> {
            if (IridiumSkyblock.getBlocksPerTick() != -1) {
                config.blocksPerTick = IridiumSkyblock.getBlocksPerTick();
                IridiumSkyblock.setBlocksPerTick(-1);
            }
            scheduler.cancelTask(initBlocks);
            initBlocks = -1;
            plugin.setUpdatingBlocks(false);
            updating = false;
            valuableBlocks.clear();
            spawners.clear();
            for (final @NotNull Location location : tempValues) {
                final @NotNull Block block = location.getBlock();
                if (!(Utils.isBlockValuable(block) || !(block.getState() instanceof CreatureSpawner))) continue;
                final @NotNull Material material = block.getType();
                final @NotNull XMaterial xmaterial = XMaterial.matchXMaterial(material);
                valuableBlocks.compute(xmaterial.name(), (xmaterialName, original) -> {
                    if (original == null) return 1;
                    return original + 1;
                });
            }
            tempValues.clear();
            calculateValue();
        });
        initBlocks = scheduler.scheduleSyncRepeatingTask(plugin, task, 0, 1);
    }

    public void forceInitBlocks(@Nullable CommandSender sender, int blocksPerTick, @NotNull String name) {
        if (sender != null)
            sender.sendMessage(Utils.color(messages.updateStarted
                    .replace("%player%", name)
                    .replace("%prefix%", config.prefix)));
        updating = true;
        final Runnable task = new InitIslandBlocksWithSenderRunnable(this, blocksPerTick, sender, name, () -> {
            if (sender != null)
                sender.sendMessage(Utils.color(messages.updateFinished
                        .replace("%player%", name)
                        .replace("%prefix%", config.prefix)));
            scheduler.cancelTask(initBlocks);
            initBlocks = -1;
            updating = false;
            valuableBlocks.clear();
            spawners.clear();
            for (final @NotNull Location location : tempValues) {
                final @NotNull Block block = location.getBlock();
                if (!(Utils.isBlockValuable(block) || !(block.getState() instanceof CreatureSpawner))) continue;
                final @NotNull Material material = block.getType();
                final @NotNull XMaterial xmaterial = XMaterial.matchXMaterial(material);
                valuableBlocks.compute(xmaterial.name(), (xmaterialName, original) -> {
                    if (original == null) return 1;
                    return original + 1;
                });
            }
            tempValues.clear();
            calculateValue();
        });
        initBlocks = scheduler.scheduleSyncRepeatingTask(plugin, task, 0, 1);
    }

    public void resetMissions() {
        databaseManager.resetIslandMissionsLevels(this);
    }

    public @NotNull MissionData getMissionLevel(@NotNull String missionName) {
        @NotNull final Mission mission = IridiumSkyblock.getMission(missionName);
        final int level = getMissionLevelId(missionName);
        return mission.levels.get(level);
    }

    public int getMissionLevelId(@NotNull String missionName) {
        return databaseManager.getIslandMissionLevel(this, missionName);
    }

    public int getMissionAmount(@NotNull String missionName) {
        return databaseManager.getIslandMissionAmount(this, missionName);
    }

    public void addMissionAmount(@NotNull String name, int amountToAdd) {
        final int currentAmount = getMissionAmount(name);
        final int newAmount = currentAmount + amountToAdd;
        setMissionAmountWithKnownCurrentAmount(name, currentAmount, newAmount);
    }

    public void setMissionAmount(@NotNull String name, int newAmount) {
        final int currentAmount = getMissionAmount(name);
        setMissionAmountWithKnownCurrentAmount(name, currentAmount, newAmount);
    }

    public void setMissionAmountWithKnownCurrentAmount(@NotNull String name, int currentAmount, int newAmount) {
        if (currentAmount == Integer.MIN_VALUE) return;
        databaseManager.setIslandMissionAmount(this, name, newAmount);

        final @Nullable MissionData level = getMissionLevel(name);
        if (level == null) return;

        if (level.amount <= newAmount)
            completeMission(name);
    }

    public @NotNull Permissions getPermissionsByUser(@NotNull User user) {
        if (user.isBypassing()) return new Permissions();

        @NotNull Role role;
        if (user.getIslandId() == id) role = user.getRole();
        else if (isCoop(user.getIsland())) role = Role.Member;
        else role = Role.Visitor;
        return getPermissionsByRole(role);
    }

    public @NotNull Permissions getPermissionsByRole(@NotNull Role role) {
        if (permissions == null)
            permissions = new HashMap<>(config.defaultPermissions);
        if (!permissions.containsKey(role))
            permissions.put(role, new Permissions());
        return permissions.get(role);
    }

    public void sendBorder() {
        for (final @NotNull Player player : getPlayersOnIsland())
            sendBorder(player);
    }

    public void hideBorder() {
        for (final @NotNull Player player : getPlayersOnIsland())
            hideBorder(player);
    }

    public void sendBorder(@NotNull Player player) {
        double size = IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size;
        if (size % 2 == 0) size++;
        if (player.getLocation().getWorld().equals(IridiumSkyblock.getIslandManager().getWorld())) {
            nms.sendWorldBorder(player, borderColor, size, getCenter());
        } else if (config.netherIslands) {
            Location loc = getCenter().clone();
            loc.setWorld(IridiumSkyblock.getIslandManager().getNetherWorld());
            nms.sendWorldBorder(player, borderColor, size, loc);
        }
    }

    public void hideBorder(@NotNull Player player) {
        nms.sendWorldBorder(player, borderColor, Integer.MAX_VALUE, getCenter().clone());
    }

    public void calculateValue() {
        if (valuableBlocks == null) valuableBlocks = new HashMap<>();
        if (spawners == null) spawners = new HashMap<>();
        if (tempValues == null) tempValues = new HashSet<>();

        final BlockValues blockValues = IridiumSkyblock.getBlockValues();
        final Map<XMaterial, Double> blockValueMap = blockValues.blockvalue;

        AtomicReference<Double> value = new AtomicReference<>((double) 0);
        for (Map.Entry<String, Integer> entry : valuableBlocks.entrySet()) {
            final String item = entry.getKey();
            final Optional<XMaterial> xmaterial = XMaterial.matchXMaterial(item);
            if (!xmaterial.isPresent()) continue;

            final Double blockValue = blockValueMap.get(xmaterial.get());
            if (blockValue == null) continue;

            value.updateAndGet(v -> (double) (v + (entry.getValue() * blockValue)));
        }

        final double minX = pos1.getX();
        final double minZ = pos1.getZ();
        final double maxX = pos2.getZ();
        final double maxZ = pos2.getZ();

        final Map<String, Double> spawnerValueMap = blockValues.spawnervalue;

        Function<CreatureSpawner, Integer> getSpawnerAmount;
        if (Wildstacker.enabled) {
            getSpawnerAmount = Wildstacker::getSpawnerAmount;
        } else if (MergedSpawners.enabled) {
            getSpawnerAmount = MergedSpawners::getSpawnerAmount;
        } else if (UltimateStacker.enabled) {
            getSpawnerAmount = UltimateStacker::getSpawnerAmount;
        } else if (EpicSpawners.enabled) {
            getSpawnerAmount = EpicSpawners::getSpawnerAmount;
        } else if (AdvancedSpawners.enabled) {
            getSpawnerAmount = AdvancedSpawners::getSpawnerAmount;
        } else {
            getSpawnerAmount = null;
        }

        spawners.clear();

        new IslandChunkIterator(this).forEachRemaining(chunk -> {
            for (BlockState state : chunk.getTileEntities()) {
                if (!(state instanceof CreatureSpawner)) continue;

                final CreatureSpawner spawner = (CreatureSpawner) state;
                final Location location = spawner.getLocation();
                final double x = location.getX();
                final double z = location.getZ();
                if (x < minX || x > maxX || z < minZ || z > maxZ) continue;

                final EntityType type = spawner.getSpawnedType();
                final String typeName = type.name();
                final Double spawnerValue = spawnerValueMap.get(typeName);
                if (spawnerValue == null) continue;

                final int amount = (getSpawnerAmount == null) ? 1 : getSpawnerAmount.apply(spawner);
                spawners.compute(typeName, (name, original) -> {
                    if (original == null) return amount;
                    return original + amount;
                });

                value.updateAndGet(v -> (double) (v + (spawnerValue * amount)));
            }
        });

        this.value = value.get();
        if (startvalue == -1) startvalue = this.value;

        for (final @NotNull Mission mission : IridiumSkyblock.getMissions()) {
            final @NotNull String missionName = mission.name;
            final @Nullable MissionData level = getMissionLevel(missionName);
            if (level.type != MissionType.VALUE_INCREASE) continue;

            final int newAmount = (int) (value.get() - startvalue);
            setMissionAmount(missionName, newAmount);
        }
    }

    public void addWarp(Player player, Location location, String name, String password) {
        if (warps.size() < IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(warpLevel).size) {
            warps.add(new Warp(location, name, password));
            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().warpAdded.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        } else {
            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().maxWarpsReached.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    public boolean isBlockInIsland(@NotNull Block block) {
        return isLocationInIsland(block.getLocation());
    }

    public boolean isEntityInIsland(@NotNull Entity entity) {
        return isLocationInIsland(entity.getLocation());
    }

    public boolean isLocationInIsland(@NotNull Location location) {
        return isInIsland(location.getX(), location.getZ());
    }

    public boolean isInIsland(double x, double z) {
        return x >= pos1.getX()
                && x <= pos2.getX()
                && z >= pos1.getZ()
                && z <= pos2.getZ();
    }

    public void init() {
        updating = false;
        if (biome == null) biome = IridiumSkyblock.getConfiguration().defaultBiome;
        if (valuableBlocks == null) valuableBlocks = new HashMap<>();
        if (spawners == null) spawners = new HashMap<>();
        if (tempValues == null) tempValues = new HashSet<>();
        addMember(User.getUser(owner));

        upgradeGUI = new UpgradeGUI(this);
        boosterGUI = new BoosterGUI(this);
        missionsGUI = new MissionsGUI(this);
        membersGUI = new MembersGUI(this);
        warpGUI = new WarpGUI(this);
        borderColorGUI = new BorderColorGUI(this);
        schematicSelectGUI = new SchematicSelectGUI(this);
        permissionsGUI = new PermissionsGUI(this);
        islandMenuGUI = new IslandMenuGUI(this);
        coopGUI = new CoopGUI(this);
        bankGUI = new BankGUI(this);
        biomeGUI = new BiomeGUI(this);
        failedGenerators = new HashSet<>();
        coopInvites = new HashSet<>();
        boosterid = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), () -> {
            if (spawnerBooster > 0) spawnerBooster--;
            if (farmingBooster > 0) farmingBooster--;
            if (expBooster > 0) expBooster--;
            if (flightBooster == 1) {
                for (final @NotNull User member : getMembers()) {
                    final @Nullable Player memberPlayer = member.getPlayer();
                    if (memberPlayer == null) continue;
                    if ((!memberPlayer.hasPermission("IridiumSkyblock.Fly")
                        && !memberPlayer.hasPermission("iridiumskyblock.fly"))
                        && memberPlayer.getGameMode().equals(GameMode.SURVIVAL)) {
                        memberPlayer.setAllowFlight(false);
                        memberPlayer.setFlying(false);
                        member.setFlying(false);
                    }
                }
            }
            if (flightBooster > 0) flightBooster--;
        }, 0, 20);
        if (permissions == null) {
            permissions = new HashMap<Role, Permissions>() {{
                for (Role role : Role.values()) {
                    put(role, new Permissions());
                }
            }};
        }
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.getInstance(), (Runnable) this::sendBorder, 20);
    }

    public long canGenerate() {
        if (lastRegen == null) return 0;
        if (new Date().after(lastRegen)) return 0;
        return lastRegen.getTime() - System.currentTimeMillis();
    }

    public void pasteSchematic(boolean deleteBlocks) {
        //TODO
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, config.regenCooldown);
        lastRegen = c.getTime();
        if (deleteBlocks) deleteBlocks();
        pasteSchematic();
        killEntities();
        //Reset island home
        for (Schematics.FakeSchematic schematic : plugin.getSchems().keySet()) {
            if (!schematic.name.equals(this.schematic)) continue;
            home = new Location(IridiumSkyblock.getIslandManager().getWorld(), getCenter().getX() + schematic.x, schematic.y, getCenter().getZ() + schematic.z);
        }
    }

    public void teleportPlayersHome() {
        for (Player p : getPlayersOnIsland()) {
            teleportHome(p);
        }
    }

    public void pasteSchematic(Player player, boolean deleteBlocks) {
        pasteSchematic(deleteBlocks);
        User.getUser(player).setTeleportingHome(false);
        teleportHome(player);
        sendBorder(player);
        nms.sendTitle(player, messages.islandCreated, 20, 40, 20);
        if (!messages.islandCreatedSubtitle.isEmpty())
            nms.sendSubTitle(player, messages.islandCreatedSubtitle, 20, 40, 20);
    }

    private void pasteSchematic() {
        for (Schematics.FakeSchematic fakeSchematic : plugin.getSchems().keySet()) {
            if (fakeSchematic.name.equals(schematic)) {
                if (plugin.getSchems().containsKey(fakeSchematic)) {
                    plugin.getSchems().get(fakeSchematic).pasteSchematic(getCenter().clone(), this);
                } else {
                    IridiumSkyblock.getInstance().getLogger().warning("Failed to load schematic: " + fakeSchematic.name);
                    getCenter().getBlock().setType(Material.STONE);
                }
                if (config.debugSchematics) {
                    File schematicFolder = new File(IridiumSkyblock.getInstance().getDataFolder(), "schematics");
                    try {
                        Schematic.debugSchematic(new File(schematicFolder, fakeSchematic.name));
                        if (IridiumSkyblock.getConfiguration().netherIslands)
                            Schematic.debugSchematic(new File(schematicFolder, fakeSchematic.netherisland));
                    } catch (IOException e) {
                    }
                }
                Location center = getCenter().clone();
                if (IridiumSkyblock.getConfiguration().netherIslands) {
                    center.setWorld(IridiumSkyblock.getIslandManager().getNetherWorld());
                    if (plugin.getNetherschems().containsKey(fakeSchematic)) {
                        plugin.getNetherschems().get(fakeSchematic).pasteSchematic(center, this);
                    } else {
                        plugin.getLogger().warning("Failed to load schematic: " + fakeSchematic.netherisland);
                    }
                }
                return;
            }
        }
        IridiumSkyblock.getInstance().getLogger().warning("Could not find schematic: " + schematic);
        getCenter().getBlock().setType(Material.STONE);
    }

    public void clearInventories() {
        if (!config.clearInventories) return;
        for (final @NotNull User member : getMembers()) {
            final @Nullable Player memberPlayer = member.getPlayer();
            if (memberPlayer == null) continue;

            memberPlayer.getInventory().clear();
        }
    }

    public void teleportHome(@NotNull Player p) {
        if (getHome() == null) home = getCenter();
        if (User.getUser(p).isTeleportingHome()) {
            return;
        }
        if (isBanned(User.getUser(p)) && !getMembers().contains(p.getUniqueId())) {
            p.sendMessage(Utils.color(messages.bannedFromIsland
                .replace("%prefix%", config.prefix)));
            return;
        }
        if (getSchematic() == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.getInstance().getSchems().size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.getInstance().getSchems().keySet()) {
                        setSchematic(schematic.name);
                    }
                } else {
                    p.openInventory(getSchematicSelectGUI().getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (getMembers().contains(p.getUniqueId())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().teleportingHome
                .replace("%prefix%", config.prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitingIsland
                .replace("%player%", User.getUser(owner).getName())
                .replace("%prefix%", config.prefix)));
            for (final @NotNull User member : getMembers()) {
                final @Nullable Player memberPlayer = member.getPlayer();
                if (memberPlayer != null) {
                    memberPlayer.sendMessage(Utils.color(messages.visitedYourIsland
                        .replace("%player%", p.getName())
                        .replace("%prefix%", config.prefix)));
                }
            }
        }
        if (Utils.isSafe(getHome(), this)) {
            p.teleport(getHome());
            sendBorder(p);
        } else {
            Location loc = Utils.getNewHome(this, this.home);
            if (loc != null) {
                this.home = loc;
                p.teleport(this.home);
                sendBorder(p);
            } else {
                User.getUser(p).setTeleportingHome(true);
                pasteSchematic(p, false);
            }
        }
    }

    public void teleportNetherHome(Player p) {
        if (getNetherhome() == null) {
            netherhome = center;
            netherhome.setWorld(IridiumSkyblock.getIslandManager().getNetherWorld());
        }
        if (User.getUser(p).isTeleportingHome()) {
            return;
        }
        if (isBanned(User.getUser(p)) && !getMembers().contains(p.getUniqueId())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().bannedFromIsland
                .replace("%prefix%", config.prefix)));
            return;
        }
        if (getSchematic() == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.getInstance().getSchems().size() == 1) {
                    for (Schematics.FakeSchematic schematic : plugin.getSchems().keySet()) {
                        setSchematic(schematic.name);
                    }
                } else {
                    p.openInventory(getSchematicSelectGUI().getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (getMemberUuids().contains(p.getUniqueId())) {
            p.sendMessage(Utils.color(messages.teleportingHome.
                replace("%prefix%", config.prefix)));
        } else {
            p.sendMessage(Utils.color(messages.visitingIsland
                .replace("%player%", User.getUser(owner).getName())
                .replace("%prefix%", config.prefix)));
            for (final @NotNull User member : getMembers()) {
                Player player = member.getPlayer();
                if (player != null) {
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitedYourIsland.replace("%player%", p.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }
        if (Utils.isSafe(getNetherhome(), this)) {
            p.teleport(getNetherhome());
            sendBorder(p);
        } else {

            Location loc = Utils.getNewHome(this, this.netherhome);
            if (loc != null) {
                this.netherhome = loc;
                p.teleport(this.netherhome);
                sendBorder(p);
            } else {
                User.getUser(p).setTeleportingHome(true);
                pasteSchematic(p, false);
            }
        }
    }

    public void delete() {
        Bukkit.getPluginManager().callEvent(new IslandDeleteEvent(this));

        Bukkit.getScheduler().cancelTask(getMembersGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getBoosterGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getMissionsGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getUpgradeGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getWarpGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getPermissionsGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getIslandMenuGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getCoopGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getBankGUI().scheduler);
        if (generateID != -1) Bukkit.getScheduler().cancelTask(generateID);
        if (updating) {
            Bukkit.getScheduler().cancelTask(initBlocks);
            plugin.setUpdatingBlocks(false);
        }
        permissions.clear();
        clearInventories();
        spawnPlayers();
        for (final @NotNull User member : getMembers()) {
            member.setIslandId(0);
            Player p = member.getPlayer();
            if (p != null) {
                p.closeInventory();
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandDeleted.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        killEntities();
        deleteBlocks();
        final IslandManager islandManager = IridiumSkyblock.getIslandManager();
        for (int id : coop) {
            islandManager.getIslandById(id).coop.remove(getId());
        }
        coop = null;
        hideBorder();
        this.owner = null;
        this.pos1 = null;
        this.pos2 = null;
        databaseManager.removeIslandMembers(this);
        this.center = null;
        this.home = null;
        islandManager.removeIsland(this);
        IridiumSkyblock.getInstance().saveConfigs();
        Bukkit.getScheduler().cancelTask(boosterid);
        boosterid = -1;
    }

    public void removeBan(@NotNull User user) {
        if (bans == null) bans = new HashSet<>();
        bans.remove(user);
    }

    public void addBan(@NotNull User user) {
        if (bans == null) bans = new HashSet<>();
        bans.add(user);
    }

    public void removeVote(@NotNull User user) {
        if (votes == null) votes = new HashSet<>();
        votes.remove(user);
    }

    public void addVote(@NotNull User user) {
        if (votes == null) votes = new HashSet<>();
        votes.add(user);
    }

    public boolean hasVoted(@NotNull User user) {
        if (votes == null) votes = new HashSet<>();
        return votes.contains(user.getPlayer());
    }

    public int getVotes() {
        if (votes == null) votes = new HashSet<>();
        return votes.size();
    }

    public boolean isBanned(@NotNull User user) {
        if (bans == null) bans = new HashSet<>();
        return bans.contains(user.getPlayer());
    }

    public void addCoop(@NotNull Island island) {
        if (coop == null) coop = new HashSet<>();
        for (final @NotNull User member : island.getMembers()) {
            Player pl = member.getPlayer();
            if (pl != null) {
                pl.sendMessage(Utils.color(messages.coopGiven
                    .replace("%player%", User.getUser(owner).getName())
                    .replace("%prefix%", config.prefix)));
            }
        }
        for (final @NotNull User member : getMembers()) {
            Player pl = member.getPlayer();
            if (pl != null) {
                pl.sendMessage(Utils.color(messages.coopAdded
                    .replace("%player%", User.getUser(island.getOwner()).getName())
                    .replace("%prefix%", config.prefix)));
            }
        }
        coop.add(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.add(id);
    }

    public void inviteCoop(Island island) {
        if (coopInvites == null) coopInvites = new HashSet<>();
        coopInvites.add(island.getId());
        for (final @NotNull User member : getMembers()) {
            Player pl = member.getPlayer();
            if (pl != null) {
                BaseComponent[] components = TextComponent.fromLegacyText(Utils.color(messages.coopInvite
                    .replace("%player%", User.getUser(island.getOwner()).getName())
                    .replace("%prefix%", config.prefix)));

                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is coop " + User.getUser(island.getOwner()).getName());
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to coop players island!").create());
                for (BaseComponent component : components) {
                    component.setClickEvent(clickEvent);
                    component.setHoverEvent(hoverEvent);
                }
                pl.getPlayer().spigot().sendMessage(components);
            }
        }
    }

    public void removeCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        coop.remove(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.remove(id);
        for (final @NotNull User member : island.getMembers()) {
            Player pl = member.getPlayer();
            if (pl != null) {
                pl.sendMessage(Utils.color(messages.coopTaken
                    .replace("%player%", User.getUser(owner).getName())
                    .replace("%prefix%", config.prefix)));
            }
        }
        for (final @NotNull User member : getMembers()) {
            Player pl = member.getPlayer();
            if (pl != null) {
                pl.sendMessage(Utils.color(messages.coopTaken
                    .replace("%player%", User.getUser(island.getOwner()).getName())
                    .replace("%prefix%", config.prefix)));
            }
        }
        getCoopGUI().getInventory().clear();
        getCoopGUI().addContent();
        island.getCoopGUI().getInventory().clear();
        island.getCoopGUI().addContent();
    }

    public void removeCoop(int id) {
        if (coop == null) coop = new HashSet<>();
        coop.remove(id);
    }

    public boolean isCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        if (island == null) return false;
        return coop.contains(island.id);
    }

    public Set<Integer> getCoop() {
        if (coop == null) coop = new HashSet<>();
        return coop;
    }

    public void spawnPlayers() {
        for (Player p : getPlayersOnIsland()) {
            spawnPlayer(p);
        }
    }

    public List<Player> getPlayersOnIsland() {
        List<Player> players = new ArrayList<>();
        for (final @NotNull Player p : Bukkit.getOnlinePlayers()) {
            if (isEntityInIsland(p)) {
                players.add(p);
            }
        }
        return players;
    }

    public void spawnPlayer(Player player) {
        if (player == null) return;
        if (Bukkit.getPluginManager().isPluginEnabled("EssentialsSpawn")) {
            EssentialsSpawn essentialsSpawn = (EssentialsSpawn) Bukkit.getPluginManager().getPlugin("EssentialsSpawn");
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            player.teleport(essentialsSpawn.getSpawn(essentials.getUser(player).getGroup()));
        } else {
            World world = Bukkit.getWorld(IridiumSkyblock.getConfiguration().worldSpawn);
            if (world == null) {
                world = Bukkit.getWorlds().get(0);
            }
            player.teleport(world.getSpawnLocation());
        }
    }

    public void setBiome(XBiome biome) {
        this.biome = biome;
        biome.setBiome(getPos1(), getPos2());
        for (int X = getPos1().getChunk().getX(); X <= getPos2().getChunk().getX(); X++) {
            for (int Z = getPos1().getChunk().getZ(); Z <= getPos2().getChunk().getZ(); Z++) {
                for (Player p : IridiumSkyblock.getIslandManager().getWorld().getPlayers()) {
                    if (p.getLocation().getWorld().equals(IridiumSkyblock.getIslandManager().getWorld())) {
                        nms.sendChunk(p, IridiumSkyblock.getIslandManager().getWorld().getChunkAt(X, Z));
                    }
                }
            }
        }
    }

    public void deleteBlocks() {
        valuableBlocks.clear();
        calculateValue();
        for (int X = getPos1().getBlockX(); X <= getPos2().getBlockX(); X++) {
            for (int Y = 0; Y <= 255; Y++) {
                for (int Z = getPos1().getBlockZ(); Z <= getPos2().getBlockZ(); Z++) {
                    nms.setBlockFast(IridiumSkyblock.getIslandManager().getWorld().getBlockAt(X, Y, Z), 0, (byte) 0);
                }
            }
        }
        if (IridiumSkyblock.getConfiguration().netherIslands) {
            for (int X = getPos1().getBlockX(); X <= getPos2().getBlockX(); X++) {
                for (int Y = 0; Y <= 255; Y++) {
                    for (int Z = getPos1().getBlockZ(); Z <= getPos2().getBlockZ(); Z++) {
                        nms.setBlockFast(IridiumSkyblock.getIslandManager().getWorld().getBlockAt(X, Y, Z), 0, (byte) 0);
                    }
                }
            }
        }
    }

    public void killEntities() {
        for (int X = getPos1().getChunk().getX(); X <= getPos2().getChunk().getX(); X++) {
            for (int Z = getPos1().getChunk().getZ(); Z <= getPos2().getChunk().getZ(); Z++) {
                Chunk overworld = IridiumSkyblock.getIslandManager().getWorld().getChunkAt(X, Z);
                Chunk nether = IridiumSkyblock.getIslandManager().getWorld().getChunkAt(X, Z);
                for (Entity e : overworld.getEntities()) {
                    if (!e.getType().equals(EntityType.PLAYER)) {
                        e.remove();
                    }
                }
                for (Entity e : nether.getEntities()) {
                    if (!e.getType().equals(EntityType.PLAYER)) {
                        e.remove();
                    }
                }
            }
        }
    }

    public Location getNetherhome() {
        if (netherhome == null) {
            netherhome = getHome().clone();
            netherhome.setWorld(IridiumSkyblock.getIslandManager().getNetherWorld());
        }
        return netherhome;
    }

    public void setOwner(OfflinePlayer owner) {
        for (final @NotNull User member : getMembers()) {
            final @Nullable Player memberPlayer = member.getPlayer();
            if (memberPlayer == null) continue;

            memberPlayer.sendMessage(Utils.color(messages.transferdOwnership
                .replace("%player%", owner.getName())
                .replace("%prefix%", config.prefix)));
        }
        User.getUser(getOwner()).setRole(Role.CoOwner);
        setOwner(owner);
        User.getUser(getOwner()).setRole(Role.Owner);
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = sizeLevel;

        pos1 = getCenter().clone().subtract(IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 0, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00);
        pos2 = getCenter().clone().add(IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 0, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00);
        sendBorder();
        setBiome(biome);
    }

    public void removeWarp(@NotNull Warp warp) {
        warps.remove(warp);
    }

    public @Nullable String getName() {
        if (name == null) name = User.getUser(owner).getName();
        return name;
    }

    public @NotNull Set<UUID> getMemberUuids() {
        return databaseManager.getIslandMemberUuids(this);
    }

    public void addTempValue(@NotNull Location location) {
        tempValues.add(location);
    }


    // Users
    public void addUser(@NotNull User user) {
        final @NotNull Set<User> members = getMembers();
        if (members.size() < upgrades.memberUpgrade.upgrades.get(memberLevel).size) {

            for (final @NotNull User member : members) {
                final @Nullable Player memberPlayer = member.getPlayer();
                if (memberPlayer == null) continue;
                memberPlayer.sendMessage(Utils.color(messages.playerJoinedYourIsland
                    .replace("%player%", user.getName())
                    .replace("%prefix%", config.prefix)));
            }
            bans.remove(user.getPlayer());
            user.setIslandId(id);
            user.setRole(Role.Member);
            user.clearInvites();
            addMember(user);
            teleportHome(user.getPlayer());
            user.clearInvites();
        } else {
            final @Nullable Player player = user.getPlayer();
            if (player != null)
                player.sendMessage(Utils.color(messages.maxMemberCount
                    .replace("%prefix%", config.prefix)));
        }
        getMembersGUI().getInventory().clear();
        getMembersGUI().addContent();
    }

    public void removeUser(@NotNull User user) {
        user.setIslandId(0);
        final @Nullable Player player = user.getPlayer();
        if (player != null) {
            spawnPlayer(player);
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        removeMember(user);
        user.setRole(Role.Visitor);
        for (final @NotNull User member : getMembers()) {
            final @Nullable Player memberPlayer = member.getPlayer();
            if (memberPlayer == null) continue;

            memberPlayer.sendMessage(Utils.color(messages.kickedMember
                .replace("%member%", user.getName())
                .replace("%prefix%", config.prefix)));
        }
        getMembersGUI().getInventory().clear();
        getMembersGUI().addContent();
    }


    // Entities
    public @NotNull Set<Entity> getEntities() {
        return getEntityUuids().stream()
            .map(Bukkit::getEntity)
            .collect(Collectors.toSet());
    }

    public @NotNull Set<UUID> getEntityUuids() {
        return databaseManager.getIslandEntityUuids(this);
    }

    public void addEntity(@NotNull Entity entity) {
        addEntityUuid(entity.getUniqueId());
    }

    public void addEntityUuid(@NotNull UUID uuid) {

    }

    public void removeEntity(@NotNull Entity entity) {
        removeEntityUuid(entity.getUniqueId());
    }

    public void removeEntityUuid(@NotNull UUID uuid) {

    }


    // Failed Generators
    public boolean isFailedGenerator(@NotNull Location location) {
        return databaseManager.isIslandFailedGenerator(this, location);
    }

    public void addFailedGenerator(@NotNull Location location) {
        databaseManager.addIslandFailedGenerator(this, location);
    }

    public void removeFailedGenerator(@NotNull Location location) {
        databaseManager.removeIslandFailedGenerator(this, location);
    }


    // Members
    public @NotNull Set<User> getMembers() {

    }

    public void addMember(@NotNull User user) {

    }

    public void removeMember(@NotNull User user) {

    }


    // Missions
    public void completeMission(@NotNull String missionName) {
        missionLevels.putIfAbsent(missionName, 1);

        missions.put(missionName, (config.missionRestart == MissionRestart.Instantly ? 0 : Integer.MIN_VALUE));

        final Mission mission = IridiumSkyblock
            .getMissions()
            .stream()
            .filter(m -> m.name.equalsIgnoreCase(missionName))
            .findAny()
            .orElse(null);
        if (mission == null) return;

        final Map<Integer, MissionData> levels = mission.levels;
        final int levelProgress = missionLevels.get(missionName);
        final MissionData level = levels.get(levelProgress);
        final int crystalReward = level.crystalReward;
        final int vaultReward = level.vaultReward;
        this.crystals += crystalReward;
        this.money += vaultReward;

        final @NotNull String titleMessage = messages.missionComplete
            .replace("%mission%", missionName)
            .replace("%level%", levelProgress + "");
        final @NotNull String subTitleMessage = messages.rewards
            .replace("%crystalsReward%", crystalReward + "")
            .replace("%vaultReward%", vaultReward + "");
        for (final @NotNull UUID memberUuid : databaseManager.getIslandMemberUuids(this)) {
            final @Nullable Player memberPlayer = Bukkit.getPlayer(memberUuid);
            if (memberPlayer == null) continue;
            nms.sendTitle(memberPlayer, titleMessage, 20, 40, 20);
            nms.sendSubTitle(memberPlayer, subTitleMessage, 20, 40, 20);
        }

        //Reset current mission status
        if (mission.levels.containsKey(levelProgress + 1)) {
            //We have another mission, put us on the next level
            missions.remove(missionName);
            missionLevels.put(missionName, levelProgress + 1);
        } else if (config.missionRestart == MissionRestart.Instantly) {
            missions.remove(missionName);
            missionLevels.remove(missionName);
        }
    }


    // Valuable Blocks
    public int getValuableBlockCountByName(@NotNull String name) {
        return valuableBlocks.getOrDefault(name, 0);
    }

    public void addValuableBlock(@NotNull Block block) {
        final @NotNull Material material = block.getType();
        final @NotNull XMaterial xmaterial = XMaterial.matchXMaterial(material);
        valuableBlocks.compute(xmaterial.name(), (name, original) -> {
            if (original == null) return 1;
            return original + 1;
        });
        if (updating)
            tempValues.add(block.getLocation());
        calculateValue();
    }

    public void removeValuableBlock(@NotNull Block block) {
        @NotNull final Material material = block.getType();
        final @NotNull XMaterial xmaterial = XMaterial.matchXMaterial(material);
        valuableBlocks.computeIfPresent(xmaterial.name(), (name, original) -> original - 1);
        if (updating)
            tempValues.remove(block.getLocation());
    }

}
