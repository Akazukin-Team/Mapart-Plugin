package org.akazukin.mapart.compat;

import org.akazukin.mapart.manager.mapart.MapartWorldData;
import org.bukkit.World;

public interface Compat {
    World createMapartWorld(MapartWorldData mapartWorldData);

    void test();
}
