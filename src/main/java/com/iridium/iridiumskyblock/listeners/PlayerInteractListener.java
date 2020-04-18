package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerInteractListener implements Listener {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        try {
            final @NotNull Player player = event.getPlayer();
            final @NotNull Location playerLocation = player.getLocation();

            final @Nullable Block block = event.getClickedBlock();
            if (block == null) return;

            final @NotNull Location location = block.getLocation();
            final @Nullable Island island = islandManager.getIslandByLocation(location);
            if (island == null) return;

            // Enforce the interact permission
            final @NotNull User user = User.getUser(player);
            if (!island.getPermissionsByUser(user).interact) {
                event.setCancelled(true);
                return;
            }

            // Allow using an empty bucket as a lava bucket where the cobblestone generator has failed
            final @NotNull ItemStack itemInHand = player.getItemInHand();
            if (itemInHand.getType().equals(Material.BUCKET) && island.isFailedGenerator(location)) {
                if (itemInHand.getAmount() == 1)
                    itemInHand.setType(Material.LAVA_BUCKET);
                else {
                    player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
                    player.getItemInHand().setAmount(itemInHand.getAmount() - 1);
                }
                block.setType(Material.AIR);
                island.removeFailedGenerator(location);
            }

            // Allow placing water in the nether
            final @Nullable ItemStack item = event.getItem();
            if (item != null
                    && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    && config.allowWaterInNether) {
                final @NotNull World world = block.getWorld();
                if (!world.getEnvironment().equals(World.Environment.NETHER)) return;
                if (!item.getType().equals(Material.WATER_BUCKET)) return;

                event.setCancelled(true);

                final @NotNull BlockFace face = event.getBlockFace();
                block.getRelative(face).setType(Material.WATER);

                final @NotNull Block relative = block.getRelative(face);
                final @NotNull BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(relative, relative.getState(), block, item, player, false);
                if (blockPlaceEvent.isCancelled()) {
                    block.getRelative(face).setType(Material.AIR);
                } else if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    if (item.getAmount() == 1) {
                        item.setType(Material.BUCKET);
                    } else {
                        item.setAmount(item.getAmount() - 1);
                        player.getInventory().addItem(new ItemStack(Material.BUCKET));
                    }
                }
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        try {
            final @NotNull Entity rightClicked = event.getRightClicked();
            final @Nullable Island island = islandManager.getIslandByEntity(rightClicked);
            if (island == null) return;

            final @NotNull Player player = event.getPlayer();
            final @NotNull User user = User.getUser(player);
            if (island.getPermissionsByUser(user).interact) return;

            event.setCancelled(true);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
