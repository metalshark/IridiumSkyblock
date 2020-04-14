package com.iridium.iridiumskyblock.runnables;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.db.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IslandValueRunnable implements Runnable {

    @NotNull private final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
    @NotNull private final DatabaseManager databaseManager = IridiumSkyblock.getDatabaseManager();
    @NotNull private final IslandManager islandManager = IridiumSkyblock.getIslandManager();
    @NotNull private Iterator<Integer> islandIdsIterator = databaseManager.getIslandIds().iterator();

    @Override
    public void run() {
        if (!plugin.updatingBlocks) {
            @NotNull Integer id;
            try {
                id = islandIdsIterator.next();
            } catch (NoSuchElementException e) {
                islandIdsIterator = databaseManager.getIslandIds().iterator();
                id = islandIdsIterator.next();
            }

            final Island island = islandManager.getIslandViaId(id);
            if (island == null) return;

            if (island.updating) return;

            plugin.updatingBlocks = true;
            island.initBlocks();
        }
    }

}
