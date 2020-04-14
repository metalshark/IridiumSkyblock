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

    @NotNull final private Block toBlock;
    @NotNull final private List<String> oreUpgrades;
    @NotNull final private Material material;
    @NotNull final private Island island;
    @NotNull final private Location location;

    @Override
    public void run() {
        @NotNull final Random random = new Random();
        @NotNull final String oreUpgrade = oreUpgrades.get(random.nextInt(oreUpgrades.size()));

        @NotNull final XMaterial oreUpgradeXmaterial = XMaterial.valueOf(oreUpgrade);
        @Nullable final Material oreUpgradeMaterial = oreUpgradeXmaterial.parseMaterial(true);
        if (oreUpgradeMaterial == null) return;

        toBlock.setType(oreUpgradeMaterial);

        @NotNull final BlockState blockState = toBlock.getState();
        blockState.update(true);

        if (!Utils.isBlockValuable(toBlock)) return;
        @NotNull final XMaterial xmaterial = XMaterial.matchXMaterial(material);
        island.valuableBlocks.compute(xmaterial.name(), (name, original) -> {
            if (original == null) return 1;
            return original + 1;
        });
        if (island.updating)
            island.tempValues.add(location);
        island.calculateIslandValue();
    }

}
