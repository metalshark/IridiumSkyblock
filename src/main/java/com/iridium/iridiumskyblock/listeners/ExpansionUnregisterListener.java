package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import me.clip.placeholderapi.events.ExpansionUnregisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class ExpansionUnregisterListener implements Listener {

    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull BukkitScheduler scheduler = Bukkit.getScheduler();
    private static final @NotNull Runnable task = plugin::setupClipsPlaceholderAPI;

    @EventHandler
    @SuppressWarnings("unused")
    public void onExpansionUnregister(@NotNull ExpansionUnregisterEvent event) {
        try {
            if (!event.getExpansion().getIdentifier().equals("iridiumskyblock")) return;

            scheduler.scheduleSyncDelayedTask(plugin, task);
        } catch (Exception ex) {
            plugin.sendErrorMessage(ex);
        }
    }

}
