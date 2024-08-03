package net.akazukin.mapart.command.commands;

import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.i18n.I18n;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.command.commands.mapart.CopyrightSubCommand;
import net.akazukin.mapart.command.commands.mapart.FlySubCommand;
import net.akazukin.mapart.command.commands.mapart.GuiSubCommand;
import net.akazukin.mapart.command.commands.mapart.HelpSubCommand;
import net.akazukin.mapart.command.commands.mapart.ManageSubCommand;
import net.akazukin.mapart.command.commands.mapart.NoArgsSubCommand;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "mapart", description = "mapart command")
public final class MapartCommand extends Command {

    public MapartCommand() {
        this.addSubCommands(NoArgsSubCommand.class,
                CopyrightSubCommand.class,
                GuiSubCommand.class,
                FlySubCommand.class,
                ManageSubCommand.class,
                HelpSubCommand.class
        );
    }

    @Override
    public void run(final CommandSender sender, final String[] args, final String[] args2) {
        if (!this.runSubCommand(sender, args, args2)) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.notFound"));
        }
    }
}
