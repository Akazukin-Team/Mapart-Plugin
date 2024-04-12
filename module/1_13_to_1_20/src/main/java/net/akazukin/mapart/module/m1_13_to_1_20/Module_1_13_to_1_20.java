package net.akazukin.mapart.module.m1_13_to_1_20;

import org.bukkit.GameRule;
import org.bukkit.World;

public class Module_1_13_to_1_20 {
    public static final Module_1_13_to_1_20 SINGLETON = new Module_1_13_to_1_20();

    public void setGameRule(final World world) {
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        final GameRule<Boolean> fallDmg = (GameRule<Boolean>) GameRule.getByName("FALL_DAMAGE");
        if (fallDmg != null) world.setGameRule(fallDmg, false);
    }
}
