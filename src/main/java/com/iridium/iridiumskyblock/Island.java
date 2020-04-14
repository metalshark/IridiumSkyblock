package com.iridium.iridiumskyblock;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.iridium.iridiumskyblock.api.IslandCreateEvent;
import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
import com.iridium.iridiumskyblock.configs.*;
import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.gui.*;
import com.iridium.iridiumskyblock.iterators.IslandChunkIterator;
import com.iridium.iridiumskyblock.runnables.InitIslandBlocksRunnable;
import com.iridium.iridiumskyblock.runnables.InitIslandBlocksWithSenderRunnable;
import com.iridium.iridiumskyblock.support.*;
import lombok.Getter;
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
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class Island {

    public static class Warp {
        Location location;
        String name;
        String password;

        public Warp(Location location, String name, String password) {
            this.location = location;
            this.name = name;
            this.password = password;
        }

        public Location getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }
    }

    @Getter private String owner;
    @Getter private Set<String> members;
    @Getter private Location pos1;
    @Getter private Location pos2;
    @Getter private Location center;
    @Getter @Setter private Location home;
    @Setter private Location netherhome;

    @Getter private transient UpgradeGUI upgradeGUI;
    @Getter private transient BoosterGUI boosterGUI;
    @Getter private transient MissionsGUI missionsGUI;
    @Getter private transient MembersGUI membersGUI;
    @Getter private transient WarpGUI warpGUI;
    @Getter private transient BorderColorGUI borderColorGUI;
    @Getter private transient SchematicSelectGUI schematicSelectGUI;
    @Getter private transient PermissionsGUI permissionsGUI;
    @Getter private transient IslandMenuGUI islandMenuGUI;
    @Getter private transient CoopGUI coopGUI;
    @Getter private transient BankGUI bankGUI;
    @Getter private transient BiomeGUI biomeGUI;

    @Getter private int id;

    @Getter @Setter private int spawnerBooster;
    @Getter @Setter private int farmingBooster;
    @Getter @Setter private int expBooster;
    @Getter @Setter private int flightBooster;

    private transient int boosterid;

    @Getter @Setter private int crystals;

    @Getter private int sizeLevel;
    @Getter @Setter private int memberLevel;
    @Getter @Setter private int warpLevel;
    @Getter @Setter private int oreLevel;

    public transient int generateID;

    @Getter private double value;

    public Map<String, Integer> valuableBlocks;
    public transient Set<Location> tempValues;
    public transient Map<String, Integer> spawners;

    @Getter private final List<Warp> warps;

    private double startvalue;

    private Map<String, Integer> missions = new HashMap<>();

    private Map<String, Integer> missionLevels = new HashMap<>();

    @Getter @Setter private boolean visit;

    @Getter @Setter private Color borderColor;

    private Map<Role, Permissions> permissions;

    @Getter @Setter private String schematic;

    private Set<String> bans;

    private Set<String> votes;

    private Set<Integer> coop;

    public transient Set<Integer> coopInvites;

    @Setter private String name;

    public double money;
    public int exp;

    @Getter private XBiome biome;

    public transient Set<Location> failedGenerators;

    private Date lastRegen;

    private transient int initBlocks;

    public transient boolean updating = false;

    public int percent = 0;

    public Island(Player owner, Location pos1, Location pos2, Location center, Location home, Location netherhome, int id) {
        User user = User.getUser(owner);
        user.role = Role.Owner;
        this.biome = IridiumSkyblock.getConfiguration().defaultBiome;
        valuableBlocks = new HashMap<>();
        spawners = new HashMap<>();
        tempValues = new HashSet<>();
        this.owner = user.player;
        this.name = user.name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.center = center;
        this.home = home;
        this.netherhome = netherhome;
        this.members = new HashSet<>(Collections.singletonList(user.player));
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
        borderColor = IridiumSkyblock.border.startingColor;
        visit = IridiumSkyblock.getConfiguration().defaultIslandPublic;
        permissions = new HashMap<>(IridiumSkyblock.getConfiguration().defaultPermissions);
        this.coop = new HashSet<>();
        this.bans = new HashSet<>();
        this.votes = new HashSet<>();
        init();
        Bukkit.getPluginManager().callEvent(new IslandCreateEvent(owner, this));
    }

    public long getBlockCount() {
        final double minX = pos1.getX();
        final double maxX = pos2.getX();
        final double width = maxX - minX;

        final double minZ = pos1.getZ();
        final double maxZ = pos2.getZ();
        final double depth = maxZ - minZ;

        final IslandManager islandManager = IridiumSkyblock.getIslandManager();
        final World islandWorld = islandManager.getWorld();
        final double maxY = islandWorld.getMaxHeight();

        return (long ) (width * maxY * depth);
    }

    public void initBlocks() {
        updating = true;

        final Config config = IridiumSkyblock.getConfiguration();
        final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final Runnable task = new InitIslandBlocksRunnable(this, config.blocksPerTick, () -> {
            if (IridiumSkyblock.blockspertick != -1) {
                config.blocksPerTick = IridiumSkyblock.blockspertick;
                IridiumSkyblock.blockspertick = -1;
            }
            scheduler.cancelTask(initBlocks);
            initBlocks = -1;
            plugin.updatingBlocks = false;
            updating = false;
            valuableBlocks.clear();
            spawners.clear();
            for (Location location : tempValues) {
                final Block block = location.getBlock();
                if (!(Utils.isBlockValuable(block) || !(block.getState() instanceof CreatureSpawner))) continue;
                final Material material = block.getType();
                final XMaterial xmaterial = XMaterial.matchXMaterial(material);
                valuableBlocks.compute(xmaterial.name(), (xmaterialName, original) -> {
                    if (original == null) return 1;
                    return original + 1;
                });
            }
            tempValues.clear();
            calculateIslandValue();
        });
        initBlocks = scheduler.scheduleSyncRepeatingTask(plugin, task, 0, 1);
    }

    public void forceInitBlocks(CommandSender sender, int blocksPerTick, String name) {
        final Config config = IridiumSkyblock.getConfiguration();
        final Messages messages = IridiumSkyblock.getMessages();
        if (sender != null)
            sender.sendMessage(Utils.color(messages.updateStarted
                    .replace("%player%", name)
                    .replace("%prefix%", config.prefix)));
        updating = true;
        final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
        final BukkitScheduler scheduler = Bukkit.getScheduler();
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
            for (Location location : tempValues) {
                final Block block = location.getBlock();
                if (!(Utils.isBlockValuable(block) || !(block.getState() instanceof CreatureSpawner))) continue;
                final Material material = block.getType();
                final XMaterial xmaterial = XMaterial.matchXMaterial(material);
                valuableBlocks.compute(xmaterial.name(), (xmaterialName, original) -> {
                    if (original == null) return 1;
                    return original + 1;
                });
            }
            tempValues.clear();
            calculateIslandValue();
        });
        initBlocks = scheduler.scheduleSyncRepeatingTask(plugin, task, 0, 1);
    }

    public void resetMissions() {
        if (missions == null) missions = new HashMap<>();
        if (missionLevels == null) missionLevels = new HashMap<>();
        missions.clear();
        missionLevels.clear();
    }

    public int getMission(String mission) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        return missions.get(mission);
    }

    public void addMission(String mission, int amount) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        if (missions.get(mission) == Integer.MIN_VALUE) return;
        missions.put(mission, missions.get(mission) + amount);
        for (Missions.Mission m : IridiumSkyblock.getMissions().missions) {
            if (m.name.equalsIgnoreCase(mission)) {
                if (!getMissionLevels().containsKey(mission)) getMissionLevels().put(mission, 1);
                if (m.levels.get(getMissionLevels().get(mission)).amount <= missions.get(mission)) {
                    completeMission(mission);
                }
            }
        }
    }

    public void setMission(String mission, int amount) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        if (missions.get(mission) == Integer.MIN_VALUE) return;
        missions.put(mission, amount);
        for (Missions.Mission m : IridiumSkyblock.getMissions().missions) {
            if (m.name.equalsIgnoreCase(mission)) {
                if (!getMissionLevels().containsKey(mission)) getMissionLevels().put(mission, 1);
                if (m.levels.get(getMissionLevels().get(mission)).amount <= missions.get(mission)) {
                    completeMission(mission);
                }
            }
        }
    }

    public Permissions getPermissions(User user) {
        if (user.bypassing) return new Permissions();

        Role role;
        if (user.islandID == getId()) role = user.getRole();
        else if (isCoop(user.getIsland())) role = Role.Member;
        else role = Role.Visitor;
        return getPermissions(role);
    }

    public Permissions getPermissions(Role role) {
        if (permissions == null)
            permissions = new HashMap<>(IridiumSkyblock.getConfiguration().defaultPermissions);
        if (!permissions.containsKey(role)) {
            permissions.put(role, new Permissions());
        }
        return permissions.get(role);
    }

    public void sendBorder() {
        for (Player p : getPlayersOnIsland()) {
            sendBorder(p);
        }
    }

    public void hideBorder() {
        for (Player p : getPlayersOnIsland()) {
            hideBorder(p);
        }
    }

    public void sendBorder(Player p) {
        double size = IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size;
        if (size % 2 == 0) size++;
        if (p.getLocation().getWorld().equals(IridiumSkyblock.getIslandManager().getWorld())) {
            IridiumSkyblock.nms.sendWorldBorder(p, borderColor, size, getCenter());
        } else if (IridiumSkyblock.getConfiguration().netherIslands) {
            Location loc = getCenter().clone();
            loc.setWorld(IridiumSkyblock.getIslandManager().getNetherWorld());
            IridiumSkyblock.nms.sendWorldBorder(p, borderColor, size, loc);
        }
    }

    public void hideBorder(Player p) {
        IridiumSkyblock.nms.sendWorldBorder(p, borderColor, Integer.MAX_VALUE, getCenter().clone());
    }

    public void completeMission(String missionName) {
        missionLevels.putIfAbsent(missionName, 1);

        final Config config = IridiumSkyblock.getConfiguration();
        missions.put(missionName, (config.missionRestart == MissionRestart.Instantly ? 0 : Integer.MIN_VALUE));

        final Mission mission = IridiumSkyblock
                .getMissions()
                .missions
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

        final Messages messages = IridiumSkyblock.getMessages();
        final String titleMessage = messages.missionComplete
                .replace("%mission%", missionName)
                .replace("%level%", levelProgress + "");
        final String subTitleMessage = messages.rewards
                .replace("%crystalsReward%", crystalReward + "")
                .replace("%vaultReward%", vaultReward + "");
        for (String member : members) {
            final User user = User.getUser(member);
            final Player p = Bukkit.getPlayer(user.name);
            if (p == null) continue;
            IridiumSkyblock.nms.sendTitle(p, titleMessage, 20, 40, 20);
            IridiumSkyblock.nms.sendSubTitle(p, subTitleMessage, 20, 40, 20);
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

    public void calculateIslandValue() {
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

        for (Mission mission : IridiumSkyblock.getMissions().missions) {
            missionLevels.putIfAbsent(mission.name, 1);
            if (mission.levels.get(missionLevels.get(mission.name)).type == MissionType.VALUE_INCREASE) {
                setMission(mission.name, (int) (value.get() - startvalue));
            }
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

    public void addUser(User user) {
        if (members.size() < IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(memberLevel).size) {

            for (String player : members) {
                User u = User.getUser(player);
                Player p = Bukkit.getPlayer(u.name);
                if (p != null) {
                    p.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerJoinedYourIsland.replace("%player%", user.name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            bans.remove(user.player);
            user.islandID = id;
            user.role = Role.Member;
            user.invites.clear();
            members.add(user.player);
            teleportHome(Bukkit.getPlayer(user.name));
            user.invites.clear();
        } else {
            if (Bukkit.getPlayer(user.name) != null) {
                Bukkit.getPlayer(user.name).sendMessage(Utils.color(IridiumSkyblock.getMessages().maxMemberCount.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        getMembersGUI().getInventory().clear();
        getMembersGUI().addContent();
    }

    public void removeUser(User user) {
        user.islandID = 0;
        Player player = Bukkit.getPlayer(user.name);
        if (player != null) {
            spawnPlayer(player);
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        members.remove(user.player);
        user.role = Role.Visitor;
        for (String member : members) {
            User u = User.getUser(member);
            Player p = Bukkit.getPlayer(u.name);
            if (p != null) {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().kickedMember.replace("%member%", user.name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        getMembersGUI().getInventory().clear();
        getMembersGUI().addContent();
    }

    public boolean isInIsland(Location location) {
        if (location == null) return false;
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
        if (members == null) {
            members = new HashSet<>();
            members.add(owner);
        }

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
                for (String player : members) {
                    Player p = Bukkit.getPlayer(player);
                    if (p != null) {
                        if ((!p.hasPermission("IridiumSkyblock.Fly") && !p.hasPermission("iridiumskyblock.fly")) && p.getGameMode().equals(GameMode.SURVIVAL)) {
                            p.setAllowFlight(false);
                            p.setFlying(false);
                            User.getUser(p).flying = false;
                        }
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
        c.add(Calendar.SECOND, IridiumSkyblock.getConfiguration().regenCooldown);
        lastRegen = c.getTime();
        if (deleteBlocks) deleteBlocks();
        pasteSchematic();
        killEntities();
        //Reset island home
        for (Schematics.FakeSchematic schematic : IridiumSkyblock.getInstance().schems.keySet()) {
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
        User.getUser(player).teleportingHome = false;
        teleportHome(player);
        sendBorder(player);
        IridiumSkyblock.nms.sendTitle(player, IridiumSkyblock.getMessages().islandCreated, 20, 40, 20);
        if (!IridiumSkyblock.getMessages().islandCreatedSubtitle.isEmpty())
            IridiumSkyblock.nms.sendSubTitle(player, IridiumSkyblock.getMessages().islandCreatedSubtitle, 20, 40, 20);
    }

    private void pasteSchematic() {
        for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.getInstance().schems.keySet()) {
            if (fakeSchematic.name.equals(schematic)) {
                if (IridiumSkyblock.getInstance().schems.containsKey(fakeSchematic)) {
                    IridiumSkyblock.getInstance().schems.get(fakeSchematic).pasteSchematic(getCenter().clone(), this);
                } else {
                    IridiumSkyblock.getInstance().getLogger().warning("Failed to load schematic: " + fakeSchematic.name);
                    getCenter().getBlock().setType(Material.STONE);
                }
                if (IridiumSkyblock.getConfiguration().debugSchematics) {
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
                    if (IridiumSkyblock.getInstance().netherschems.containsKey(fakeSchematic)) {
                        IridiumSkyblock.getInstance().netherschems.get(fakeSchematic).pasteSchematic(center, this);
                    } else {
                        IridiumSkyblock.getInstance().getLogger().warning("Failed to load schematic: " + fakeSchematic.netherisland);
                    }
                }
                return;
            }
        }
        IridiumSkyblock.getInstance().getLogger().warning("Could not find schematic: " + schematic);
        getCenter().getBlock().setType(Material.STONE);
    }

    public void clearInventories() {
        if (IridiumSkyblock.getConfiguration().clearInventories) {
            for (String player : members) {
                User user = User.getUser(player);
                if (Bukkit.getPlayer(user.name) != null) Bukkit.getPlayer(user.name).getInventory().clear();
            }
        }
    }

    public void teleportHome(Player p) {
        if (getHome() == null) home = getCenter();
        if (User.getUser(p).teleportingHome) {
            return;
        }
        if (isBanned(User.getUser(p)) && !members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().bannedFromIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return;
        }
        if (getSchematic() == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.getInstance().schems.size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.getInstance().schems.keySet()) {
                        setSchematic(schematic.name);
                    }
                } else {
                    p.openInventory(getSchematicSelectGUI().getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().teleportingHome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitingIsland.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            for (String pl : members) {
                Player player = Bukkit.getPlayer(User.getUser(pl).name);
                if (player != null) {
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitedYourIsland.replace("%player%", p.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
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
                User.getUser(p).teleportingHome = true;
                pasteSchematic(p, false);
            }
        }
    }

    public void teleportNetherHome(Player p) {
        if (getNetherhome() == null) {
            netherhome = center;
            netherhome.setWorld(IridiumSkyblock.getIslandManager().getNetherWorld());
        }
        if (User.getUser(p).teleportingHome) {
            return;
        }
        if (isBanned(User.getUser(p)) && !members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().bannedFromIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return;
        }
        if (getSchematic() == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.getInstance().schems.size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.getInstance().schems.keySet()) {
                        setSchematic(schematic.name);
                    }
                } else {
                    p.openInventory(getSchematicSelectGUI().getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().teleportingHome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitingIsland.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            for (String pl : members) {
                Player player = Bukkit.getPlayer(User.getUser(pl).name);
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
                User.getUser(p).teleportingHome = true;
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
            IridiumSkyblock.getInstance().updatingBlocks = false;
        }
        permissions.clear();
        clearInventories();
        spawnPlayers();
        for (String player : members) {
            User.getUser(player).islandID = 0;
            Player p = Bukkit.getPlayer(User.getUser(player).name);
            if (p != null) {
                p.closeInventory();
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandDeleted.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        killEntities();
        deleteBlocks();
        final IslandManager islandManager = IridiumSkyblock.getIslandManager();
        for (int id : coop) {
            islandManager.getIslandViaId(id).coop.remove(getId());
        }
        coop = null;
        hideBorder();
        this.owner = null;
        this.pos1 = null;
        this.pos2 = null;
        this.members = null;
        this.center = null;
        this.home = null;
        islandManager.removeIsland(this);
        this.id = 0;
        IridiumSkyblock.getInstance().saveConfigs();
        Bukkit.getScheduler().cancelTask(boosterid);
        boosterid = -1;
    }

    public void removeBan(User user) {
        if (bans == null) bans = new HashSet<>();
        bans.remove(user.player);
    }

    public void addBan(User user) {
        if (bans == null) bans = new HashSet<>();
        bans.add(user.player);
    }

    public void removeVote(User user) {
        if (votes == null) votes = new HashSet<>();
        votes.remove(user.player);
    }

    public void addVote(User user) {
        if (votes == null) votes = new HashSet<>();
        votes.add(user.player);
    }

    public boolean hasVoted(User user) {
        if (votes == null) votes = new HashSet<>();
        return votes.contains(user.player);
    }

    public int getVotes() {
        if (votes == null) votes = new HashSet<>();
        return votes.size();
    }

    public boolean isBanned(User user) {
        if (bans == null) bans = new HashSet<>();
        return bans.contains(user.player);
    }

    public void addCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        for (String member : island.getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopGiven.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        for (String member : getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopAdded.replace("%player%", User.getUser(island.getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        coop.add(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.add(id);
    }

    public void inviteCoop(Island island) {
        if (coopInvites == null) coopInvites = new HashSet<>();
        coopInvites.add(island.getId());
        for (String member : getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                BaseComponent[] components = TextComponent.fromLegacyText(Utils.color(IridiumSkyblock.getMessages().coopInvite.replace("%player%", User.getUser(island.getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));

                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is coop " + User.getUser(island.getOwner()).name);
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
        for (String member : island.getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopTaken.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        for (String member : getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopTaken.replace("%player%", User.getUser(island.getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isInIsland(p.getLocation())) {
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
                        IridiumSkyblock.nms.sendChunk(p, IridiumSkyblock.getIslandManager().getWorld().getChunkAt(X, Z));
                    }
                }
            }
        }
    }

    public void deleteBlocks() {
        valuableBlocks.clear();
        calculateIslandValue();
        for (int X = getPos1().getBlockX(); X <= getPos2().getBlockX(); X++) {
            for (int Y = 0; Y <= 255; Y++) {
                for (int Z = getPos1().getBlockZ(); Z <= getPos2().getBlockZ(); Z++) {
                    IridiumSkyblock.nms.setBlockFast(IridiumSkyblock.getIslandManager().getWorld().getBlockAt(X, Y, Z), 0, (byte) 0);
                }
            }
        }
        if (IridiumSkyblock.getConfiguration().netherIslands) {
            for (int X = getPos1().getBlockX(); X <= getPos2().getBlockX(); X++) {
                for (int Y = 0; Y <= 255; Y++) {
                    for (int Z = getPos1().getBlockZ(); Z <= getPos2().getBlockZ(); Z++) {
                        IridiumSkyblock.nms.setBlockFast(IridiumSkyblock.getIslandManager().getWorld().getBlockAt(X, Y, Z), 0, (byte) 0);
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

        for (String player : members) {
            User user = User.getUser(player);
            Player p = Bukkit.getPlayer(user.name);
            if (p != null) {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().transferdOwnership.replace("%player%", owner.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));

            }
        }
        User.getUser(getOwner()).role = Role.CoOwner;
        this.owner = owner.getUniqueId().toString();
        User.getUser(getOwner()).role = Role.Owner;
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = sizeLevel;

        pos1 = getCenter().clone().subtract(IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 0, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00);
        pos2 = getCenter().clone().add(IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 0, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00);
        sendBorder();
        setBiome(biome);
    }

    public void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    public String getName() {
        if (name == null) name = User.getUser(getOwner()).name;
        return name;
    }

    public Map<String, Integer> getMissionLevels() {
        if (missionLevels == null) missionLevels = new HashMap<>();
        return missionLevels;
    }
}
