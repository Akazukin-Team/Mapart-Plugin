package net.akazukin.mapart.compat.compats;

import net.akazukin.mapart.compat.Compat;
import net.akazukin.mapart.manager.MapartManager;
import net.akazukin.mapart.module.m1_17_to_1_20.Module_1_17_to_1_20;
import org.bukkit.World;

public class Compat_v1_20_R3 implements Compat {
    @Override
    public World createMapartWorld(final MapartManager mgr) {
        return Module_1_17_to_1_20.SINGLETON.createMapartWorld(mgr);
    }

    @Override
    public void test() {
    }
}
