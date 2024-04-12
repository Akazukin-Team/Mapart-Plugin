package net.akazukin.mapart.module.m1_15_to_1_16;

import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class Module_1_15_to_1_16 {
    public static final Module_1_15_to_1_16 SINGLETON = new Module_1_15_to_1_16();

    public ChunkGenerator.BiomeGrid setBiome(final ChunkGenerator.BiomeGrid biomeGrid, final int x, final int y, final int z, final Biome biome) {
        biomeGrid.setBiome(x, y, z, biome);
        return biomeGrid;
    }
}
