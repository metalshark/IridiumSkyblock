package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Island.Warp;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Messages;
import com.iridium.iridiumskyblock.runnables.TeleportPlayerRunnable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerTalkListener implements Listener {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull Messages messages = IridiumSkyblock.getMessages();
    private static final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    private static final @NotNull BukkitScheduler scheduler = Bukkit.getScheduler();

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onPlayerTalk(@NotNull AsyncPlayerChatEvent event) {
        try {
            final @NotNull Player player = event.getPlayer();
            final @NotNull User user = User.getUser(player);

            // Handle warp password entry
            final @Nullable Warp warp = user.getWarp();
            if (warp != null) {
                if (warp.getPassword().equals(event.getMessage())) {
                    final @NotNull Runnable task = new TeleportPlayerRunnable(warp.getLocation(), player);
                    scheduler.runTask(plugin, task);
                    player.sendMessage(Utils.color(messages.teleporting
                        .replace("%prefix%", config.prefix)));
                } else {
                    player.sendMessage(Utils.color(messages.wrongPassword
                        .replace("%prefix%", config.prefix)));
                    user.setWarp(null);
                }
                event.setCancelled(true);
            }

            // Handle replacement of island variables in the chat format
            final @Nullable Island island = user.getIsland();
            final @NotNull String islandName;
            final @NotNull String islandRank;
            final @NotNull String islandValue;
            if (island == null)
                islandName = islandRank = islandValue = "";
            else {
                islandName = island.getName();
                islandRank = "" + Utils.getIslandRank(island);
                islandValue = "" + island.getValue();
            }
            @NotNull String format = event.getFormat();
            StringUtils.replace(format, config.chatNAMEPlaceholder, islandName);
            StringUtils.replace(format, config.chatRankPlaceholder, islandRank);
            StringUtils.replace(format, config.chatValuePlaceholder, islandValue);
            event.setFormat(Utils.color(format));

            // Handle island chat
            if (island == null || !user.isIslandChatEnabled()) return;
            @NotNull String message = messages.chatFormat
                .replace("%message%", event.getMessage());
            StringUtils.replace(message, config.chatLevelPlaceholder, String.format("%.2f", island.getValue()));
            StringUtils.replace(message, config.chatNAMEPlaceholder, islandName);
            StringUtils.replace(message, config.chatRankPlaceholder, islandRank);
            StringUtils.replace(message, config.chatValuePlaceholder, islandValue);
            for (final @NotNull UUID memberUuid : island.getMemberUuids()) {
                final @Nullable Player memberPlayer = Bukkit.getPlayer(memberUuid);
                if (memberPlayer == null) continue;

                final String memberMessage = message
                    .replace("%player%", player.getName());
                memberPlayer.sendMessage(Utils.color(memberMessage));
            }
            event.setCancelled(true);
        } catch (Exception e) {
            plugin.sendErrorMessage(e);
        }
    }

}
