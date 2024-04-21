package net.akazukin.mapart.command.commands;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.StringUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.command.commands.mapart.CopyrightSubCommand;
import net.akazukin.mapart.command.commands.mapart.FlySubCommand;
import net.akazukin.mapart.command.commands.mapart.GuiSubCommand;
import net.akazukin.mapart.command.commands.mapart.HelpSubCommand;
import net.akazukin.mapart.command.commands.mapart.ManageSubCommand;
import net.akazukin.mapart.command.commands.mapart.NoArgsSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "mapart", description = "mapart command")
public final class MapartCommand extends Command {

    @Override
    public void run(final CommandSender sender, final String... args) {
        final SubCommand subCmd = this.getSubCommand(StringUtils.getIndex(args, 0));
        if (subCmd == null) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.notFound"));
        } else if (!subCmd.validExecutor(sender)) {
            LibraryPlugin.MESSAGE_HELPER.consoleMessage(I18n.of("library.command.execute.mustBeBy" + (sender instanceof Player ? "Console" : "Player")));
        } else if (!subCmd.hasPermission(sender)) {
            LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.message.requirePerm"));
        } else {
            subCmd.run(sender, args);
        }
    }

    @Override
    public SubCommand[] getSubCommands() {
        return new SubCommand[]{
                new NoArgsSubCommand(),
                new CopyrightSubCommand(),
                new GuiSubCommand(),
                new FlySubCommand(),
                new ManageSubCommand(),
                new HelpSubCommand()
        };
    }
}
