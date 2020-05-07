package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AboutCommand {

    @Command(commandNames = { "about", "version" },
        helpMessage = "Displays plugin info",
        permission = "iridiumskyblock.about"
    )
    public static void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        sender.sendMessage(ChatColor.DARK_GRAY + "Plugin Name: "
            + ChatColor.GRAY + IridiumSkyblock.getPlugin().getName());
        sender.sendMessage(ChatColor.DARK_GRAY + "Plugin Version: "
            + ChatColor.GRAY + IridiumSkyblock.getPlugin().getDescription().getVersion());
        sender.sendMessage(ChatColor.DARK_GRAY + "Coded by IridiumLLC");
    }

}
