package net.akazukin.mapart.module.m1_8_to_1_14;

import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class Module_1_8_to_1_14 {
    public static Module_1_8_to_1_14 SINGLETON = new Module_1_8_to_1_14();

    public ChunkGenerator.BiomeGrid setBiome(final ChunkGenerator.BiomeGrid biomeGrid, final int x, final int z, final Biome biome) {
        biomeGrid.setBiome(x, z, biome);
        return biomeGrid;
    }
}
