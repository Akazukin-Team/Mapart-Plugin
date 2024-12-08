package org.akazukin.mapart.command.commands;

import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.ICmdSender;
import net.akazukin.library.i18n.I18n;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.command.commands.mapart.CopyrightSubCommand;
import org.akazukin.mapart.command.commands.mapart.FlySubCommand;
import org.akazukin.mapart.command.commands.mapart.GuiSubCommand;
import org.akazukin.mapart.command.commands.mapart.HelpSubCommand;
import org.akazukin.mapart.command.commands.mapart.ManageSubCommand;
import org.akazukin.mapart.command.commands.mapart.NoArgsSubCommand;

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
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        if (!this.runSubCommand(sender, args, args2)) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.notFound"));
        }
    }
}
