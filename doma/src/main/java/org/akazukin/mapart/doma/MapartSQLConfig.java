package org.akazukin.mapart.doma;

import java.io.File;
import lombok.Getter;

@Getter
public class MapartSQLConfig extends SQLConfig {
    private static File FILE;
    private static MapartSQLConfig CONFIG;

    public MapartSQLConfig(final File database) {
        super(database);
    }

    public static void setFile(final File file) {
        FILE = file;
    }

    public static MapartSQLConfig singleton() {
        if (CONFIG == null) {
            CONFIG = new MapartSQLConfig(FILE);
        }
        return CONFIG;
    }
}
