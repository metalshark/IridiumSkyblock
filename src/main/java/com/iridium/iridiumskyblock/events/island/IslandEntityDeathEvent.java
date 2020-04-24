package com.iridium.iridiumskyblock.events.island;

import com.iridium.iridiumskyblock.Island;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IslandEntityDeathEvent extends IslandEntityEvent {

    @Getter private final @NotNull List<ItemStack> drops;
    @Getter private final int dropExp;

    public IslandEntityDeathEvent(final @NotNull Island island,
                                  final @NotNull LivingEntity deceased,
                                  final @NotNull List<ItemStack> drops,
                                  final int dropExp) {
        super(island, deceased);
        this.drops = drops;
        this.dropExp = dropExp;
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return (LivingEntity) super.getEntity();
    }

}
