package net.akazukin.mapart.compat;

import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.World;

public interface Compat {
    World createMapartWorld(MapartManager mgr);

    void test();
}
