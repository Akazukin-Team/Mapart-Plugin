package net.akazukin.mapart.module.m1_8_to_1_12;

import org.bukkit.World;

public class Module_1_8_to_1_12 {
    public static final Module_1_8_to_1_12 SINGLETON = new Module_1_8_to_1_12();

    public World setGameRule(final World world) {
        world.setGameRuleValue("keepInventory", "true");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        return world;
    }
}
