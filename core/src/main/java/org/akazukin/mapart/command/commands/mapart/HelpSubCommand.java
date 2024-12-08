package org.akazukin.mapart.command.commands.mapart;

import java.util.Arrays;
import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.ICmdSender;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ArrayUtils;
import net.akazukin.library.utils.StringUtils;
import org.akazukin.mapart.MapartPlugin;

@CommandInfo(name = "help", description = "Show list of commands and descriptions")
public class HelpSubCommand extends SubCommand {
    @Override
    public String[] getCompletion(final ICmdSender sender, final String cmdName,
                                  final String[] args, final String[] args2) {

        if (args2.length <= 1) {
            return MapartPlugin.COMMAND_MANAGER.getCommands().stream()
                    .map(Command::getName)
                    .filter(s -> s.toLowerCase().startsWith(StringUtils.toStringOrEmpty(ArrayUtils.getIndex(args2,
                            0)).toLowerCase()))
                    .toArray(String[]::new);
        } else {
            Command cmD = MapartPlugin.COMMAND_MANAGER.getCommand(args2[0]);
            if (cmD == null) return null;

            int lastIndex = 0;
            for (int i = 1; i < Math.min(args2.length - 1, 10); i++) {
                cmD = cmD.getSubCommand(args2[i]);
                lastIndex = i;
                if (cmD == null) return null;
            }

            return cmD.getCompletion(sender, cmdName, args,
                    ArrayUtils.copy(Arrays.asList(args2), 1, args2.length - 2 - lastIndex).toArray(new String[0]));
        }
    }

    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        if (args.length == 1) {
            MapartPlugin.COMMAND_MANAGER.getCommands().forEach(cmd ->
                    MapartPlugin.MESSAGE_HELPER.sendMessage(sender,
                            I18n.of("mapart.command.help.commands." + cmd.getName())));
        } else {
            Command cmd = MapartPlugin.COMMAND_MANAGER.getCommand(args[1]);
            if (cmd == null) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.help.notFound"));
                return;
            }

            final StringBuilder id = new StringBuilder("mapart.command.help.commands." + cmd.getName());
            for (int i = 2; i < Math.min(args.length, 10); i++) {
                cmd = cmd.getSubCommand(args[i]);
                if (cmd == null) {
                    MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.help.notFound"));
                    return;
                }
                id.append(".").append(cmd.getName());
            }

            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of(id.toString()));
            final SubCommand[] subCmds = cmd.getSubCommands();
            Arrays.stream(subCmds).forEach(cmd_ ->
                    MapartPlugin.MESSAGE_HELPER.sendMessage(sender,
                            I18n.of((id + ((StringUtils.getLength(cmd_.getName()) > 0) ? "." + cmd_.getName() : "")))));
        }
    }
}
