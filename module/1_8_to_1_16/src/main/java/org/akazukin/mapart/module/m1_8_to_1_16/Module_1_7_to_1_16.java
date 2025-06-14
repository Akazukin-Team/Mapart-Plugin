package org.akazukin.mapart.module.m1_8_to_1_16;

import org.akazukin.library.utils.ServerUtils;
import org.akazukin.mapart.manager.mapart.MapartWorldData;
import org.akazukin.mapart.module.m1_13_to_1_20.Module_1_13_to_1_20;
import org.akazukin.mapart.module.m1_8_to_1_12.Module_1_8_to_1_12;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Module_1_7_to_1_16 {
    private final Module_1_7_to_1_16 SINGLETON = new Module_1_7_to_1_16();

    public World createMapartWorld(final MapartWorldData mapartWorldData) {
        final WorldCreator wc = new WorldCreator(mapartWorldData.getName());
        wc.environment(World.Environment.NORMAL);
        wc.generator(new MapartChunkGenerator());
        final World world = Bukkit.createWorld(wc);

        if (world == null) {
            return null;
        }

        world.setAutoSave(true);
        world.setDifficulty(Difficulty.EASY);
        final int protocolVersion = ServerUtils.getProtocolVersion();
        if (protocolVersion >= 393) {
            Module_1_13_to_1_20.SINGLETON.setGameRule(world);
        } else {
            Module_1_8_to_1_12.SINGLETON.setGameRule(world);
        }

        return world;
    }
}
