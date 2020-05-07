package com.iridium.iridiumskyblock.listeners;

import com.comphenix.packetwrapper.WrapperPlayServerWorldBorder;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.EnumWrappers.WorldBorderAction;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Island.BorderColor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class WorldBorderListener implements PacketListener {

    private final @NotNull IridiumSkyblock plugin;

    @Override
    public void onPacketSending(PacketEvent packetEvent) {
        final @NotNull Player player = packetEvent.getPlayer();
        final @Nullable Island island = plugin.getDatabaseManager().getIslandByLocation(player.getLocation());
        if (island == null) return;

        final @NotNull PacketContainer packet = packetEvent.getPacket();
        final @NotNull WrapperPlayServerWorldBorder wrappedPacket = new WrapperPlayServerWorldBorder(packet);
        wrappedPacket.setAction(WorldBorderAction.INITIALIZE);

        final @NotNull BoundingBox boundingBox = island.getBoundingBox();
        wrappedPacket.setCenterX(boundingBox.getCenterX());
        wrappedPacket.setCenterZ(boundingBox.getCenterZ());

        double radius = boundingBox.getWidthX(); // Islands are square, so either side works
        double oldRadius = radius;

        final @NotNull BorderColor borderColor = island.getConfiguration().getBorderColor();
        if (borderColor == BorderColor.GREEN) {
            radius += Double.MIN_VALUE; // Green when the border is expanding
        } else if (borderColor == BorderColor.RED) {
            oldRadius += Double.MIN_VALUE; // Red when the border is contracting
        } else if (borderColor == BorderColor.OFF) {
            radius = oldRadius = Double.MAX_VALUE;
        }
        wrappedPacket.setRadius(radius);
        wrappedPacket.setOldRadius(oldRadius);
        wrappedPacket.setSpeed((radius != oldRadius) ? Long.MAX_VALUE : 0);
    }

    @Override
    public void onPacketReceiving(PacketEvent packetEvent) { }

    @Override
    public @NotNull ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist
            .newBuilder()
            .types(PacketType.Play.Server.WORLD_BORDER)
            .priority(ListenerPriority.NORMAL)
            .gamePhase(GamePhase.PLAYING)
            .monitor()
            .build();
    }

    @Override
    public @NotNull ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist
            .newBuilder()
            .build();
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

}
