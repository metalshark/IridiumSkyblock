package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class KickCommand extends Command {

    public KickCommand() {
        super(Collections.singletonList("kick"), "Kick a player from your island", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is kick <player>");
            return;
        }
        Player p = (Player) sender;
        User user = User.getUser(p); // User kicking the player
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (player != null) {
            User u = User.getUser(player); // Player we want to kick
            if (user.getIsland() != null) {
                if (user.getIsland().equals(u.getIsland())) {
                    if (u.role.equals(Role.Owner)) {
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().cantKickOwner.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    } else {
                        if (user.bypassing || user.getIsland().getPermissions(u.getRole()).kickMembers) {
                            user.getIsland().removeUser(u);
                            if (player.getPlayer() != null)
                                player.getPlayer().sendMessage(Utils.color(IridiumSkyblock.getMessages().youHaveBeenKicked.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        } else {
                            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().notInYourIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
