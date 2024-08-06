package net.akazukin.mapart;

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
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.i18n.I18nUtils;
import net.akazukin.library.manager.BukkitMessageHelper;
import net.akazukin.library.utils.ConfigUtils;
import net.akazukin.mapart.command.MapartCommandManager;
import net.akazukin.mapart.compat.Compat;
import net.akazukin.mapart.compat.CompatManager;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dao.DMapartLandCollaboratorDaoImpl;
import net.akazukin.mapart.doma.dao.MMapartLandDaoImpl;
import net.akazukin.mapart.doma.dao.MMapartUserDaoImpl;
import net.akazukin.mapart.doma.dao.MMapartWorldDaoImpl;
import net.akazukin.mapart.doma.repo.MMapartWorldRepo;
import net.akazukin.mapart.event.Events;
import net.akazukin.mapart.event.GrimACEvents;
import net.akazukin.mapart.event.MapartEventManager;
import net.akazukin.mapart.event.MatrixEvents;
import net.akazukin.mapart.event.ThemisEvents;
import net.akazukin.mapart.event.TownyEvents;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MapartPlugin extends JavaPlugin {
    public static String PLUGIN_NAME;
    public static MapartCommandManager COMMAND_MANAGER;
    public static MapartEventManager EVENT_MANAGER;
    public static ConfigUtils CONFIG_UTILS;
    public static I18nUtils I18N_UTILS;
    public static Compat COMPAT;
    public static BukkitMessageHelper MESSAGE_HELPER;

    public static void main(final String[] args) {
        System.out.println("Main is running!");
    }

    @Override
    public void onLoad() {
        MapartPlugin.PLUGIN_NAME = this.getName();

        MapartPlugin.getPlugin().getLogger().addHandler(new Handler() {
            private final File file = new File(MapartPlugin.getPlugin().getDataFolder(), "error.log");

            @Override
            public void publish(final LogRecord record) {
                if (record.getLevel() == Level.SEVERE || record.getThrown() != null) {
                    try (final FileWriter file = new FileWriter(this.file, true)) {
                        try (final PrintWriter pw = new PrintWriter(new BufferedWriter(file))) {
                            pw.println("[" + record.getLevel() + "] " + record.getMessage());
                            //pw.println(pw);
                            if (record.getThrown() != null) {
                                record.getThrown().printStackTrace(pw);
                            }
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
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

    public static MapartPlugin getPlugin() {
        return JavaPlugin.getPlugin(MapartPlugin.class);
    }

    @Override
    public void onDisable() {
        this.getServer().getOnlinePlayers().stream()
                .filter(p -> MapartManager.isMapartWorld(p.getWorld()))
                .forEach(MapartManager::teleportLastPos);
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
        MapartPlugin.COMPAT = CompatManager.initCompat();
        MapartPlugin.getLogManager().info("Successfully Initialized version manager");


        MapartPlugin.getLogManager().info("Initializing configurations...");
        //MapartPlugin.CONFIG_UTILS = new ConfigUtils(this, new File(this.getDataFolder(), "locales"), "mapart");
        MapartPlugin.CONFIG_UTILS = new ConfigUtils(this, "mapart");
        MapartPlugin.CONFIG_UTILS.loadConfigFiles("config.yaml");
        MapartPlugin.getLogManager().info("Successfully Initialized configurations");


        MapartPlugin.getLogManager().info("Initializing i18n manager...");
        MapartPlugin.I18N_UTILS = new I18nUtils(this, "mapart", new File(this.getDataFolder(), "locales"));
        MapartPlugin.I18N_UTILS.build(LibraryPlugin.getPlugin().getConfigUtils().getConfig("config.yaml")
                .getStringList("locales").toArray(new String[0]));
        MapartPlugin.MESSAGE_HELPER = new BukkitMessageHelper(LibraryPlugin.I18N_UTILS, MapartPlugin.I18N_UTILS);
        MapartPlugin.getLogManager().info("Successfully Initialized i18n manager");


        MapartPlugin.getLogManager().info("Initializing event manager...");
        MapartPlugin.EVENT_MANAGER = new MapartEventManager();
        MapartPlugin.EVENT_MANAGER.registerListeners();
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
        MapartPlugin.COMMAND_MANAGER = new MapartCommandManager(this);
        MapartPlugin.COMMAND_MANAGER.registerCommands();
        MapartPlugin.getLogManager().info("Successfully Initialized command manager");


        MapartPlugin.getLogManager().info("Successfully enabled");
    }

    public static Logger getLogManager() {
        return MapartPlugin.getPlugin().getLogger();
    }
}
