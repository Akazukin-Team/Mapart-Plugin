package org.akazukin.mapart.gui;

import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.gui.screens.chest.paged.GuiPagedSinglePlayerSelector;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.repo.MapartUserRepo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class GuiManageMapartUsers extends GuiPagedSinglePlayerSelector {
    public GuiManageMapartUsers(final Player player, final GuiBase prevGui) {
        super(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player), I18n.of("mapart.panel.manage.users.gui")),
                6, 6, player,
                MapartSQLConfig.singleton().getTransactionManager().required(MapartUserRepo::selectAll)
                        .parallelStream()
                        .map(entity -> Bukkit.getOfflinePlayer(entity.getPlayerUuid()))
                        .toArray(OfflinePlayer[]::new),
                prevGui);
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        final boolean result = super.onGuiClick(event);

        if (event.getCurrentItem() == null) {
            return false;
        }

        if (result && this.selectedPlayer != null) {
            GuiManager.singleton().setScreen(this.player, () ->
                    new GuiMapartManageUser(this.player,
                            UUID.fromString(LibraryPlugin.getPlugin().getCompat().getPlDataString(event.getCurrentItem(), "HEAD_UUID")),
                            this));
            return true;
        }
        return false;
    }
}
