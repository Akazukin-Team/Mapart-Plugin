package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandExcutor;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.mapart.gui.GuiManageMapartUsers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "manage", description = "management user's data", permission = "akazukin.mapart.command.mapart.manage", executor = CommandExcutor.PLAYER)
public class ManageSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        //MapartPlugin.COMPAT.test();
        GuiManager.singleton().setScreen(((Player) sender).getUniqueId(), new GuiManageMapartUsers(((Player) sender).getUniqueId(), null));
    }
}
