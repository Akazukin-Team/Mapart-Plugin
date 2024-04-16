package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.i18n.I18n;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.gui.GuiManageMapartUsers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "manage", description = "management user's data")
public class ManageSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        if (!(sender instanceof Player)) {
            MapartPlugin.MESSAGE_HELPER.consoleMessage(I18n.of("library.command.execute.mustBeByPlayer"));
            return;
        } else if (!sender.hasPermission("akazukin.mapart.command.mapart.manage")) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.message.requirePerm"));
            return;
        }

        GuiManager.singleton().setScreen(((Player) sender).getUniqueId(), new GuiManageMapartUsers(((Player) sender).getUniqueId(), null));
    }
}
