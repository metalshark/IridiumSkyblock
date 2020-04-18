package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Inventories;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemCraftListener implements Listener {

    private static final @NotNull Inventories inventories = IridiumSkyblock.getInventories();
    private static final @NotNull ItemStack crystalItemStack = Utils.makeItemHidden(inventories.crystal);

    @EventHandler
    @SuppressWarnings("unused")
    public void onItemCraft(@NotNull PrepareItemCraftEvent event) {
        try {
            final @NotNull CraftingInventory inventory = event.getInventory();
            if (inventory.getResult() == null) return;

            for (final @NotNull ItemStack itemStack : inventory.getContents()) {
                if (!crystalItemStack.isSimilar(itemStack)) continue;
                inventory.setResult(null);
                return;
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
