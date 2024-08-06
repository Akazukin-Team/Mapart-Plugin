package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandExecutor;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.ICmdSender;
import net.akazukin.library.command.IPlayerCmdSender;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandInfo(name = "fly", description = "Toggle flight at mapart world", executor = CommandExecutor.PLAYER)
public class FlySubCommand extends SubCommand {
    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        final Player p = Bukkit.getPlayer(((IPlayerCmdSender) sender).getUniqueId());
        if (MapartManager.isMapartWorld(p.getWorld())) {
            p.setAllowFlight(((Player) sender).getAllowFlight());
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.fly.toggled"));
        } else {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.fly.onlyMapartWorld"));
        }
    }
}
