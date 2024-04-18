package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.mapart.MapartPlugin;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@CommandInfo(name = "help", description = "Show list of commands and descriptions")
public class HelpSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        if (args.length == 0) {
            MapartPlugin.COMMAND_MANAGER.getCommands().forEach(cmd ->
                    MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.help.command" + cmd.getName())));
        } else {
            Command cmd = MapartPlugin.COMMAND_MANAGER.getCommand(args[0]);
            if (cmd == null) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.help.notFound"));
                return;
            }

            final StringBuilder id = new StringBuilder("mapart.command.help.command." + cmd.getName());
            for (int i = 1; i < Math.min(args.length, 10); i++) {
                cmd = cmd.getSubCommand(args[i]);
                id.append(".").append(args[i]);
                if (cmd == null) {
                    MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.help.notFound"));
                    return;
                }
            }
            final SubCommand[] subCmds = cmd.getSubCommands();
            if (subCmds.length == 0) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of(id.toString()));
            } else {
                Arrays.stream(subCmds).map(SubCommand::getName).forEach(name ->
                        MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of((id + name).toLowerCase())));
            }
        }
    }
}
