package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ShopCommand extends Command {

    public ShopCommand() {
        super(Arrays.asList("shop"), "Access the Skyblock Shop", "", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        Player p = (Player) sender;
        p.openInventory(IridiumSkyblock.getShopGUI().getInventory());
    }

    @Override
    public List<String> TabComplete(@NotNull CommandSender cs, org.bukkit.command.@NotNull Command cmd, @NotNull String s, String[] args) {
        return null;
    }
}
