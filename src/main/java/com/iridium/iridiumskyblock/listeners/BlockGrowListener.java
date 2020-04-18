package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.XBlock;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.material.Crops;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGrowListener implements Listener {

    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onBlockGrow(@NotNull BlockGrowEvent event) {
        try {
            final @NotNull Block block = event.getBlock();
            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            if (island.getFarmingBooster() == 0) return;

            final @NotNull Material material = block.getType();
            if (!XBlock.isCrops(material)) return;

            event.setCancelled(true);

            final @NotNull Crops crops = new Crops(CropState.RIPE);
            final @NotNull BlockState blockState = block.getState();
            blockState.setData(crops);
            blockState.update();
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
