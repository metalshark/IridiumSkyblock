package com.iridium.iridiumskyblock.runnables;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.XMaterial;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class OreUpgradeRunnable implements Runnable {

    final private @NotNull Block toBlock;
    final private @NotNull List<String> oreUpgrades;
    final private @NotNull Material material;
    final private @NotNull Island island;
    final private @NotNull Location location;

    @Override
    public void run() {
        final @NotNull Random random = new Random();
        final @NotNull String oreUpgrade = oreUpgrades.get(random.nextInt(oreUpgrades.size()));

        final @NotNull XMaterial oreUpgradeXmaterial = XMaterial.valueOf(oreUpgrade);
        final @Nullable Material oreUpgradeMaterial = oreUpgradeXmaterial.parseMaterial(true);
        if (oreUpgradeMaterial == null) return;

        toBlock.setType(oreUpgradeMaterial);

        @NotNull final BlockState blockState = toBlock.getState();
        blockState.update(true);

        if (!Utils.isBlockValuable(toBlock)) return;

        island.addTempValue(location);
        if (island.isUpdating())
            island.addTempValue(location);
        island.calculateValue();
    }

}
