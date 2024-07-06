package net.akazukin.mapart.command;

import net.akazukin.library.command.CommandManager;
import net.akazukin.mapart.command.commands.MapartCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MapartCommandManager extends CommandManager {
    public MapartCommandManager(final JavaPlugin plugin) {
        super(plugin);
    }

    public void registerCommands() {
        this.registerCommands(
                MapartCommand.class
        );
    }
}
