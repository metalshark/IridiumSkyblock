package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class HomeCommand extends Command {

    public HomeCommand() {
        super(Collections.singletonList("home"),"Teleport to your island home", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            user.getIsland().teleportHome(p);
        } else {
            if(IridiumSkyblock.getConfiguration().createIslandonHome){
                IridiumSkyblock.getIslandManager().createIsland(p);
            }else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
