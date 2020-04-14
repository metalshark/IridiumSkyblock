package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SetWarpCommand extends Command {

    public SetWarpCommand() {
        super(Arrays.asList("setwarp", "addwarp"), "Set a new island warp", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length == 2 || args.length == 3) {
            User user = User.getUser(p);
            if (user.getIsland() != null) {
                String password = args.length == 3 ? args[2] : "";
                if(Utils.isSafe(p.getLocation(), user.getIsland())){
                    user.getIsland().addWarp(p, p.getLocation(), args[1], password);
                }else{
                    p.sendMessage(Utils.color(IridiumSkyblock.getMessages().isNotSafe.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is setwarp <name> (password)");
        }
    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
