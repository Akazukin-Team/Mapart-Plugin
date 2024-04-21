package net.akazukin.mapart.compat.compats;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import net.akazukin.mapart.compat.Compat;
import net.akazukin.mapart.module.m1_17_to_1_20.Module_1_17_to_1_20;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Compat_v1_20_R2 implements Compat {
    @Override
    public World createMapartWorld() {
        return Module_1_17_to_1_20.SINGLETON.createMapartWorld();
    }

    @Override
    public void test() {
        final Player p = new ArrayList<>(Bukkit.getOnlinePlayers()).get(0);
        Bukkit.getWorlds().stream().map(world -> new File(world.getWorldFolder(), "playerdata/d028c48f-5c08-467c-ab67-d485f7808e97.dat")).filter(File::exists).forEach(file -> {
            try {
                final NBTTagCompound nbt = NBTCompressedStreamTools.a(file);
                //p.sendMessage(nbt.toString());
                nbt.e().forEach(key -> System.out.println(key + " - " + nbt.c(key)));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }
}
