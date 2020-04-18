package com.iridium.iridiumskyblock.runnables;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class UpdateBlockStateRunnable implements Runnable {

    private final @NotNull BlockState blockState;

    @Override
    public void run() {
        blockState.update(true, true);
    }

}
