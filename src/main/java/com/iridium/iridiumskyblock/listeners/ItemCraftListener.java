package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemCraftListener implements Listener {

    @EventHandler
    public void onItemCraft(@NotNull PrepareItemCraftEvent event) {
        try {
            @NotNull final CraftingInventory inventory = event.getInventory();
            if (inventory.getResult() == null) return;

            for (@NotNull ItemStack itemStack : inventory.getContents()) {
                if (!Utils.makeItemHidden(IridiumSkyblock.getInventories().crystal).isSimilar(itemStack)) continue;
                inventory.setResult(null);
                return;
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
