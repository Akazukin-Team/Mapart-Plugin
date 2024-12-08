package org.akazukin.mapart.compat;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import lombok.Getter;
import org.akazukin.mapart.MapartPlugin;

@Getter
public class CompatManager {
    public static Compat initCompat() {
        return getCompat("org.akazukin.mapart.compat.compats.Compat_" + net.akazukin.library.compat.minecraft.CompatManager.getMappingVersion());
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
