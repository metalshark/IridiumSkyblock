package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.commands.CommandManager;
import com.iridium.iridiumskyblock.configs.*;
import com.iridium.iridiumskyblock.db.DatabaseManager;
import com.iridium.iridiumskyblock.gui.*;
import com.iridium.iridiumskyblock.listeners.*;
import com.iridium.iridiumskyblock.nms.NMS;
import com.iridium.iridiumskyblock.placeholders.ClipPlaceholderAPIManager;
import com.iridium.iridiumskyblock.placeholders.MVDWPlaceholderAPIManager;
import com.iridium.iridiumskyblock.runnables.DailyRunnable;
import com.iridium.iridiumskyblock.runnables.IslandValueRunnable;
import com.iridium.iridiumskyblock.serializer.Persist;
import com.iridium.iridiumskyblock.support.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IridiumSkyblock extends JavaPlugin {

    @Getter @Nullable public static Config configuration;
    @Getter public static Messages messages;
    @Getter public static Missions missions;
    public static Upgrades upgrades;
    @Getter public static Boosters boosters;
    @Getter public static Inventories inventories;
    @Getter public static Schematics schematics;
    @Getter public static Commands commands;
    @Getter public static BlockValues blockValues;
    @Getter public static Shop shop;
    public static TopGUI topGUI;
    @Getter public static ShopGUI shopGUI;
    public static Border border;
    public static Map<Integer, VisitGUI> visitGUI;
    public static Map<Integer, List<String>> oreUpgradeCache = new HashMap<>();
    public static Map<Integer, List<String>> netherOreUpgradeCache = new HashMap<>();
    public static SkyblockGenerator generator;
    public static WorldEdit worldEdit;
    @Getter private static IridiumSkyblock instance;
    @Getter private static Persist persist;

    @Getter public static IslandManager islandManager;
    @Getter private static CommandManager commandManager;
    @Getter private static DatabaseManager databaseManager;
    public Map<Schematics.FakeSchematic, Schematic> schems = new HashMap<>();
    public Map<Schematics.FakeSchematic, Schematic> netherschems = new HashMap<>();
    public boolean updatingBlocks = false;
    public Map<String, String> languages = new HashMap<>();
    public LanguagesGUI languagesGUI;
    @Getter private String latest;

    public Map<UUID, Island> entities = new HashMap<>();

    public static NMS nms;

    public static int blockspertick;

    private ScheduledExecutorService dailyScheduler;

    public static Upgrades getUpgrades() {
        if (upgrades == null) {
            upgrades = new Upgrades();
            IridiumSkyblock.getPersist().getFile(upgrades).delete();
            IridiumSkyblock.getInstance().saveConfigs();
        }
        return upgrades;
    }

    @Override
    public void onEnable() {
        blockspertick = -1;
        try {
            nms = (NMS) Class.forName("com.iridium.iridiumskyblock.nms." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]).newInstance();
        } catch (ClassNotFoundException e) {
            //Unsupported Version
            getLogger().info("Unsupported Version Detected: " + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
            getLogger().info("Try updating from spigot");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
        } catch (ClassNotFoundException e) {
            getLogger().info("CraftBukkit is not Supported");
            getLogger().info("Please use Spigot instead");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            generator = new SkyblockGenerator();
            instance = this;

            super.onEnable();
            Bukkit.getUpdateFolderFile().mkdir();
            getDataFolder().mkdir();

            persist = new Persist();

            new Metrics(IridiumSkyblock.getInstance());

            if (!loadConfigs()) return;
            saveConfigs();

            startCounting();
            getLanguages();
            Bukkit.getScheduler().runTask(this, () -> { // Call this a tick later to ensure all worlds are loaded
                loadIslandManager();
                if (islandManager == null) return;

                if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) registerMultiverse();

                // Call it as a delayed task to wait for the server to properly load first
                Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), IridiumSkyblock.getInstance()::islandValueManager);

                topGUI = new TopGUI();
                shopGUI = new ShopGUI();
                visitGUI = new HashMap<>();

                registerListeners(new EntitySpawnListener(), new LeafDecayListener(), new BlockPistonListener(), new EntityPickupItemListener(), new PlayerTalkListener(), new ItemCraftListener(), new PlayerTeleportListener(), new PlayerPortalListener(), new BlockBreakListener(), new BlockPlaceListener(), new PlayerInteractListener(), new BlockFromToListener(), new SpawnerSpawnListener(), new EntityDeathListener(), new PlayerJoinLeaveListener(), new BlockGrowListener(), new PlayerTalkListener(), new PlayerMoveListener(), new EntityDamageByEntityListener(), new PlayerExpChangeListener(), new PlayerFishListener(), new EntityExplodeListener());

                Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::saveIslandManager, 0, 20 * 60);

                if (configuration.doIslandBackup)
                    Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::backupIslandManager, 0, 20 * 60 * configuration.backupIntervalMinutes);

                Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addPages, 0, 20 * 60);

                setupPlaceholderAPI();

                Plugin worldedit = Bukkit.getPluginManager().getPlugin("WorldEdit");
                if (worldedit != null) {
                    if (worldedit.getDescription().getVersion().startsWith("6")) {
                        worldEdit = new WorldEdit6();
                    } else if (worldedit.getDescription().getVersion().startsWith("7")) {
                        worldEdit = new WorldEdit7();
                    }
                }

                try {
                    loadSchematics();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Bukkit.getPluginManager().getPlugin("Vault") != null) new Vault();
                if (Bukkit.getPluginManager().isPluginEnabled("WildStacker")) new Wildstacker();
                if (Bukkit.getPluginManager().isPluginEnabled("MergedSpawner")) new MergedSpawners();
                if (Bukkit.getPluginManager().isPluginEnabled("UltimateStacker")) new UltimateStacker();
                if (Bukkit.getPluginManager().isPluginEnabled("EpicSpawners")) new EpicSpawners();
                if (Bukkit.getPluginManager().isPluginEnabled("AdvancedSpawners")) new AdvancedSpawners();
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    registerListeners(new ExpansionUnregisterListener());

                getLogger().info("-------------------------------");
                getLogger().info("");
                getLogger().info(getDescription().getName() + " Enabled!");
                getLogger().info("");
                getLogger().info("-------------------------------");

                Bukkit.getScheduler().scheduleAsyncDelayedTask(this, () -> {
                    try {
                        latest = new BufferedReader(new InputStreamReader(new URL("https://api.spigotmc.org/legacy/update.php?resource=62480").openConnection().getInputStream())).readLine();
                    } catch (IOException e) {
                        getLogger().warning("Failed to connect to api.spigotmc.org");
                    }
                    if (latest != null && !latest.equals(getDescription().getVersion())) {
                        getLogger().info("Newer version available: " + latest);
                        if (configuration.automaticUpdate) {
                            getLogger().info("Attempting to download version: " + latest);
                            try {
                                getFile().renameTo(new File(getFile().getParentFile(), "/IridiumSkyblock-" + latest + ".jar"));
                                URL url = new URL("http://www.iridiumllc.com/IridiumSkyblock-" + latest + ".jar");
                                URLConnection conn = url.openConnection();
                                conn.setConnectTimeout(15000);
                                conn.setReadTimeout(15000);
                                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                                conn.setAllowUserInteraction(false);
                                conn.setDoOutput(true);
                                InputStream in = conn.getInputStream();

                                File file = new File(Bukkit.getUpdateFolderFile() + "/IridiumSkyblock-" + latest + ".jar");
                                file.createNewFile();
                                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                                byte[] buffer = new byte[1024];

                                int numRead;
                                while ((numRead = in.read(buffer)) != -1) {
                                    out.write(buffer, 0, numRead);
                                }
                                in.close();
                                out.close();
                            } catch (Exception e) {
                                getLogger().info("Failed to connect to update server");
                            }
                        }
                    }
                });
            });
        } catch (Exception e) {
            sendErrorMessage(e);
        }
        dailyScheduler = Executors.newScheduledThreadPool(1);
    }

    public void getLanguages() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            languages.clear();
            try {
                URLConnection connection = new URL("https://iridiumllc.com/languages.php").openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.setAllowUserInteraction(false);
                connection.setDoOutput(true);
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNext()) {
                    String language = scanner.next();
                    languages.put(language.split("-")[0], language.split("-")[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            languagesGUI = new LanguagesGUI();
        });
    }

    public void setLanguage(String language, Player player) {
        ConfirmationGUI gui = new ConfirmationGUI(() -> {
            //Reset the configs back to default
            persist.getFile(commands).delete();
            persist.getFile(inventories).delete();
            persist.getFile(messages).delete();
            persist.getFile(missions).delete();
            if (!language.equalsIgnoreCase("English")) {
                downloadConfig(language, persist.getFile(commands));
                downloadConfig(language, persist.getFile(inventories));
                downloadConfig(language, persist.getFile(messages));
                downloadConfig(language, persist.getFile(missions));
            }
            loadConfigs();
            saveConfigs();
            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().reloaded.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }, "Change Language");
        player.openInventory(gui.getInventory());
    }

    public void downloadConfig(String language, File file) {
        getLogger().info("https://iridiumllc.com/Languages/" + language + "/" + file.getName());
        try {
            URLConnection connection = new URL("https://iridiumllc.com/Languages/" + language + "/" + file.getName()).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setAllowUserInteraction(false);
            connection.setDoOutput(true);
            InputStream in = connection.getInputStream();

            if (!file.exists()) file.createNewFile();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[1024];

            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            IridiumSkyblock.getInstance().getLogger().info("Failed to connect to Translation servers");
        }
    }

    private void registerMultiverse() {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + islandManager.getWorld().getName() + " normal -g " + getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + getName() + " " + islandManager.getWorld().getName());

        if (IridiumSkyblock.configuration.netherIslands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + islandManager.getNetherWorld().getName() + " nether -g " + getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + getName() + " " + islandManager.getNetherWorld().getName());
        }
    }

    @Override
    public void onDisable() {
        try {
            super.onDisable();

            dailyScheduler.shutdown();

            saveData();

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.closeInventory();
            }

            getLogger().info("-------------------------------");
            getLogger().info("");
            getLogger().info(getDescription().getName() + " Disabled!");
            getLogger().info("");
            getLogger().info("-------------------------------");
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    public void saveIslandManager() {
        if (islandManager != null) {
            getDataFolder().mkdir();
            getPersist().save(islandManager, getPersist().getFile("IslandManager_temp"));
            try {
                if (persist.load(IslandManager.class, getPersist().getFile("IslandManager_temp")) == null) {
                    getPersist().getFile("IslandManager_temp").delete();
                    return;
                }
            } catch (Exception e) {
                getPersist().getFile("IslandManager_temp").delete();
                return;
            }
            getPersist().getFile(islandManager).delete();
            getPersist().getFile("IslandManager_temp").renameTo(getPersist().getFile(islandManager));
        }
    }

    public void backupIslandManager() {
        if (islandManager != null) {
            File backupsFolder = new File(getDataFolder(), "backups");
            if (!backupsFolder.exists()) backupsFolder.mkdir();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -configuration.deleteBackupsAfterDays);
            for (File file : backupsFolder.listFiles()) {
                Date date = getLocalDateTime(file.getName().replace(".json", "").replace("IslandManager_", ""));
                if (date == null) {
                    file.delete();
                } else {
                    if (date.before(cal.getTime())) {
                        file.delete();
                    }
                }
            }
            getPersist().save(islandManager, new File(backupsFolder, "IslandManager_" + getCurrentTimeStamp() + ".json"));
        }
    }

    public String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }

    public Date getLocalDateTime(String time) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//dd/MM/yyyy
        try {
            return sdfDate.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (worldName.equals(configuration.worldName) || worldName.equals(configuration.netherWorldName))
            return generator;
        return super.getDefaultWorldGenerator(worldName, id);
    }

    private void addPages() {
        int size = (int) (Math.floor(Utils.getIslands().size() / 45.00) + 1);
        for (int i = 1; i <= size; i++) {
            if (!visitGUI.containsKey(i)) {
                visitGUI.put(i, new VisitGUI(i));
            }
        }
    }

    public void startCounting() {
        final Runnable task = new DailyRunnable();

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final long initalDelay = (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000;

        dailyScheduler.scheduleAtFixedRate(task, initalDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public void islandValueManager() {
        // Loop through all online islands and make sure Island#valuableBlocks is accurate
        final Runnable task = new IslandValueRunnable();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, task, 0, 0);
    }

    public void sendErrorMessage(Exception e) {
        e.printStackTrace();
    }

    public void registerListeners(Listener... listener) {
        for (Listener l : listener) {
            Bukkit.getPluginManager().registerEvents(l, this);
        }
    }

    private void setupPlaceholderAPI() {
        Plugin mvdw = getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI");
        if (mvdw != null && mvdw.isEnabled()) {
            new MVDWPlaceholderAPIManager().register();
            getLogger().info("Successfully registered placeholders with MVDWPlaceholderAPI.");
        }
        setupClipsPlaceholderAPI();
    }

    public void setupClipsPlaceholderAPI() {
        Plugin clip = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (clip != null && clip.isEnabled()) {
            if (new ClipPlaceholderAPIManager().register()) {
                getLogger().info("Successfully registered placeholders with PlaceholderAPI.");
            }
        }
    }

    public void loadSchematics() throws IOException {
        File schematicFolder = new File(getDataFolder(), "schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdir();
        }
        if (!new File(schematicFolder, "island.schematic").exists()) {
            if (getResource("schematics/island.schematic") != null) {
                saveResource("schematics/island.schematic", false);
            }
        }
        if (!new File(schematicFolder, "nether.schematic").exists()) {
            if (getResource("schematics/nether.schematic") != null) {
                saveResource("schematics/nether.schematic", false);
            }
        }

        schems.clear();

        for (Schematics.FakeSchematic fakeSchematic : schematics.schematics) {
            File schem = new File(schematicFolder, fakeSchematic.name);
            try {
                if (schem.exists()) {
                    schems.put(fakeSchematic, Schematic.loadSchematic(schem));
                    if (fakeSchematic.netherisland == null) {
                        fakeSchematic.netherisland = fakeSchematic.name;
                    }
                    schem = new File(schematicFolder, fakeSchematic.netherisland);
                    if (schem.exists()) {
                        netherschems.put(fakeSchematic, Schematic.loadSchematic(schem));
                    }
                } else {
                    IridiumSkyblock.getInstance().getLogger().warning("Failed to load schematic: " + fakeSchematic.name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                IridiumSkyblock.getInstance().getLogger().warning("Failed to load schematic: " + fakeSchematic.name);
            }
        }
    }

    public void loadIslandManager() {
        islandManager = persist.getFile(IslandManager.class).exists() ? persist.load(IslandManager.class) : new IslandManager();

        if (islandManager == null) return;

        for (@NotNull Island island : databaseManager.getIslands()) {
            island.init();
        }
        islandManager.getWorld().getWorldBorder().setSize(Double.MAX_VALUE);
        if (configuration.netherIslands)
            islandManager.getNetherWorld().getWorldBorder().setSize(Double.MAX_VALUE);
    }

    public boolean loadConfigs() {
        configuration = persist.getFile(Config.class).exists() ? persist.load(Config.class) : new Config();
        missions = persist.getFile(Missions.class).exists() ? persist.load(Missions.class) : new Missions();
        messages = persist.getFile(Messages.class).exists() ? persist.load(Messages.class) : new Messages();
        upgrades = persist.getFile(Upgrades.class).exists() ? persist.load(Upgrades.class) : new Upgrades();
        boosters = persist.getFile(Boosters.class).exists() ? persist.load(Boosters.class) : new Boosters();
        inventories = persist.getFile(Inventories.class).exists() ? persist.load(Inventories.class) : new Inventories();
        schematics = persist.getFile(Schematics.class).exists() ? persist.load(Schematics.class) : new Schematics();
        commands = persist.getFile(Commands.class).exists() ? persist.load(Commands.class) : new Commands();
        blockValues = persist.getFile(BlockValues.class).exists() ? persist.load(BlockValues.class) : new BlockValues();
        shop = persist.getFile(Shop.class).exists() ? persist.load(Shop.class) : new Shop();
        border = persist.getFile(Border.class).exists() ? persist.load(Border.class) : new Border();


        commandManager = new CommandManager("island");
        commandManager.registerCommands();

        if (configuration == null || missions == null || messages == null || upgrades == null || boosters == null || inventories == null || schematics == null || commands == null || blockValues == null || shop == null) {
            return false;
        }

        if (shop.shop == null) shop = new Shop();

        if (getCommandManager() != null) {
            if (getCommandManager().commands.contains(IridiumSkyblock.getCommands().shopCommand)) {
                if (!configuration.islandShop)
                    getCommandManager().unRegisterCommand(IridiumSkyblock.getCommands().shopCommand);
            } else {
                if (configuration.islandShop)
                    getCommandManager().registerCommand(IridiumSkyblock.getCommands().shopCommand);
            }
        }

        getBlockValues().blockvalue.remove(XMaterial.AIR);

        oreUpgradeCache.clear();
        for (int i : getUpgrades().oresUpgrade.upgrades.keySet()) {
            ArrayList<String> items = new ArrayList<>();
            for (String item : getUpgrades().oresUpgrade.upgrades.get(i).ores) {
                if (item != null) {
                    int i1 = Integer.parseInt(item.split(":")[1]);
                    for (int a = 0; a <= i1; a++) {
                        items.add(item.split(":")[0]);
                    }
                } else {
                    getUpgrades().oresUpgrade.upgrades.get(i).ores.remove(null);
                }
            }
            oreUpgradeCache.put(i, items);
        }

        netherOreUpgradeCache.clear();
        for (int i : getUpgrades().oresUpgrade.upgrades.keySet()) {
            ArrayList<String> items = new ArrayList<>();
            for (String item : getUpgrades().oresUpgrade.upgrades.get(i).netherores) {
                if (item != null) {
                    int i1 = Integer.parseInt(item.split(":")[1]);
                    for (int a = 0; a <= i1; a++) {
                        items.add(item.split(":")[0]);
                    }
                } else {
                    getUpgrades().oresUpgrade.upgrades.get(i).netherores.remove(null);
                }
            }
            netherOreUpgradeCache.put(i, items);
        }

        if (getBoosters().flightBooster.time == 0) getBoosters().flightBooster.time = 3600;
        if (getBoosters().experianceBooster.time == 0) getBoosters().experianceBooster.time = 3600;
        if (getBoosters().farmingBooster.time == 0) getBoosters().farmingBooster.time = 3600;
        if (getBoosters().spawnerBooster.time == 0) getBoosters().spawnerBooster.time = 3600;

        if (getBoosters().spawnerBooster.crystalsCost == 0 && getBoosters().spawnerBooster.vaultCost == 0)
            getBoosters().spawnerBooster.crystalsCost = 15;
        if (getBoosters().farmingBooster.crystalsCost == 0 && getBoosters().farmingBooster.vaultCost == 0)
            getBoosters().farmingBooster.crystalsCost = 15;
        if (getBoosters().experianceBooster.crystalsCost == 0 && getBoosters().experianceBooster.vaultCost == 0)
            getBoosters().experianceBooster.crystalsCost = 15;
        if (getBoosters().flightBooster.crystalsCost == 0 && getBoosters().flightBooster.vaultCost == 0)
            getBoosters().flightBooster.crystalsCost = 15;

        if (configuration.blockvalue != null) {
            getBlockValues().blockvalue = new HashMap<>(configuration.blockvalue);
            configuration.blockvalue = null;
        }
        if (configuration.spawnervalue != null) {
            getBlockValues().spawnervalue = new HashMap<>(configuration.spawnervalue);
            configuration.spawnervalue = null;
        }
        int max = 0;
        for (Upgrades.IslandUpgrade size : getUpgrades().sizeUpgrade.upgrades.values()) {
            if (max < size.size) {
                max = size.size;
            }
        }
        if (configuration.distance <= max) {
            configuration.distance = max + 1;
        }
        if (islandManager != null) {
            for (@NotNull Island island : databaseManager.getIslands()) {
                if (island.getIslandMenuGUI() != null) island.getIslandMenuGUI().getInventory().clear();
                if (island.getSchematicSelectGUI() != null) island.getSchematicSelectGUI().getInventory().clear();
                if (island.getBankGUI() != null) island.getBankGUI().getInventory().clear();
                if (island.getBoosterGUI() != null) island.getBoosterGUI().getInventory().clear();
                if (island.getCoopGUI() != null) island.getCoopGUI().getInventory().clear();
                if (island.getMembersGUI() != null) island.getMembersGUI().getInventory().clear();
                if (island.getMissionsGUI() != null) island.getMissionsGUI().getInventory().clear();
                if (island.getPermissionsGUI() != null) island.getPermissionsGUI().getInventory().clear();
                if (island.getUpgradeGUI() != null) island.getUpgradeGUI().getInventory().clear();
                if (island.getWarpGUI() != null) island.getWarpGUI().getInventory().clear();
                if (island.getBorderColorGUI() != null) island.getBorderColorGUI().getInventory().clear();
                if (configuration.missionRestart == MissionRestart.Instantly) {
                    island.resetMissions();
                }
            }
        }
        try {
            for (Field field : Permissions.class.getDeclaredFields()) {
                if (!getMessages().permissions.containsKey(field.getName())) {
                    getMessages().permissions.put(field.getName(), field.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        configuration.biomes.sort(Comparator.comparing(XBiome::toString));
        return true;
    }

    public void saveData() {
        if (islandManager != null) persist.save(islandManager);
    }

    public void saveConfigs() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if (configuration != null) persist.save(configuration);
            if (missions != null) persist.save(missions);
            if (messages != null) persist.save(messages);
            if (upgrades != null) persist.save(upgrades);
            if (boosters != null) persist.save(boosters);
            if (inventories != null) persist.save(inventories);
            if (schematics != null) persist.save(schematics);
            if (commands != null) persist.save(commands);
            if (blockValues != null) persist.save(blockValues);
            if (shop != null) persist.save(shop);
            if (border != null) persist.save(border);
        });
    }
}