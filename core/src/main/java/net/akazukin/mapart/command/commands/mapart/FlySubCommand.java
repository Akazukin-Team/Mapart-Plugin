package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "fly", description = "Toggle flight at mapart world")
public class FlySubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        if (!(sender instanceof Player)) {
            MapartPlugin.MESSAGE_HELPER.consoleMessage(I18n.of("library.command.execute.mustBeByPlayer"));
            return;
        }

        if (((Player) sender).getWorld().getUID() == MapartManager.getWorld().getUID()) {
            ((Player) sender).setAllowFlight(((Player) sender).getAllowFlight());
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.fly.toggled"));
        } else {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.fly.onlyMapartWorld"));
        }
    }
}
