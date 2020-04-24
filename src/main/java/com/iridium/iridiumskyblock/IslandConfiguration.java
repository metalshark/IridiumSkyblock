package com.iridium.iridiumskyblock;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Color;

@Accessors(chain = true)
public class IslandConfiguration {

    @Getter @Setter private Color borderColor;
    @Getter @Setter private boolean explosionsEnabled;
    @Getter @Setter private boolean leavesDecayEnabled;
    @Getter @Setter private boolean pvpEnabled;

}
