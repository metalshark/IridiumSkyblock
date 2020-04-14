package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CoopCommand extends Command {

    public CoopCommand() {
        super(Collections.singletonList("coop"), "Coops you to an island", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (args.length != 2) {
                p.openInventory(user.getIsland().getCoopGUI().getInventory());
                return;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (player != null) {
                User u = User.getUser(player);
                if (!user.getIsland().equals(u.getIsland()) && u.getIsland() != null) {
                    if (user.bypassing || user.getIsland().getPermissions(user.getRole()).coop) {
                        if (user.getIsland().coopInvites.contains(u.islandID)) {
                            user.getIsland().coopInvites.remove(u.islandID);
                            user.getIsland().addCoop(u.getIsland());
                        } else {
                            u.getIsland().inviteCoop(user.getIsland());
                            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopInviteSent.replace("%player%", User.getUser(u.getIsland().getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    } else {
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
