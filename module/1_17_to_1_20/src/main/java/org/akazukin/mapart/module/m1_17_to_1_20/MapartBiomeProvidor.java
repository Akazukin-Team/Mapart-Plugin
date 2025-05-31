package org.akazukin.mapart.module.m1_17_to_1_20;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.List;

public class MapartBiomeProvidor extends BiomeProvider {
    @Override
    public Biome getBiome(final WorldInfo worldInfo, final int i, final int i1, final int i2) {
        return Biome.PLAINS;
    }

    @Override
    public List<Biome> getBiomes(final WorldInfo worldInfo) {
        return List.of(Biome.PLAINS);
    }
}
