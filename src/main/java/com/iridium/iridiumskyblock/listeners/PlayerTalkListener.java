package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerTalkListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTalk(@NotNull AsyncPlayerChatEvent event) {
        try {
            @NotNull final Player player = event.getPlayer();
            @NotNull final User user = User.getUser(player);
            @NotNull final Config config = IridiumSkyblock.getConfiguration();
            @NotNull final Messages messages = IridiumSkyblock.getMessages();

            if (user.warp != null) {
                if (user.warp.getPassword().equals(event.getMessage())) {
                    Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), () -> player.teleport(user.warp.getLocation()));
                    player.sendMessage(Utils.color(messages.teleporting
                            .replace("%prefix%", config.prefix)));
                } else {
                    player.sendMessage(Utils.color(messages.wrongPassword
                            .replace("%prefix%", config.prefix)));
                    user.warp = null;
                }
                event.setCancelled(true);
            }

            @Nullable final Island island = user.getIsland();

            @NotNull String format = event.getFormat();
            if (format.contains(config.chatRankPlaceholder)) {
                if (island == null) {
                    format = format.replace(config.chatRankPlaceholder, "");
                } else {
                    format = format.replace(config.chatRankPlaceholder, Utils.getIslandRank(island) + "");
                }
            }
            if (format.contains(config.chatNAMEPlaceholder)) {
                if (island == null) {
                    format = format.replace(config.chatNAMEPlaceholder, "");
                } else {
                    format = format.replace(config.chatNAMEPlaceholder, island.getName());
                }
            }
            if (format.contains(config.chatValuePlaceholder)) {
                if (island == null) {
                    format = format.replace(config.chatValuePlaceholder, "");
                } else {
                    format = format.replace(config.chatValuePlaceholder, island.getValue() + "");
                }
            }

            if (island != null && user.islandChat) {
                for (@NotNull String member : island.getMembers()) {
                    @NotNull final User memberUser = User.getUser(member);
                    @Nullable final Player memberPlayer = Bukkit.getPlayer(memberUser.name);
                    if (memberPlayer == null) continue;

                    memberPlayer.sendMessage(Utils.color(messages.chatFormat)
                            .replace(config.chatValuePlaceholder, island.getValue() + "")
                            .replace(config.chatNAMEPlaceholder, island.getName())
                            .replace(config.chatLevelPlaceholder, String.format("%.2f", island.getValue()))
                            .replace(config.chatRankPlaceholder, Utils.getIslandRank(island) + "")
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage()));
                }
                event.setCancelled(true);
            }

            event.setFormat(Utils.color(format));
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
