package net.akazukin.mapart.command;

import net.akazukin.library.command.BukkitCommandManager;
import net.akazukin.mapart.command.commands.MapartCommand;
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
