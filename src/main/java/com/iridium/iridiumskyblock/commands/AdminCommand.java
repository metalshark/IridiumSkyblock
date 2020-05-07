package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class AdminCommand {

    @Command(commandNames = { "admin" },
        helpMessage = "Control a players Island",
        permission = "iridiumskyblock.admin"
    )
    public static void execute(final @NotNull Player player, @Nullable Island island) {
        final @NotNull IridiumSkyblock plugin = IridiumSkyblock.getPlugin();
        if (island == null)
            island = plugin.getDatabaseManager().getIslandByLocation(player.getLocation());

        if (island == null) {
            player.sendMessage(plugin.getChatColor() + plugin.getPrefix() + plugin.getPlayerMessages(player).getString("noIslandWithThatName"));
            return;
        }

        plugin.getGuiManager().openIslandMenu(player, island);
    }

}
