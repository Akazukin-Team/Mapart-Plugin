package org.akazukin.mapart.compat.compats;

import org.akazukin.mapart.compat.Compat;
import org.akazukin.mapart.manager.mapart.MapartWorldData;
import org.akazukin.mapart.module.m1_17_to_1_20.Module_1_17_to_1_20;
import org.bukkit.World;

public class Compat_v1_21_R4 implements Compat {
    @Override
    public World createMapartWorld(final MapartWorldData mapartWorldData) {
        return Module_1_17_to_1_20.SINGLETON.createMapartWorld(mapartWorldData);
    }

    @Override
    public void test() {
    }
}
