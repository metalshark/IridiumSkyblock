package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BoosterGUI extends GUI implements Listener {

    public BoosterGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().boosterGUISize, IridiumSkyblock.getInventories().boosterGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            if (IridiumSkyblock.getBoosters().spawnerBooster.enabled)
                setItem(IridiumSkyblock.getBoosters().spawnerBooster.slot, Utils.makeItem(IridiumSkyblock.getInventories().spawner, getIsland()));
            if (IridiumSkyblock.getBoosters().farmingBooster.enabled)
                setItem(IridiumSkyblock.getBoosters().farmingBooster.slot, Utils.makeItem(IridiumSkyblock.getInventories().farming, getIsland()));
            if (IridiumSkyblock.getBoosters().experianceBooster.enabled)
                setItem(IridiumSkyblock.getBoosters().experianceBooster.slot, Utils.makeItem(IridiumSkyblock.getInventories().exp, getIsland()));
            if (IridiumSkyblock.getBoosters().flightBooster.enabled)
                setItem(IridiumSkyblock.getBoosters().flightBooster.slot, Utils.makeItem(IridiumSkyblock.getInventories().flight, getIsland()));
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            Player p = (Player) e.getWhoClicked();
            Island island = getIsland();
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == IridiumSkyblock.getBoosters().spawnerBooster.slot && IridiumSkyblock.getBoosters().spawnerBooster.enabled) {
                if (getIsland().getSpawnerBooster() == 0) {
                    if (Utils.canBuy(p, IridiumSkyblock.getBoosters().spawnerBooster.vaultCost, IridiumSkyblock.getBoosters().spawnerBooster.crystalsCost)) {
                        getIsland().setSpawnerBooster(IridiumSkyblock.getBoosters().spawnerBooster.time);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().spawnerBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getBoosters().farmingBooster.slot && IridiumSkyblock.getBoosters().farmingBooster.enabled) {
                if (getIsland().getFarmingBooster() == 0) {
                    if (Utils.canBuy(p, IridiumSkyblock.getBoosters().farmingBooster.vaultCost, IridiumSkyblock.getBoosters().farmingBooster.crystalsCost)) {
                        getIsland().setFarmingBooster(IridiumSkyblock.getBoosters().farmingBooster.time);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().farmingBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getBoosters().experianceBooster.slot && IridiumSkyblock.getBoosters().experianceBooster.enabled) {
                if (getIsland().getExpBooster() == 0) {
                    if (Utils.canBuy(p, IridiumSkyblock.getBoosters().experianceBooster.vaultCost, IridiumSkyblock.getBoosters().experianceBooster.crystalsCost)) {
                        getIsland().setExpBooster(IridiumSkyblock.getBoosters().experianceBooster.time);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().expBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getBoosters().flightBooster.slot && IridiumSkyblock.getBoosters().flightBooster.enabled) {
                if (getIsland().getFlightBooster() == 0) {
                    if (Utils.canBuy(p, IridiumSkyblock.getBoosters().flightBooster.vaultCost, IridiumSkyblock.getBoosters().flightBooster.crystalsCost)) {
                        getIsland().setFlightBooster(IridiumSkyblock.getBoosters().flightBooster.time);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().flightBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }
    }
}