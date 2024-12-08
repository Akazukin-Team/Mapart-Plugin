package org.akazukin.mapart.command.commands.mapart;

import org.akazukin.i18n.I18n;
import org.akazukin.library.command.CommandExecutor;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.command.SubCommand;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.manager.MapartManager;
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
