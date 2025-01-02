package org.akazukin.mapart.gui;

import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.gui.screens.chest.paged.GuiPagedSinglePlayerSelector;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.library.utils.ItemUtils;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.utils.RepoUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class GuiMapartCollaboPanel extends GuiPagedSinglePlayerSelector {
    private final ItemStack myMapartsItem;

    public GuiMapartCollaboPanel(final Player player, final GuiBase prevGui) {
        super(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player), I18n.of("mapart.panel.gui.collaboration")),
                6, 6, player,
                Arrays.stream(MapartSQLConfig.singleton().getTransactionManager().required(() ->
                                RepoUtils.getMapartLandsByCollaborator(player.getUniqueId())
                        )).parallel()
                        .map(land -> Bukkit.getOfflinePlayer(land.getOwnerUUID()))
                        .toArray(OfflinePlayer[]::new), prevGui);

        final ItemStack myMapartsItem = ItemUtils.getSkullItem(player);
        ItemUtils.setDisplayName(myMapartsItem, "§aYour Maparts");
        this.myMapartsItem = ItemUtils.setGuiItem(myMapartsItem);
    }

    @Override
    protected Inventory getInventory() {
        final Inventory inv = super.getInventory();
        inv.setItem(51, this.myMapartsItem);
        return inv;
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        final boolean result = super.onGuiClick(event);

        if (event.getCurrentItem() == null) {
            return false;
        }

        if (!result && this.myMapartsItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.prevGui);
            return true;
        } else if (result && this.selectedPlayer != null) {
            GuiManager.singleton().setScreen(this.player, () -> new GuiMapartPanel(this.player,
                    UUID.fromString(LibraryPlugin.getPlugin().getCompat().getPlDataString(event.getCurrentItem(), "HEAD_UUID")), false,
                    this));
            return true;
        }
        return false;
    }
}
