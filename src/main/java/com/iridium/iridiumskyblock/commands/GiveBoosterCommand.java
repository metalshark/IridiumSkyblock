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

public class GiveBoosterCommand extends Command {

    public GiveBoosterCommand() {
        super(Collections.singletonList("givebooster"), "Give an Island a Booster", "givebooster", false);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length != 4 && args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is givebooster <player> <booster> <amount>");
            return;
        }

        if (Bukkit.getPlayer(args[1]) != null) {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                Island island = User.getUser(player).getIsland();
                if (island != null) {
                    try {
                        int amount = args.length == 3 ? 3600 : Integer.parseInt(args[3]);
                        if (args[2].equalsIgnoreCase("exp")) {
                            island.setExpBooster(amount);
                        }
                        if (args[2].equalsIgnoreCase("farming")) {
                            island.setFarmingBooster(amount);
                        }
                        if (args[2].equalsIgnoreCase("flight")) {
                            island.setFlightBooster(amount);
                        }
                        if (args[2].equalsIgnoreCase("spawner")) {
                            island.setSpawnerBooster(amount);
                        }
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
        if (args.length == 3) {
            return Arrays.asList("exp", "farming", "flight", "spawner");
        }
        return null;
    }
}
