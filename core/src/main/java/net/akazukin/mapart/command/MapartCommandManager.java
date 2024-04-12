package net.akazukin.mapart.command;

import net.akazukin.library.command.CommandManager;
import net.akazukin.mapart.command.commands.MapartCommand;

public final class MapartCommandManager extends CommandManager {
    public void registerCommands() {
        registerCommands(
                MapartCommand.class
        );
    }
}
