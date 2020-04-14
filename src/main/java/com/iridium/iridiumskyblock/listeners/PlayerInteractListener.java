package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.User;
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

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        try {
            @NotNull final Player player = event.getPlayer();
            @NotNull final Location playerLocation = player.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();

            @Nullable final Block block = event.getClickedBlock();
            if (block == null) return;

            @NotNull final Location location = block.getLocation();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            // Enforce the interact permission
            @NotNull final User user = User.getUser(player);
            if (!island.getPermissions(user).interact) {
                event.setCancelled(true);
                return;
            }

            // Allow using an empty bucket as a lava bucket where the cobblestone generator has failed
            @NotNull final ItemStack itemInHand = player.getItemInHand();
            if (itemInHand.getType().equals(Material.BUCKET) && island.failedGenerators.remove(location)) {
                if (itemInHand.getAmount() == 1)
                    itemInHand.setType(Material.LAVA_BUCKET);
                else {
                    player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
                    player.getItemInHand().setAmount(itemInHand.getAmount() - 1);
                }
                block.setType(Material.AIR);
            }

            // Allow placing water in the nether
            @Nullable final ItemStack item = event.getItem();
            if (item != null
                    && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    && IridiumSkyblock.getConfiguration().allowWaterInNether) {
                @NotNull final World world = block.getWorld();
                if (!world.getEnvironment().equals(World.Environment.NETHER)) return;
                if (!item.getType().equals(Material.WATER_BUCKET)) return;

                event.setCancelled(true);

                @NotNull final BlockFace face = event.getBlockFace();
                block.getRelative(face).setType(Material.WATER);

                @NotNull final Block relative = block.getRelative(face);
                @NotNull final BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(relative, relative.getState(), block, item, player, false);
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
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        try {
            @NotNull final Player player = event.getPlayer();
            @NotNull final User user = User.getUser(player);
            @NotNull final Entity rightClicked = event.getRightClicked();
            @NotNull final Location location = rightClicked.getLocation();
            @NotNull final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            @Nullable final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            if (island.getPermissions(user).interact) return;

            event.setCancelled(true);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
