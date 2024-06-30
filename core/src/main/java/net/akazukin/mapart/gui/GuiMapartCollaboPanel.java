package net.akazukin.mapart.gui;

import java.util.Arrays;
import java.util.UUID;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.paged.GuiPagedSinglePlayerSelector;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.utils.RepoUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiMapartCollaboPanel extends GuiPagedSinglePlayerSelector {
    private final ItemStack myMapartsItem;

    public GuiMapartCollaboPanel(final UUID player, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui" +
                        ".collaboration")),
                6, 6, player,
                Arrays.stream(MapartSQLConfig.singleton().getTransactionManager().required(() ->
                                RepoUtils.getMapartLandsByCollaborator(player)
                        )).parallel()
                        .map(land -> Bukkit.getOfflinePlayer(land.getOwnerUUID()))
                        .toArray(OfflinePlayer[]::new), prevGui);

        final ItemStack myMapartsItem = ItemUtils.getSkullItem(Bukkit.getOfflinePlayer(player));
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

        if (event.getCurrentItem() == null) return false;

        if (!result && this.myMapartsItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.prevGui);
            return true;
        } else if (result && this.selectedPlayer != null) {
            GuiManager.singleton().setScreen(this.player, () -> new GuiMapartPanel(this.player,
                    UUID.fromString(LibraryPlugin.COMPAT.getNBTString(event.getCurrentItem(), "HEAD_UUID")), false,
                    this));
            return true;
        }
        return false;
    }
}
