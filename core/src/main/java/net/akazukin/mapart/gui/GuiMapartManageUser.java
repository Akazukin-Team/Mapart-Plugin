package net.akazukin.mapart.gui;

import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.ChestGuiBase;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.GuiSizeSelector;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dto.MapartUserDto;
import net.akazukin.mapart.doma.entity.MMapartLand;
import net.akazukin.mapart.doma.entity.MMapartUser;
import net.akazukin.mapart.doma.repo.MMapartLandRepo;
import net.akazukin.mapart.doma.repo.MMapartUserRepo;
import net.akazukin.mapart.doma.repo.MapartUserRepo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GuiMapartManageUser extends ChestGuiBase {
    private final GuiSizeSelector maxLandSelector;
    private final ItemStack headItem;
    private final ItemStack maxLandItem;
    private final ItemStack manageMapartsItem;

    private final UUID member;

    public GuiMapartManageUser(final UUID player, final UUID member, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.manage.user.gui"), Bukkit.getOfflinePlayer(player).getName()),
                4, player, false, prevGui);

        this.member = member;

        final OfflinePlayer membeR = Bukkit.getOfflinePlayer(member);

        maxLandSelector = new GuiSizeSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.manage.maxLand.gui"), Bukkit.getOfflinePlayer(player).getName()),
                player, 1, 2, 1, this);

        final ItemStack headItem = ItemUtils.getSkullItem(membeR);
        ItemUtils.setDisplayName(headItem, "§a" + membeR.getName());
        this.headItem = ItemUtils.setGuiItem(headItem);

        final ItemStack maxLandItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(maxLandItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.manage.maxLand.item"), Bukkit.getOfflinePlayer(player).getName()));
        this.maxLandItem = ItemUtils.setGuiItem(maxLandItem);

        final ItemStack manageMapartsItem = new ItemStack(Material.getMaterial("PAPER"));
        ItemUtils.setDisplayName(manageMapartsItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.manage.maparts.item"), Bukkit.getOfflinePlayer(player).getName()));
        this.manageMapartsItem = ItemUtils.setGuiItem(manageMapartsItem);

        final MapartUserDto entity = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MapartUserRepo.selectByPlayer(member));
        maxLandSelector.setDefaultSize(entity.getMaxLand() == null ? MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("limit.land.default") : entity.getMaxLand());
    }

    @Override
    protected Inventory getInventory() {
        if (maxLandSelector.isDone()) {
            maxLandSelector.setDefaultSize(maxLandSelector.getResult());
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                MMapartUser entity = MMapartUserRepo.selectByPlayer(player);
                if (entity == null) {
                    entity = new MMapartUser();
                    entity.setPlayerUuid(player);
                }
                entity.setMaxLand(maxLandSelector.getResult());
            });
            Bukkit.getPlayer(player).sendMessage(maxLandSelector.getResult() + "");
            maxLandSelector.reset();
        }


        final List<MMapartLand> landsEntity = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MMapartLandRepo.select(member));

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, MessageHelper.getLocale(player));
        InventoryUtils.fillCloseItem(inv, MessageHelper.getLocale(player));
        if (prevGui != null)
            InventoryUtils.fillBackItem(inv, MessageHelper.getLocale(player));

        ItemUtils.setLore(headItem, Arrays.asList(
                MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.manage.user.head.lore.maxLand"), maxLandSelector.getResult()),
                MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.manage.user.head.lore.countLand"), landsEntity.size())
        ));
        inv.setItem(4, headItem);
        inv.setItem(10, maxLandItem);
        inv.setItem(12, manageMapartsItem);
        return inv;
    }

    @Override
    public boolean onGuiClick(final InventoryClickEvent event) {
        if (maxLandItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(player, maxLandSelector);
            return true;
        } else if (manageMapartsItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(player, new GuiMapartPanel(player, member, true, this));
            return true;
        }
        return false;
    }
}