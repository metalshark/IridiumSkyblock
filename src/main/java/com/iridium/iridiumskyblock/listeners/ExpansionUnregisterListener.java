package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import me.clip.placeholderapi.events.ExpansionUnregisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ExpansionUnregisterListener implements Listener {

    @EventHandler
    public void onExpansionUnregister(@NotNull ExpansionUnregisterEvent event) {
        try {
            if (!event.getExpansion().getIdentifier().equals("iridiumskyblock")) return;

            @NotNull final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, plugin::setupClipsPlaceholderAPI);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

}
