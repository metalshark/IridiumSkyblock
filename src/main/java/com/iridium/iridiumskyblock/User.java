package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.Island.Warp;
import com.iridium.iridiumskyblock.db.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {

    @NotNull private static DatabaseManager databaseManager = IridiumSkyblock.getDatabaseManager();
    @NotNull private static IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @Getter private final @NotNull UUID uuid;
    @Getter @Setter private @Nullable String name;
    @Getter @Setter private int islandId;
    @Getter private @Nullable Role role;
    private final @NotNull Set<Integer> invites;
    @Getter @Setter private @Nullable Warp warp;
    @Getter private boolean bypassing;
    @Getter private boolean islandChatEnabled;
    @Getter @Setter private boolean flying;
    @Getter @Setter private boolean teleportingHome;
    @Getter @Setter private @Nullable Date lastCreate;

    public User(@NotNull OfflinePlayer offlinePlayer) {
        uuid = offlinePlayer.getUniqueId();
        name = offlinePlayer.getName();
        islandId = 0;
        role = null;
        invites = new HashSet<>();
        warp = null;
        islandChatEnabled = false;
        flying = false;
        teleportingHome = false;
        lastCreate = null;
        IridiumSkyblock.getDatabaseManager().addUser(this);
    }

    public User(@NotNull OfflinePlayer offlinePlayer, int islandId, @Nullable Role role, @NotNull Set<Integer> invites,
                @Nullable Warp warp, boolean bypassing, boolean islandChat, boolean flying, boolean teleportingHome,
                @Nullable Date lastCreate) {
        this.uuid = offlinePlayer.getUniqueId();
        this.name = offlinePlayer.getName();
        this.islandId = islandId;
        this.role = role;
        this.invites = invites;
        this.warp = warp;
        this.bypassing = bypassing;
        this.islandChatEnabled = islandChat;
        this.flying = flying;
        this.teleportingHome = teleportingHome;
        this.lastCreate = lastCreate;
    }

    public @Nullable Island getIsland() {
        return islandManager.getIslandById(islandId);
    }

    public @NotNull Role getRole() {
        if (role == null) {
            final @Nullable Island island = getIsland();
            if (island == null)
                role = Role.Visitor;
            else if (island.getOwner().equals(uuid))
                role = Role.Owner;
            else
                role = Role.Member;
            databaseManager.updateUser(this);
        }
        return role;
    }

    public void setRole(@NotNull Role role) {
        this.role = role;
        databaseManager.updateUser(this);
    }

    public void setBypassing(boolean bypassing) {
        this.bypassing = bypassing;
        databaseManager.updateUser(this);
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public static @NotNull User getUser(@NotNull String uuidString) {
        @NotNull final UUID uuid = UUID.fromString(uuidString);
        return getUser(uuid);
    }

    public static @NotNull User getUser(@NotNull OfflinePlayer offlinePlayer) {
        return getUser(offlinePlayer.getUniqueId());
    }

    public static @NotNull User getUser(@NotNull UUID uuid) {
        @Nullable User user = IridiumSkyblock.getDatabaseManager().getUserByUUID(uuid);
        if (user == null) {
            final @NotNull OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            user = new User(offlinePlayer);
        }
        return user;
    }

    public void clearInvites() {
        invites.clear();
    }

}
