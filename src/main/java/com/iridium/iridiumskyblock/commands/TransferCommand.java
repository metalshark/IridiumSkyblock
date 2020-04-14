package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.gui.ConfirmationGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class TransferCommand extends Command {

    public TransferCommand() {
        super(Collections.singletonList("transfer"), "Transfer island ownership", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is transfer player");
            return;
        }
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            Island island = user.getIsland();
            if (island.getOwner().equals(p.getUniqueId().toString())) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (player != null) {
                    if (User.getUser(player).getIsland() == island) {
                        p.openInventory(new ConfirmationGUI(user.getIsland(), () -> island.setOwner(player), IridiumSkyblock.getMessages().transferAction.replace("%player%", player.getName())).getInventory());
                    } else {
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().notInYourIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().mustBeIslandOwner.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
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
