package org.akazukin.mapart.command;

import org.akazukin.library.command.BukkitCommandManager;
import org.akazukin.mapart.command.commands.MapartCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MapartCommandManager extends BukkitCommandManager {
    public MapartCommandManager(final JavaPlugin plugin) {
        super(plugin);
    }

    public void registerCommands() {
        this.registerCommands(
                MapartCommand.class
        );
    }
}
