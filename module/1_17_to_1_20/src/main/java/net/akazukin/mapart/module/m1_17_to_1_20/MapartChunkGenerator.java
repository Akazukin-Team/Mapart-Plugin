package net.akazukin.mapart.module.m1_17_to_1_20;

import java.util.Random;
import javax.annotation.Nonnull;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class MapartChunkGenerator extends ChunkGenerator {
    private final MapartManager mgr;

    public MapartChunkGenerator(final MapartManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public void generateBedrock(@Nonnull final WorldInfo worldInfo, @Nonnull final Random random,
                                final int chunkX, final int chunkZ, @Nonnull final ChunkData chunkData) {
        final int chuOffX = (int) ((chunkX + 4) % (this.mgr.getSize() * MapartManager.MAP_SIZE));
        final int chuOffZ = (int) ((chunkZ + 4) % (this.mgr.getSize() * MapartManager.MAP_SIZE));

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (x == 0 || x == 15 || z == 0 || z == 15) {
                    if (this.mgr.getSize() >= 16 && ((x == 0 && chuOffX % (8 * 16) == 0) || (z == 0 && chuOffZ % (8 * 16) == 0) || (x == 15 && (chuOffX + 1) % (8 * 16) == 0) || (z == 15 && (chuOffZ + 1) % (8 * 16) == 0))) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.RED_WOOL);
                    } else if (this.mgr.getSize() >= 8 && ((x == 0 && chuOffX % (8 * 8) == 0) || (z == 0 && chuOffZ % (8 * 8) == 0) || (x == 15 && (chuOffX + 1) % (8 * 8) == 0) || (z == 15 && (chuOffZ + 1) % (8 * 8) == 0))) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.RED_WOOL);
                    } else if (this.mgr.getSize() >= 4 && ((x == 0 && chuOffX % (8 * 4) == 0) || (z == 0 && chuOffZ % (8 * 4) == 0) || (x == 15 && (chuOffX + 1) % (8 * 4) == 0) || (z == 15 && (chuOffZ + 1) % (8 * 4) == 0))) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.ORANGE_WOOL);
                    } else if (this.mgr.getSize() >= 2 && ((x == 0 && chuOffX % (8 * 2) == 0) || (z == 0 && chuOffZ % (8 * 2) == 0) || (x == 15 && (chuOffX + 1) % (8 * 2) == 0) || (z == 15 && (chuOffZ + 1) % (8 * 2) == 0))) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.YELLOW_WOOL);
                    } else if ((x == 0 && chuOffX % 8 == 0) || (z == 0 && chuOffZ % 8 == 0) || (x == 15 && (chuOffX + 1) % 8 == 0) || (z == 15 && (chuOffZ + 1) % 8 == 0)) {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.PINK_WOOL);
                    } else {
                        chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.WHITE_WOOL);
                    }
                } else {
                    chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.STONE);
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
