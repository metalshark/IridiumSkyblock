package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UpdateCommand extends Command {

    public UpdateCommand() {
        super(Collections.singletonList("update"), "Updates an islands levels", "update", false);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2 && args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is update <player/all> <blocksPerTick>");
            return;
        }
        int blocksPerTick = 1000;
        if (args.length == 3) {
            try {
                blocksPerTick = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(args[2] + "is not a number");
            }
        }

        if (args[1].equalsIgnoreCase("all")) {
            boolean out = true;
            for (@NotNull final Island island : IridiumSkyblock.getDatabaseManager().getIslands()) {
                island.valuableBlocks.clear();
                if (!island.updating) {
                    island.forceInitBlocks(out ? sender : null, blocksPerTick, "Everyone");
                    out = false;
                } else {
                    //change blockspertick to blocks temp
                    IridiumSkyblock.blockspertick = IridiumSkyblock.getConfiguration().blocksPerTick;
                    IridiumSkyblock.getConfiguration().blocksPerTick = blocksPerTick;
                }
            }
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

        if (player != null) {
            User u = User.getUser(player);
            if (u.getIsland() != null) {
                if (u.getIsland().updating) {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().alreadyRecalculating.replace("%player%", User.getUser(u.getIsland().getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    return;
                }
                u.getIsland().forceInitBlocks(sender, blocksPerTick, User.getUser(u.getIsland().getOwner()).name);
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
