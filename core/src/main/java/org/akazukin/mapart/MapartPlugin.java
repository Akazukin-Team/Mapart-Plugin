package org.akazukin.mapart;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.i18n.I18nUtils;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.library.utils.ConfigUtils;
import org.akazukin.mapart.command.MapartCommandManager;
import org.akazukin.mapart.compat.Compat;
import org.akazukin.mapart.compat.CompatManager;
import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.dao.DMapartLandCollaboratorDaoImpl;
import org.akazukin.mapart.doma.dao.MMapartLandDaoImpl;
import org.akazukin.mapart.doma.dao.MMapartUserDaoImpl;
import org.akazukin.mapart.doma.dao.MMapartWorldDaoImpl;
import org.akazukin.mapart.doma.repo.MMapartWorldRepo;
import org.akazukin.mapart.event.Events;
import org.akazukin.mapart.event.GrimACEvents;
import org.akazukin.mapart.event.MapartEventManager;
import org.akazukin.mapart.event.MatrixEvents;
import org.akazukin.mapart.event.ThemisEvents;
import org.akazukin.mapart.event.TownyEvents;
import org.akazukin.mapart.manager.mapart.MapartManager;
import org.akazukin.mapart.manager.mapart.MapartWorldListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class MapartPlugin extends JavaPlugin {
    BukkitMessageHelper messageHelper;
    String pluginName;
    MapartCommandManager commandManager;
    ConfigUtils configUtils;
    I18nUtils i18nUtils;
    Compat compat;
    MapartEventManager eventManager;

    public static void main(final String[] args) {
        System.out.println("Main is running!");
    }

    @Override
    public void onLoad() {
        this.pluginName = this.getName();

        getLogManager().addHandler(new Handler() {
            private final File file = new File(MapartPlugin.this.getDataFolder(), "error.log");

            @Override
            public void publish(final LogRecord record) {
                if (record.getLevel() != Level.SEVERE && record.getThrown() == null) {
                    return;
                }

                try (final FileWriter file = new FileWriter(this.file, true)) {
                    try (final BufferedWriter bw = new BufferedWriter(file)) {
                        try (final PrintWriter pw = new PrintWriter(bw)) {
                            pw.println("[" + record.getLevel() + "] " + record.getMessage());
                            if (record.getThrown() != null) {
                                record.getThrown().printStackTrace(pw);
                            }
                            pw.println("\n");
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });


        try {
            Files.createDirectories(this.getDataFolder().toPath());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }


        MapartPlugin.getLogManager().info("Initializing database...");
        MapartSQLConfig.setFile(new File(this.getDataFolder(), "mapart.db"));
        final MapartSQLConfig sqlCfg = MapartSQLConfig.singleton();
        sqlCfg.getTransactionManager().required(() -> {
            new DMapartLandCollaboratorDaoImpl(sqlCfg).create();
            new MMapartLandDaoImpl(sqlCfg).create();
            new MMapartUserDaoImpl(sqlCfg).create();
            new MMapartWorldDaoImpl(sqlCfg).create();
        });
        MapartPlugin.getLogManager().info("Successfully Initialized database");
    }

    @Override
    public void onDisable() {
        this.getServer().getOnlinePlayers().stream()
                .filter(p -> MapartManager.isMapartWorld(p.getWorld()))
                .forEach(MapartWorldListener::teleportLastPos);
    }

    @Override
    public void onEnable() {
        final String libPl = "AkazukinLibrary";
        final Plugin library = Bukkit.getPluginManager().getPlugin(libPl);
        if (library == null || !library.isEnabled()) {
            MapartPlugin.getLogManager().severe(libPl + " is required to enable!");
            this.setEnabled(false);
            return;
        }

        MapartPlugin.getLogManager().info("Initializing version manager...");
        this.compat = CompatManager.initCompat();
        MapartPlugin.getLogManager().info("Successfully Initialized version manager");


        MapartPlugin.getLogManager().info("Initializing configurations...");
        //MapartPlugin.getPlugin().getConfigUtils() = new ConfigUtils(this, new File(this.getDataFolder(), "locales"), "mapart");
        this.configUtils = new ConfigUtils(this, "mapart");
        this.configUtils.loadConfigFiles("config.yaml");
        MapartPlugin.getLogManager().info("Successfully Initialized configurations");

        final YamlConfiguration config = LibraryPlugin.getPlugin().getConfigUtils().getConfig("config.yaml");
        MapartPlugin.getLogManager().info("Initializing i18n manager...");
        this.i18nUtils = new I18nUtils(this.getClassLoader(), "org.akazukin", "mapart", this.getDataFolder(), config.getString("locale"), config.getStringList("locales").toArray(new String[0]));
        this.messageHelper = new BukkitMessageHelper(LibraryPlugin.I18N_UTILS, MapartPlugin.getPlugin().getI18nUtils());
        MapartPlugin.getLogManager().info("Successfully Initialized i18n manager");


        MapartPlugin.getLogManager().info("Initializing event manager...");
        this.eventManager = new MapartEventManager();
        this.eventManager.registerListeners();
        MapartPlugin.getLogManager().info("Successfully Initialized event manager");


        MapartPlugin.getLogManager().info("Initializing event listeners...");
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        if (Bukkit.getPluginManager().isPluginEnabled("GrimAC")) {
            Bukkit.getPluginManager().registerEvents(new GrimACEvents(), this);
        }
        if (Bukkit.getPluginManager().getPlugin("Matrix") != null) {
            Bukkit.getPluginManager().registerEvents(new MatrixEvents(), this);
        }
        if (Bukkit.getPluginManager().getPlugin("Themis") != null) {
            Bukkit.getPluginManager().registerEvents(new ThemisEvents(), this);
        }
        if (Bukkit.getPluginManager().getPlugin("Towny") != null) {
            Bukkit.getPluginManager().registerEvents(new TownyEvents(), this);
        }
        MapartPlugin.getLogManager().info("Successfully Initialized event listeners");


        MapartPlugin.getLogManager().info("Initializing worlds for mapart...");
        MapartSQLConfig.singleton().getTransactionManager().required(MMapartWorldRepo::selectAll)
                .forEach(w -> MapartManager.singleton(w.getLandSize()).generateWorld());
        MapartPlugin.getLogManager().info("Successfully Initialized worlds");


        MapartPlugin.getLogManager().info("Initializing command manager...");
        this.commandManager = new MapartCommandManager(this);
        this.commandManager.registerCommands();
        MapartPlugin.getLogManager().info("Successfully Initialized command manager");


        MapartPlugin.getLogManager().info("Successfully enabled");
    }

    public static Logger getLogManager() {
        return MapartPlugin.getPlugin().getLogger();
    }

    public static MapartPlugin getPlugin() {
        return JavaPlugin.getPlugin(MapartPlugin.class);
    }
}
