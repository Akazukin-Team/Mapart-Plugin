package org.akazukin.mapart.module.m1_17_to_1_20;

import org.akazukin.mapart.manager.mapart.MapartManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import javax.annotation.Nonnull;
import java.util.Random;

public class MapartChunkGenerator extends ChunkGenerator {
    private Integer size;

    public MapartChunkGenerator(final int size) {
        this.size = size;
    }

    @Override
    public void generateBedrock(@Nonnull final WorldInfo worldInfo, @Nonnull final Random random,
                                final int chunkX, final int chunkZ, @Nonnull final ChunkData chunkData) {
        if (this.size == null) {
            final MapartManager mgr = MapartManager.singleton(worldInfo.getUID());
            if (mgr != null) {
                this.size = mgr.getSize();
            }
        }

        final long chuOffX = (chunkX + 4) % ((long) this.size * (long) MapartManager.MAP_SIZE);
        final long chuOffZ = (chunkZ + 4) % ((long) this.size * (long) MapartManager.MAP_SIZE);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (x == 0 || x == 15 || z == 0 || z == 15) {
                    if (this.size >= 16 && ((x == 0 && chuOffX % (8 * 16) == 0) || (z == 0 && chuOffZ % (8 * 16) == 0) || (x == 15 && (chuOffX + 1) % (8 * 16) == 0) || (z == 15 && (chuOffZ + 1) % (8 * 16) == 0))) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.RED_WOOL);
                    } else if (this.size >= 8 && ((x == 0 && chuOffX % (8 * 8) == 0) || (z == 0 && chuOffZ % (8 * 8) == 0) || (x == 15 && (chuOffX + 1) % (8 * 8) == 0) || (z == 15 && (chuOffZ + 1) % (8 * 8) == 0))) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.RED_WOOL);
                    } else if (this.size >= 4 && ((x == 0 && chuOffX % (8 * 4) == 0) || (z == 0 && chuOffZ % (8 * 4) == 0) || (x == 15 && (chuOffX + 1) % (8 * 4) == 0) || (z == 15 && (chuOffZ + 1) % (8 * 4) == 0))) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.ORANGE_WOOL);
                    } else if (this.size >= 2 && ((x == 0 && chuOffX % (8 * 2) == 0) || (z == 0 && chuOffZ % (8 * 2) == 0) || (x == 15 && (chuOffX + 1) % (8 * 2) == 0) || (z == 15 && (chuOffZ + 1) % (8 * 2) == 0))) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.YELLOW_WOOL);
                    } else if ((x == 0 && chuOffX % 8 == 0) || (z == 0 && chuOffZ % 8 == 0) || (x == 15 && (chuOffX + 1) % 8 == 0) || (z == 15 && (chuOffZ + 1) % 8 == 0)) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.PINK_WOOL);
                    } else {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.WHITE_WOOL);
                    }
                } else {
                    chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.BLACK_CONCRETE);
                }
            }
        }
    }

    @Override
    public final Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0.0D, world.getMinHeight() + 1, 0.0D);
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return true;
    }
}
