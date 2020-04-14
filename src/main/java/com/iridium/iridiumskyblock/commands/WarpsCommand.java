package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class WarpsCommand extends Command {

    public WarpsCommand() {
        super(Collections.singletonList("warps"), "opens the Warp GUI", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        Island island;
        if (args.length == 2) {
            island = User.getUser(Bukkit.getOfflinePlayer(args[1])).getIsland();
        } else {
            island = user.getIsland();
        }
        if (island != null) {
            if (island.getPermissions(user.islandID == island.getId() ? user.role : Role.Visitor).useWarps || user.bypassing) {
                p.openInventory(island.getWarpGUI().getInventory());
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            if (user.getIsland() != null) {
                p.openInventory(user.getIsland().getWarpGUI().getInventory());
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
