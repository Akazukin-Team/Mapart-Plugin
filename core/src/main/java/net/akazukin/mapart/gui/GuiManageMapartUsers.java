package net.akazukin.mapart.gui;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.paged.GuiPagedSinglePlayerSelector;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.repo.MapartUserRepo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class GuiManageMapartUsers extends GuiPagedSinglePlayerSelector {
    public GuiManageMapartUsers(final UUID player, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.manage.users.gui")),
                6, 6, player,
                MapartSQLConfig.singleton().getTransactionManager().required(MapartUserRepo::selectAll)
                        .stream()
                        .map(entity -> Bukkit.getOfflinePlayer(entity.getPlayerUuid()))
                        .toArray(OfflinePlayer[]::new),
                prevGui);
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        final boolean result = super.onGuiClick(event);

        if (event.getCurrentItem() == null) return false;

        if (result && selectedPlayer != null) {
            GuiManager.singleton().setScreen(player, new GuiMapartManageUser(player, UUID.fromString(LibraryPlugin.COMPAT.getNBTString(event.getCurrentItem(), "HEAD_UUID")), this));
            return true;
        }
        return false;
    }
}
