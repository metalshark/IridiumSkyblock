package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PermissionsCommand extends Command {

    public PermissionsCommand() {
        super(Collections.singletonList("permissions"), "Edit Island Permissions", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        Player player = (Player) sender;
        User user = User.getUser(player);
        Island island = user.getIsland();
        if (island != null) {
            player.openInventory(island.getPermissionsGUI().getInventory());
        }
    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
