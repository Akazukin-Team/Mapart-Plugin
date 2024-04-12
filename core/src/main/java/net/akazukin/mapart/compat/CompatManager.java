package net.akazukin.mapart.compat;

import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.mapart.MapartPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

@Getter
public class CompatManager {
    public static Compat initCompat() {
        final String version = LibraryPlugin.getPlugin().getServer().getClass().getPackage().getName().split("\\.")[3];
        return getCompat("net.akazukin.mapart.compat.compats.Compat_" + version);
    }

    public static Compat getCompat(final String clazzName) {
        try {
            final Class<?> clazz = Class.forName(clazzName);
            if (Compat.class.isAssignableFrom(clazz)) {
                return getCompat((Class<? extends Compat>) clazz);
            } else {
                throw new IllegalArgumentException("The class was not extends ComaptClass");
            }
        } catch (final IllegalArgumentException | ClassNotFoundException e) {
            MapartPlugin.getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public static Compat getCompat(final Class<? extends Compat> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (final IllegalArgumentException | InvocationTargetException |
                       NoSuchMethodException |
                       IllegalAccessException | InstantiationException e) {
            MapartPlugin.getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
}
