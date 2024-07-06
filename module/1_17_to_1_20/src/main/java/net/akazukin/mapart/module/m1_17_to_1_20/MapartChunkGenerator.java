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
    @Override
    public void generateBedrock(final WorldInfo worldInfo, @Nonnull final Random random,
                                final int chunkX, final int chunkZ, final ChunkData chunkData) {
        if (chunkData.getMinHeight() == worldInfo.getMinHeight()) {
            final MapartManager mgr = MapartManager.singleton(worldInfo.getUID());
            if (mgr == null) return;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (x == 0 || x == 15 || z == 0 || z == 15) {
                        if (mgr.getSize() > 8 && ((x == 0 && (chunkX + 4) % 32 == 0) || (z == 0 && (chunkZ + 4) % 32 == 0) || (x == 15 && (chunkX + 5) % 32 == 0) || (z == 15 && (chunkZ + 5) % 32 == 0))) {
                            chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.RED_WOOL);
                        } else if (mgr.getSize() > 4 && ((x == 0 && (chunkX + 4) % 16 == 0) || (z == 0 && (chunkZ + 4) % 16 == 0) || (x == 15 && (chunkX + 5) % 16 == 0) || (z == 15 && (chunkZ + 5) % 16 == 0))) {
                            chunkData.setBlock(x, chunkData.getMinHeight(), z, Material.YELLOW_WOOL);
                        } else if (mgr.getSize() > 2 && ((x == 0 && (chunkX + 4) % 8 == 0) || (z == 0 && (chunkZ + 4) % 8 == 0) || (x == 15 && (chunkX + 5) % 8 == 0) || (z == 15 && (chunkZ + 5) % 8 == 0))) {
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
