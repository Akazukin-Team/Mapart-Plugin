package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandExecutor;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.mapart.gui.GuiMapartPanel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        name = "gui", description = "Open mapart gui", permission = "akazukin.mapart.command.mapart.gui",
        executor = CommandExecutor.PLAYER
)
public class GuiSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        GuiManager.singleton().setScreen(((Player) sender).getUniqueId(),
                () -> new GuiMapartPanel(((Player) sender).getUniqueId(), null));
    }
}
