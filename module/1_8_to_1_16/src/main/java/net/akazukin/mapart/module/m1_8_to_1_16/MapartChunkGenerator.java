package net.akazukin.mapart.module.m1_8_to_1_16;

import java.util.Random;
import net.akazukin.library.utils.ServerUtils;
import net.akazukin.mapart.module.m1_15_to_1_16.Module_1_15_to_1_16;
import net.akazukin.mapart.module.m1_8_to_1_14.Module_1_8_to_1_14;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class MapartChunkGenerator extends ChunkGenerator {
    @Override
    public ChunkData generateChunkData(final World world, final Random random, final int x, final int z, BiomeGrid biome) {
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

        final ChunkData chunkData = super.generateChunkData(world, random, x, z, biome);

        chunkData.setRegion(0, 0, 0, 15, 0, 15, Material.STONE);
        chunkData.setRegion(1, 0, 1, 14, 0, 14, Material.WHITE_WOOL);

        return chunkData;
    }

    @Override
    public final Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0.0D, 1, 0.0D);
    }
}
