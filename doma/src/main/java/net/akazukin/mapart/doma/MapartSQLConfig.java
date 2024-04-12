package net.akazukin.mapart.doma;

import lombok.Getter;

import java.io.File;

@Getter
public class MapartSQLConfig extends SQLConfig {
    private static File FILE;
    private static MapartSQLConfig CONFIG;

    public static void setFile(final File file) {
        FILE = file;
    }

    public static MapartSQLConfig singleton() {
        if (CONFIG == null) CONFIG = new MapartSQLConfig(FILE);
        return CONFIG;
    }

    public MapartSQLConfig(final File database) {
        super(database);
    }
}
