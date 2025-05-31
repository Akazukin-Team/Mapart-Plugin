package org.akazukin.mapart.command.commands.mapart;

import org.akazukin.library.command.CommandExecutor;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.command.SubCommand;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.mapart.gui.GuiManageMapartUsers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandInfo(
        name = "manage", description = "management user's data", permission = "akazukin.mapart.command.mapart.manage",
        executor = CommandExecutor.PLAYER
)
public final class ManageSubCommand extends SubCommand<ICmdSender> {
    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        //MapartPlugin.getPlugin().getCompat().test();
        final Player p = Bukkit.getPlayer(((IPlayerCmdSender) sender).getUniqueId());
        GuiManager.singleton().setScreen(p, () -> new GuiManageMapartUsers(p, null));
    }
}
