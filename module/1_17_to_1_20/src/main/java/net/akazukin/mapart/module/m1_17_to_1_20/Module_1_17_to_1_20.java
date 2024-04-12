package net.akazukin.mapart.module.m1_17_to_1_20;

import net.akazukin.mapart.mapart.MapartManager;
import net.akazukin.mapart.module.m1_13_to_1_20.Module_1_13_to_1_20;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Module_1_17_to_1_20 {
    public static final Module_1_17_to_1_20 SINGLETON = new Module_1_17_to_1_20();

    public World createMapartWorld() {
        final WorldCreator wc = new WorldCreator(MapartManager.getWorldName());
        wc.environment(World.Environment.NORMAL);
        wc.biomeProvider(new MapartBiomeProvidor());
        wc.generator(new MapartChunkGenerator());
        final World world = Bukkit.createWorld(wc);

        if (world == null) return null;

        world.setAutoSave(true);
        world.setDifficulty(Difficulty.EASY);

        Module_1_13_to_1_20.SINGLETON.setGameRule(world);

        return world;
    }
}
