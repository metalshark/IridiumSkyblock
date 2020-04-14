package com.iridium.iridiumskyblock.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public abstract class Command {
    @Getter @NotNull private final List<String> aliases;
    @Getter @NotNull private final String description;
    @Getter @NotNull private final String permission;
    @Getter private final boolean player;
    @Getter private final boolean enabled = true;

    public abstract void execute(@NotNull CommandSender sender, @NotNull String[] args);

    public abstract List<String> TabComplete(@NotNull CommandSender cs, @NotNull org.bukkit.command.Command cmd, @NotNull String s, @NotNull String[] args);
}
