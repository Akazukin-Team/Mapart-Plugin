package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.mapart.MapartPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "manage", description = "management user's data")
public class ManageSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        if (!(sender instanceof Player)) {
            MapartPlugin.MESSAGE_HELPER.consoleMessage(I18n.of("library.command.execute.mustBeByPlayer"));
        }
    }
}
