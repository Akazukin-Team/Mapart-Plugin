package org.akazukin.mapart.compat;

import org.akazukin.mapart.manager.MapartManager;
import org.bukkit.World;

public interface Compat {
    World createMapartWorld(MapartManager mgr);

    void test();
}
