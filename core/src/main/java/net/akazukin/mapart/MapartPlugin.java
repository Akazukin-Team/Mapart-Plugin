package net.akazukin.mapart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.command.Command;
import net.akazukin.library.i18n.I18nUtils;
import net.akazukin.library.utils.ConfigUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.mapart.command.MapartCommandManager;
import net.akazukin.mapart.compat.Compat;
import net.akazukin.mapart.compat.CompatManager;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dao.DMapartLandCollaboratorDaoImpl;
import net.akazukin.mapart.doma.dao.MMapartLandDaoImpl;
import net.akazukin.mapart.doma.dao.MMapartUserDaoImpl;
import net.akazukin.mapart.doma.dao.MMapartWorldDaoImpl;
import net.akazukin.mapart.event.Events;
import net.akazukin.mapart.event.GrimACEvents;
import net.akazukin.mapart.event.MapartEventManager;
import net.akazukin.mapart.event.TownyEvents;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MapartPlugin extends JavaPlugin {
    public static String PLUGIN_NAME;
    public static MapartCommandManager COMMAND_MANAGER;
    public static MapartEventManager EVENT_MANAGER;
    public static ConfigUtils CONFIG_UTILS;
    public static I18nUtils I18N_UTILS;
    public static Compat COMPAT;
    public static MessageHelper MESSAGE_HELPER;

    public static void main(final String[] args) {
        System.out.println("Main is running!");
    }

    @Override
    public void onLoad() {
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
    }

    public static MapartPlugin getPlugin() {
        return JavaPlugin.getPlugin(MapartPlugin.class);
    }

    @Override
    public void onDisable() {
        for (final Map.Entry<UUID, Location> entry : MapartManager.getLastPos().entrySet()) {
            final Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null && MapartManager.isMapartWorld(p.getWorld()))
                p.teleport(entry.getValue());
            MapartManager.getLastPos().remove(entry.getKey());
        }
    }

    @Override
    public void onEnable() {
        final LibraryPlugin library = JavaPlugin.getPlugin(LibraryPlugin.class);
        if (!library.isEnabled()) {
            MapartPlugin.getLogManager().severe(library.getName() + " is required to enabled!");
            this.setEnabled(false);
            return;
        }

        MapartPlugin.PLUGIN_NAME = this.getName();

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


        MapartPlugin.getLogManager().info("Initializing version manager...");
        MapartPlugin.COMPAT = CompatManager.initCompat();
        MapartPlugin.getLogManager().info("Successfully Initialized version manager");


        MapartPlugin.getLogManager().info("Initializing configurations...");
        MapartPlugin.CONFIG_UTILS = new ConfigUtils(this, "mapart");
        MapartPlugin.CONFIG_UTILS.loadConfigFiles("config.yaml");
        MapartPlugin.getLogManager().info("Successfully Initialized configurations");


        MapartPlugin.getLogManager().info("Initializing i18n manager...");
        MapartPlugin.I18N_UTILS = new I18nUtils(this, "mapart");
        MapartPlugin.I18N_UTILS.build(LibraryPlugin.CONFIG_UTILS.getConfig("config.yaml").getList("locales").toArray(new String[0]));
        MapartPlugin.MESSAGE_HELPER = new MessageHelper(LibraryPlugin.I18N_UTILS, MapartPlugin.I18N_UTILS);
        MapartPlugin.getLogManager().info("Successfully Initialized i18n manager");


        MapartPlugin.getLogManager().info("Initializing event manager...");
        MapartPlugin.EVENT_MANAGER = new MapartEventManager();
        MapartPlugin.EVENT_MANAGER.registerListeners();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        if (Bukkit.getPluginManager().isPluginEnabled("GrimAC")) {
            Bukkit.getPluginManager().registerEvents(new GrimACEvents(), this);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            Bukkit.getPluginManager().registerEvents(new TownyEvents(), this);
        }
        MapartPlugin.getLogManager().info("Successfully Initialized event manager");


        MapartPlugin.getLogManager().info("Initializing command manager...");
        MapartPlugin.COMMAND_MANAGER = new MapartCommandManager(this);
        MapartPlugin.COMMAND_MANAGER.registerCommands();
        for (final Command cmd : MapartPlugin.COMMAND_MANAGER.getCommands()) {
            final PluginCommand command = this.getCommand(cmd.getName());
            if (command != null) command.setExecutor(MapartPlugin.COMMAND_MANAGER);
            final PluginCommand command2 =
                    this.getCommand(MapartPlugin.getPlugin().getName().toLowerCase() + ":" + cmd.getName());
            if (command2 != null) command2.setExecutor(MapartPlugin.COMMAND_MANAGER);
        }
        MapartPlugin.getLogManager().info("Successfully Initialized command manager");


        Bukkit.broadcastMessage("Successfully enabled");
    }

    public static Logger getLogManager() {
        return MapartPlugin.getPlugin().getLogger();
    }
}
