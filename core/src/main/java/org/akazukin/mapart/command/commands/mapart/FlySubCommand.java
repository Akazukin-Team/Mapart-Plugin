package org.akazukin.mapart.command.commands.mapart;

import org.akazukin.i18n.I18n;
import org.akazukin.library.command.CommandExecutor;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.command.SubCommand;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.manager.mapart.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandInfo(name = "fly", description = "Toggle flight at mapart world", executor = CommandExecutor.PLAYER)
public final class FlySubCommand extends SubCommand<ICmdSender> {
    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        final Player p = Bukkit.getPlayer(((IPlayerCmdSender) sender).getUniqueId());
        if (MapartManager.isMapartWorld(p.getWorld())) {
            p.setAllowFlight(p.getAllowFlight());
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("mapart.command.fly.toggled"));
        } else {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("mapart.command.fly.onlyMapartWorld"));
        }
    }
}
