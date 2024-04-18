package net.akazukin.mapart.command.commands.mapart;

import net.akazukin.library.command.CommandExcutor;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.manager.CopyrightManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandInfo(name = "copyright", description = "Toggle flight at mapart world", permission = "akazukin.mapart.command.mapart.copyright", executor = CommandExcutor.PLAYER)
public class CopyrightSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        final ItemStack handItem = ((Player) sender).getInventory().getItemInMainHand();
        if (handItem.getType() == Material.AIR) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.copyright.mustHaveInHand"));
        } else if (CopyrightManager.hasCopyright(handItem)) {
            if (!CopyrightManager.isOwner(handItem, ((Player) sender).getUniqueId())) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.copyright.mustBeOwner"));
            } else {
                final ItemStack item = CopyrightManager.removeCopyright(handItem);
                if (item == null) {
                    MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.copyright.failedRemoving"));
                } else {
                    ((Player) sender).getInventory().setItemInMainHand(item);
                    MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.copyright.removed"));
                }
            }
        } else if (!ItemUtils.getLore(handItem).isEmpty()) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.copyright.invalid"));
        } else {
            final ItemStack item = CopyrightManager.setCopyright(handItem, ((Player) sender).getUniqueId());
            ((Player) sender).getInventory().setItemInMainHand(item);
            MapartPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("mapart.command.copyright.added"));
        }
    }
}
