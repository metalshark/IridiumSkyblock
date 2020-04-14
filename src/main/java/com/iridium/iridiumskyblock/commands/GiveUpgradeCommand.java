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

public class GiveUpgradeCommand extends Command {

    public GiveUpgradeCommand() {
        super(Collections.singletonList("giveupgrade"), "Give an Island an Upgrade", "giveupgrade", false);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length != 4 && args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is giveupgrade <player> <upgrade> <level>");
            return;
        }

        if (Bukkit.getPlayer(args[1]) != null) {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                Island island = User.getUser(player).getIsland();
                if (island != null) {
                    try {
                        if (args[2].equalsIgnoreCase("size")) {
                            int amount = args.length == 3 ? island.getSizeLevel() + 1 : Integer.parseInt(args[3]);
                            if (IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.containsKey(amount)) {
                                island.setSizeLevel(amount);
                            }
                        }
                        if (args[2].equalsIgnoreCase("member")) {
                            int amount = args.length == 3 ? island.getMemberLevel() + 1 : Integer.parseInt(args[3]);
                            if (IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.containsKey(amount)) {
                                island.setMemberLevel(amount);
                            }
                        }
                        if (args[2].equalsIgnoreCase("warp")) {
                            int amount = args.length == 3 ? island.getWarpLevel() + 1 : Integer.parseInt(args[3]);
                            if (IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.containsKey(amount)) {
                                island.setWarpLevel(amount);
                            }
                        }
                        if (args[2].equalsIgnoreCase("ores")) {
                            int amount = args.length == 3 ? island.getOreLevel() + 1 : Integer.parseInt(args[3]);
                            if (IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.containsKey(amount)) {
                                island.setOreLevel(amount);
                            }
                        }
                    } catch (NumberFormatException e) {
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
        if (args.length == 3) {
            return Arrays.asList("size", "member", "warp", "ores");
        }
        return null;
    }
}