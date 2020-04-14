package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ValueCommand extends Command {

    public ValueCommand() {
        super(Collections.singletonList("value"), "Shows your island value", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        Island island;
        if (args.length == 2) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            island = User.getUser(player).getIsland();
        } else {
            island = user.getIsland();
        }
        if (island != null) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandValue.replace("%value%", island.getValue() + "").replace("%rank%", Utils.getIslandRank(island) + "").replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }

    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
