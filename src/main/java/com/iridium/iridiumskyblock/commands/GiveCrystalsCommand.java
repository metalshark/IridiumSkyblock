package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GiveCrystalsCommand extends Command {

    public GiveCrystalsCommand() {
        super(Collections.singletonList("givecrystals"), "Give a player Crystals", "givecrystals", false);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is givecrystals <player> <amount>");
            return;
        }

        if (Bukkit.getPlayer(args[1]) != null) {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                Island island = User.getUser(player).getIsland();
                if (island != null) {
                    try {
                        int amount = Integer.parseInt(args[2]);
                        island.setCrystals(island.getCrystals() + amount);
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().giveCrystals.replace("%crystals%", args[2]).replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        if (player.getPlayer() != null)
                            player.getPlayer().sendMessage(Utils.color(IridiumSkyblock.getMessages().givenCrystals.replace("%crystals%", args[2]).replace("%player%", sender.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    } catch (Exception e) {
                        sender.sendMessage(args[2] + "is not a number");
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
