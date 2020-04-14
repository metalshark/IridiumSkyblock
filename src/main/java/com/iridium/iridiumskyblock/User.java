package com.iridium.iridiumskyblock;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {

    public String player;
    public String name;
    public int islandID;
    public Role role;
    public Set<Integer> invites;
    public Island.Warp warp;
    public boolean bypassing;
    public boolean islandChat;
    public boolean flying;
    public transient boolean teleportingHome;
    public Date lastCreate;

    public User(OfflinePlayer p) {
        invites = new HashSet<>();
        this.player = p.getUniqueId().toString();
        this.name = p.getName();
        this.islandID = 0;
        bypassing = false;
        islandChat = false;
        flying = false;
        IridiumSkyblock.getDatabaseManager().addUser(this);
    }

    @Nullable public Island getIsland() {
        return IridiumSkyblock.getIslandManager().getIslandViaId(islandID);
    }

    @NotNull public Role getRole() {
        if (role == null) {
            if (getIsland() != null) {
                if (getIsland().getOwner().equals(player)) {
                    role = Role.Owner;
                } else {
                    role = Role.Member;
                }
            } else {
                role = Role.Visitor;
            }
        }
        return role;
    }

    public static @Nullable User getUser(@NotNull String uuidString) {
        @NotNull final UUID uuid = UUID.fromString(uuidString);
        return getUser(uuid);
    }

    public static @Nullable User getUser(@NotNull OfflinePlayer offlinePlayer) {
        return getUser(offlinePlayer.getUniqueId());
    }

    public static @Nullable User getUser(@NotNull UUID uuid) {
        return IridiumSkyblock.getDatabaseManager().getUserByUUID(uuid);
    }

}
