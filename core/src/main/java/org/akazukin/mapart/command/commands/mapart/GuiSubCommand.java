package org.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandExecutor;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.ICmdSender;
import net.akazukin.library.command.IPlayerCmdSender;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.gui.GuiManager;
import org.akazukin.mapart.gui.GuiMapartPanel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandInfo(
        name = "gui", description = "Open mapart gui", permission = "akazukin.mapart.command.mapart.gui",
        executor = CommandExecutor.PLAYER
)
public class GuiSubCommand extends SubCommand {
    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        final Player p = Bukkit.getPlayer(((IPlayerCmdSender) sender).getUniqueId());
        GuiManager.singleton().setScreen(p, () -> new GuiMapartPanel(p, null));
    }
}
