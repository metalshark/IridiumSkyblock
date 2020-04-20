package com.iridium.iridiumskyblock.runnables;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.MissionRestart;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class DailyRunnable implements Runnable {

    private static final @NotNull Config config = IridiumSkyblock.getConfiguration();
    private static final @NotNull IslandManager islandManager = IridiumSkyblock.getIslandManager();
    private static final @NotNull Messages messages = IridiumSkyblock.getMessages();

    @Override
    public void run() {
        final @NotNull LocalDateTime localDateTime = LocalDateTime.now();
        final boolean resetMissions = config.missionRestart.equals(MissionRestart.Daily) ||
                (config.missionRestart.equals(MissionRestart.Weekly)
                        && localDateTime.getDayOfWeek().equals(DayOfWeek.MONDAY));

        for (final @NotNull Island island : islandManager.getIslands()) {
            if (resetMissions) island.resetMissions();

            final double money = island.getMoney();
            final double moneyInterest = Math.floor(money * (config.dailyMoneyInterest / 100));
            island.setMoney(money + moneyInterest);

            final int crystals = island.getCrystals();
            final int crystalsInterest = (int) Math.floor(crystals * (config.dailyCrystalsInterest / 100));
            island.setCrystals(crystals + crystalsInterest);

            final int experience = island.getExperience();
            final int experienceInterest = (int) Math.floor(experience * (config.dailyExpInterest / 100));
            island.setExperience(experience + experienceInterest);

            if (moneyInterest == 0 && crystalsInterest == 0 && experienceInterest == 0) continue;

            for (final @NotNull User member : island.getMembers()) {
                final @Nullable Player memberPlayer = member.getPlayer();
                if (memberPlayer == null) continue;

                memberPlayer.sendMessage(Utils.color(messages.islandInterest
                        .replace("%exp%", Integer.toString(experienceInterest))
                        .replace("%crystals%", Integer.toString(crystalsInterest))
                        .replace("%money%", Double.toString(moneyInterest))
                        .replace("%prefix%", config.prefix)));
            }
        }
    }

}
