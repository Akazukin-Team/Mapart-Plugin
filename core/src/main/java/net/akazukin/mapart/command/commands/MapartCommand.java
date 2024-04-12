package net.akazukin.mapart.command.commands;

import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.StringUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.command.commands.mapart.FlySubCommand;
import net.akazukin.mapart.command.commands.mapart.ManageSubCommand;
import net.akazukin.mapart.command.commands.mapart.NoArgsSubCommand;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "mapart", description = "mapart command")
public final class MapartCommand extends Command {

    @Override
    public SubCommand[] getSubCommands() {
        return new SubCommand[]{
                new NoArgsSubCommand(),
                new FlySubCommand(),
                new ManageSubCommand()
        };
    }

    @Override
    public boolean run(final CommandSender sender, final String... args) {
        final SubCommand subCmd = getSubCommand(StringUtils.getIndex(args, 0));
        if (subCmd == null) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.notFound"));
            return true;
        }
        return subCmd.run(sender, args);
    }
}
