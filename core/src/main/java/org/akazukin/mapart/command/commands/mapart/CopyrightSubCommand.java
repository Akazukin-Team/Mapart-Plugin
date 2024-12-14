package org.akazukin.mapart.command.commands.mapart;

import org.akazukin.i18n.I18n;
import org.akazukin.library.command.CommandExecutor;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.command.SubCommand;
import org.akazukin.library.utils.ItemUtils;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.manager.CopyrightManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandInfo(
        name = "copyright", description = "Toggle flight at mapart world",
        permission = "akazukin.mapart.command.mapart.copyright", executor = CommandExecutor.PLAYER
)
public class CopyrightSubCommand extends SubCommand {
    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        final Player p = Bukkit.getPlayer(((IPlayerCmdSender) sender).getUniqueId());

        final ItemStack handItem = p.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.AIR) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("mapart.command.copyright.mustHaveInHand"));
        } else if (CopyrightManager.hasCopyright(handItem)) {
            if (!CopyrightManager.isOwner(handItem, p.getUniqueId())) {
                MapartPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("mapart.command.copyright.mustBeOwner"));
            } else {
                final ItemStack item = CopyrightManager.removeCopyright(handItem);
                if (item == null) {
                    MapartPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("mapart.command.copyright.failedRemoving"));
                } else {
                    p.getInventory().setItemInMainHand(item);
                    MapartPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("mapart.command.copyright.removed"));
                }
            }
        } else if (!ItemUtils.getLore(handItem).isEmpty()) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("mapart.command.copyright.invalid"));
        } else {
            final ItemStack item = CopyrightManager.setCopyright(handItem, p);
            p.getInventory().setItemInMainHand(item);
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("mapart.command.copyright.added"));
        }
    }
}
