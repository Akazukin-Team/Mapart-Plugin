package net.akazukin.mapart;

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
import net.akazukin.mapart.event.Events;
import net.akazukin.mapart.event.MapartEventManager;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

    public static MapartPlugin getPlugin() {
        return getPlugin(MapartPlugin.class);
    }

    public static Logger getLogManager() {
        return getPlugin().getLogger();
    }

    @Override
    public void onLoad() {
        getPlugin().getLogger().addHandler(new Handler() {
            private final File file = new File(getPlugin().getDataFolder(), "error.log");

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

    @Override
    public void onDisable() {
        for (final Map.Entry<UUID, Location> entry : MapartManager.SINGLETON.getLastPos().entrySet()) {
            final Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null && p.getWorld().getUID() == MapartManager.getWorld().getUID())
                p.teleport(entry.getValue());
            MapartManager.SINGLETON.getLastPos().remove(entry.getKey());
        }
    }

    @Override
    public void onEnable() {
        final LibraryPlugin library = getPlugin(LibraryPlugin.class);
        if (!library.isEnabled()) {
            getLogManager().severe(library.getName() + " is required to enabled!");
            this.setEnabled(false);
            return;
        }

        MapartPlugin.PLUGIN_NAME = this.getName();

        try {
            Files.createDirectories(this.getDataFolder().toPath());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }


        getLogManager().info("Initializing database...");
        MapartSQLConfig.setFile(new File(this.getDataFolder(), "mapart.db"));
        final MapartSQLConfig sqlCfg = MapartSQLConfig.singleton();
        sqlCfg.getTransactionManager().required(() -> {
            new DMapartLandCollaboratorDaoImpl(sqlCfg).create();
            new MMapartLandDaoImpl(sqlCfg).create();
            new MMapartUserDaoImpl(sqlCfg).create();
        });
        getLogManager().info("Successfully Initialized database");


        getLogManager().info("Initializing version manager...");
        COMPAT = CompatManager.initCompat();
        getLogManager().info("Successfully Initialized version manager");


        getLogManager().info("Initializing configurations...");
        CONFIG_UTILS = new ConfigUtils(this, "mapart");
        CONFIG_UTILS.loadConfigFiles("config.yaml");
        getLogManager().info("Successfully Initialized configurations");


        getLogManager().info("Initializing i18n manager...");
        I18N_UTILS = new I18nUtils(this, "mapart");
        I18N_UTILS.build(LibraryPlugin.CONFIG_UTILS.getConfig("config.yaml").getList("locales").toArray(new String[0]));
        MESSAGE_HELPER = new MessageHelper(LibraryPlugin.I18N_UTILS, I18N_UTILS);
        getLogManager().info("Successfully Initialized i18n manager");


        getLogManager().info("Initializing event manager...");
        EVENT_MANAGER = new MapartEventManager();
        EVENT_MANAGER.registerListeners();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        getLogManager().info("Successfully Initialized event manager");


        getLogManager().info("Initializing mapart world...");
        if (MapartManager.getWorld() == null) {
            MapartManager.generateWorld();
        }
        getLogManager().info("Successfully Initialized mapart world");


        getLogManager().info("Initializing command manager...");
        COMMAND_MANAGER = new MapartCommandManager();
        COMMAND_MANAGER.registerCommands();
        for (final Command cmd : COMMAND_MANAGER.getCommands()) {
            final PluginCommand command = this.getCommand(cmd.getName());
            if (command != null) command.setExecutor(COMMAND_MANAGER);
            final PluginCommand command2 = this.getCommand(getPlugin().getName().toLowerCase() + ":" + cmd.getName());
            if (command2 != null) command2.setExecutor(COMMAND_MANAGER);
        }
        getLogManager().info("Successfully Initialized command manager");


        Bukkit.broadcastMessage("Successfully enabled");
    }
}
