package org.akazukin.mapart.command.commands.mapart;

import org.akazukin.library.command.CommandExecutor;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.command.SubCommand;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.mapart.gui.GuiMapartPanel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandInfo(
        name = "gui", description = "Open mapart gui", permission = "akazukin.mapart.command.mapart.gui",
        executor = CommandExecutor.PLAYER
)
public final class GuiSubCommand extends SubCommand<ICmdSender> {
    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        final Player p = Bukkit.getPlayer(((IPlayerCmdSender) sender).getUniqueId());
        GuiManager.singleton().setScreen(p, () -> new GuiMapartPanel(p, null));
    }
}
