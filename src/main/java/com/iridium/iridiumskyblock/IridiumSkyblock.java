package com.iridium.iridiumskyblock;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.comphenix.protocol.AsynchronousManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.async.AsyncListenerHandler;
import com.comphenix.protocol.events.PacketListener;
import com.iridium.iridiumskyblock.listeners.WorldBorderListener;
import com.iridium.iridiumskyblock.managers.*;
import com.iridium.iridiumskyblock.plugins.MultiverseCoreManager;
import com.iridium.iridiumskyblock.plugins.WorldEditManager;
import com.songoda.ultimatestacker.UltimateStacker;
import com.vk2gpz.mergedspawner.api.MergedSpawnerAPI;
import lombok.Getter;
import ninja.amp.ampmenus.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

public class IridiumSkyblock extends JavaPlugin {

    @Getter private @NotNull BoostManager boostManager;
    @Getter private @NotNull DatabaseManager databaseManager;
    @Getter private @NotNull GuiManager guiManager;
    @Getter private @NotNull IslandManager islandManager;
    @Getter private @NotNull MissionManager missionManager;
    @Getter private @NotNull UpgradeManager upgradeManager;
    @Getter private @NotNull UserManager userManager;
    @Getter private @NotNull WorldManager worldManager;

    @Getter private ProtocolManager protocolManager;
    private AsynchronousManager asynchronousManager;
    private PacketListener worldBorderListener;
    private AsyncListenerHandler worldBorderListenerHandler;

    @Getter private @Nullable MultiverseCoreManager mulitverseCoreManager;
    @Getter private @Nullable WorldEditManager worldEditManager;

    @Getter private @Nullable Function<Block, Integer> barrelAmount;
    @Getter private @Nullable Function<LivingEntity, Integer> entityAmount;
    @Getter private @Nullable Function<Item, Integer> itemAmount;
    @Getter private @Nullable Function<CreatureSpawner, Integer> spawnerAmount;

    @Getter private @NotNull ChatColor chatColor;
    @Getter private @NotNull String prefix;

    private @NotNull Map<Locale, ResourceBundle> messages;

    @Override
    public void onEnable() {
        super.onEnable();

        chatColor = ChatColor.LIGHT_PURPLE;
        prefix = "";
        messages = new HashMap<>();

        MenuListener.getInstance().register(this);

        databaseManager = new DatabaseManager(this);

        worldManager = new WorldManager(this);
        islandManager = new IslandManager(this);
        userManager = new UserManager(this);

        missionManager = new MissionManager(this);
        upgradeManager = new UpgradeManager(this);
        boostManager = new BoostManager(this);

        guiManager = new GuiManager(this);

        protocolManager = ProtocolLibrary.getProtocolManager();
        asynchronousManager = protocolManager.getAsynchronousManager();

        worldBorderListener = new WorldBorderListener(this);
        worldBorderListenerHandler = asynchronousManager.registerAsyncHandler(worldBorderListener);
        worldBorderListenerHandler.syncStart();

        @Nullable Plugin plugin;
        plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        if (plugin != null && plugin.isEnabled())
            mulitverseCoreManager = new MultiverseCoreManager(plugin);

        plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (plugin != null && plugin.isEnabled())
            worldEditManager = new WorldEditManager(plugin);

        plugin = Bukkit.getPluginManager().getPlugin("MergedSpawnerAPI");
        if (plugin != null && plugin.isEnabled())
            spawnerAmount = (spawner) -> MergedSpawnerAPI.getInstance()
                .getCountFor(spawner.getBlock());

        plugin = Bukkit.getPluginManager().getPlugin("UltimateStacker");
        if (plugin != null && plugin.isEnabled())
            spawnerAmount = (spawner) -> UltimateStacker.getInstance()
                .getSpawnerStackManager().getSpawner(spawner.getBlock()).getAmount();

        plugin = Bukkit.getPluginManager().getPlugin("WildStacker");
        if (plugin != null && plugin.isEnabled()) {
            barrelAmount = WildStackerAPI::getBarrelAmount;
            entityAmount = WildStackerAPI::getEntityAmount;
            itemAmount = WildStackerAPI::getItemAmount;
            spawnerAmount = WildStackerAPI::getSpawnersAmount;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        asynchronousManager.unregisterAsyncHandler(worldBorderListenerHandler);
        HandlerList.unregisterAll(this);
    }

    public static IridiumSkyblock getPlugin() {
        return getPlugin(IridiumSkyblock.class);
    }

    public @NotNull ResourceBundle getMessages(final @Nullable Locale locale) {
        return messages.computeIfAbsent(
            locale,
            key -> ResourceBundle.getBundle("MessagesBundle", key)
        );
    }

    public @NotNull Locale getPlayerLocale(final @Nullable Player player) {
        if (player == null)
            return Locale.getDefault();

        final @NotNull String[] localeParts = player.getLocale().split("_");
        if (localeParts.length == 1) {
            final @NotNull String language = localeParts[0];
            return new Locale(language);
        } else if (localeParts.length == 2) {
            final @NotNull String language = localeParts[0];
            final @NotNull String country = localeParts[1];
            return new Locale(language, country);
        } else if (localeParts.length == 3) {
            final @NotNull String language = localeParts[0];
            final @NotNull String country = localeParts[1];
            final @NotNull String variant = localeParts[2];
            return new Locale(language, country, variant);
        } else
            return Locale.getDefault();
    }

    public @NotNull ResourceBundle getPlayerMessages(final @Nullable Player player) {
        return getMessages(getPlayerLocale(player));
    }

}
