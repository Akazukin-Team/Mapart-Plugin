package org.akazukin.mapart.module.m1_8_to_1_16;

import org.akazukin.library.utils.ServerUtils;
import org.akazukin.mapart.module.m1_15_to_1_16.Module_1_15_to_1_16;
import org.akazukin.mapart.module.m1_8_to_1_14.Module_1_8_to_1_14;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import javax.annotation.Nonnull;
import java.util.Random;

public class MapartChunkGenerator extends ChunkGenerator {
    @Override
    public ChunkData generateChunkData(@Nonnull final World world, @Nonnull final Random random,
                                       final int chunkX, final int chunkZ, @Nonnull BiomeGrid biome) {
        for (int X = 0; X < 16; X++) {
            for (int Z = 0; Z < 16; Z++) {
                if (ServerUtils.getProtocolVersion() >= 573) {
                    for (int y = 0; y < world.getMaxHeight(); y += 4) {
                        biome = Module_1_15_to_1_16.SINGLETON.setBiome(biome, X, y, Z, Biome.PLAINS);
                    }
                } else {
                    biome = Module_1_8_to_1_14.SINGLETON.setBiome(biome, X, Z, Biome.PLAINS);
                }
            }
        }

        final ChunkData chunkData = super.generateChunkData(world, random, chunkX, chunkZ, biome);

        chunkData.setRegion(0, 0, 0, 15, 0, 15, Material.BLACK_CONCRETE);
        chunkData.setRegion(1, 0, 1, 14, 0, 14, Material.WHITE_WOOL);

        return chunkData;
    }

    @Override
    public final Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0.0D, 1, 0.0D);
    }
}
