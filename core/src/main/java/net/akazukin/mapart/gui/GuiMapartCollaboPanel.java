package net.akazukin.mapart.gui;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.paged.GuiPagedSinglePlayerSelector;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.TaskUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.utils.RepoUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class GuiMapartCollaboPanel extends GuiPagedSinglePlayerSelector {
    private final ItemStack myMapartsItem;

    public GuiMapartCollaboPanel(final UUID player, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.collaboration")),
                6, 6, player, Arrays.stream(TaskUtils.addSynchronizedTask(() -> MapartSQLConfig.singleton().getTransactionManager().required(() -> RepoUtils.getMapartLandsByCollaborator(player)))).map(land ->
                        Bukkit.getOfflinePlayer(land.getOwnerUUID())
                ).filter(Objects::nonNull).toArray(OfflinePlayer[]::new), prevGui);


        final ItemStack myMapartsItem = ItemUtils.getSkullItem(Bukkit.getOfflinePlayer(player));
        ItemUtils.setDisplayName(myMapartsItem, "§aYour Maparts");
        this.myMapartsItem = ItemUtils.setGuiItem(myMapartsItem);
    }

    @Override
    protected Inventory getInventory() {
        final Inventory inv = super.getInventory();
        inv.setItem(51, myMapartsItem);
        return inv;
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        final boolean result = super.onGuiClick(event);

        if (event.getCurrentItem() == null) return false;

        if (!result && myMapartsItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(player, prevGui);
            return true;
        } else if (result && selectedPlayer != null) {
            GuiManager.singleton().setScreen(player, new GuiMapartPanel(player, UUID.fromString(LibraryPlugin.COMPAT.getNBTString(event.getCurrentItem(), "HEAD_UUID"))));
            return true;
        }
        return false;
    }
}
