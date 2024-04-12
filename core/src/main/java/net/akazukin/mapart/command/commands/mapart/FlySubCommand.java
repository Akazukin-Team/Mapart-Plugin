package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.mapart.MapartManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "", description = "Open mapart gui")
public class FlySubCommand extends SubCommand {
    @Override
    public boolean run(final CommandSender sender, final String... args) {
        if (!(sender instanceof Player)) {
            MapartPlugin.MESSAGE_HELPER.consoleMessage(I18n.of("library.command.execute.mustBeByPlayer"));
            return true;
        }

        if (((Player) sender).getWorld().getUID() == MapartManager.getWorld().getUID()) {
            ((Player) sender).setAllowFlight(((Player) sender).getAllowFlight());
            MapartPlugin.MESSAGE_HELPER.consoleMessage(I18n.of("mapart.command.fly.toggled"));
        } else {
            MapartPlugin.MESSAGE_HELPER.consoleMessage(I18n.of("mapart.command.fly.onlyMapartWorld"));
        }
        return true;
    }
}
