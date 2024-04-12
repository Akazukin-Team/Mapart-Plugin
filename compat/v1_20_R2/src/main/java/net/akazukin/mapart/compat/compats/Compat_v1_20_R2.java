package net.akazukin.mapart.compat.compats;

import net.akazukin.mapart.compat.Compat;
import net.akazukin.mapart.module.m1_17_to_1_20.Module_1_17_to_1_20;
import org.bukkit.World;

public class Compat_v1_20_R2 implements Compat {
    @Override
    public World createMapartWorld() {
        return Module_1_17_to_1_20.SINGLETON.createMapartWorld();
    }

    @Override
    public void test() {
    }
}
