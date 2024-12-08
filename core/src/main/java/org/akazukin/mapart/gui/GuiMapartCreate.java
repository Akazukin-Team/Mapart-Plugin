package org.akazukin.mapart.gui;

import java.util.Arrays;
import org.akazukin.i18n.I18n;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.ChestGuiBase;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.gui.screens.chest.GuiSizeSelector;
import org.akazukin.library.gui.screens.sign.SignStringSelectorGui;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.library.utils.InventoryUtils;
import org.akazukin.library.utils.ItemUtils;
import org.akazukin.library.utils.StringUtils;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.entity.MMapartLand;
import org.akazukin.mapart.doma.entity.MMapartUser;
import org.akazukin.mapart.doma.repo.MMapartLandRepo;
import org.akazukin.mapart.doma.repo.MMapartUserRepo;
import org.akazukin.mapart.manager.MapartManager;
import org.akazukin.util.utils.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiMapartCreate extends ChestGuiBase {
    private final GuiSizeSelector heightSelector;
    private final GuiSizeSelector widthSelector;
    private final ItemStack nameItem;
    private final ItemStack heightItem;
    private final ItemStack widthItem;
    private final ItemStack borrowItem;
    private final SignStringSelectorGui nameSelector = new SignStringSelectorGui(this.player, this);
    private String name;

    public GuiMapartCreate(final Player player, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(
                        BukkitMessageHelper.getLocale(player),
                        I18n.of("mapart.panel.gui.create.main")
                ),
                4, player, false, prevGui);
        final int minLandSize = MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("land.size.min");
        final int maxLandSize = MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("land.size.max");
        this.heightSelector = new GuiSizeSelector(
                MapartPlugin.MESSAGE_HELPER.get(
                        BukkitMessageHelper.getLocale(player),
                        I18n.of("mapart.panel.gui.select.height")
                ),
                player,
                minLandSize, maxLandSize, minLandSize,
                this);
        this.widthSelector = new GuiSizeSelector(
                MapartPlugin.MESSAGE_HELPER.get(
                        BukkitMessageHelper.getLocale(player),
                        I18n.of("mapart.panel.gui.select.width")
                ),
                player,
                minLandSize, maxLandSize, minLandSize,
                this);
        this.name = MapartPlugin.MESSAGE_HELPER.get(
                BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.defaultName", player.getName())
        );


        final ItemStack nameItem = new ItemStack(Material.getMaterial("OAK_SIGN"));
        ItemUtils.setDisplayName(nameItem, MapartPlugin.MESSAGE_HELPER.get(
                BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.name")
        ));
        this.nameItem = ItemUtils.setGuiItem(nameItem);

        final ItemStack heightItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(heightItem, MapartPlugin.MESSAGE_HELPER.get(
                BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.height")
        ));
        this.heightItem = ItemUtils.setGuiItem(heightItem);

        final ItemStack widthItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(widthItem, MapartPlugin.MESSAGE_HELPER.get(
                BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.width")
        ));
        this.widthItem = ItemUtils.setGuiItem(widthItem);


        final ItemStack borrowItem = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(borrowItem, MapartPlugin.MESSAGE_HELPER.get(
                BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.borrow")
        ));
        this.borrowItem = ItemUtils.setGuiItem(borrowItem);
    }

    @Override
    protected Inventory getInventory() {
        final String lines = ListUtils.join("", this.nameSelector.getResult());
        if (0 < org.akazukin.util.utils.StringUtils.getLength(lines) && org.akazukin.util.utils.StringUtils.getLength(lines) < 30) {
            this.name = lines;
        }

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, BukkitMessageHelper.getLocale(this.player));
        InventoryUtils.fillCloseItem(inv, BukkitMessageHelper.getLocale(this.player));
        if (this.prevGui != null) {
            InventoryUtils.fillBackItem(inv, BukkitMessageHelper.getLocale(this.player));
        }

        inv.setItem(10, this.nameItem);
        inv.setItem(12, this.heightItem);
        inv.setItem(13, this.widthItem);

        ItemUtils.setLore(this.borrowItem, Arrays.asList(
                MapartPlugin.MESSAGE_HELPER.get(
                        BukkitMessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.gui.land.borrow.name", StringUtils.getColoredString(this.name))),
                MapartPlugin.MESSAGE_HELPER.get(
                        BukkitMessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.gui.land.borrow.size", this.heightSelector.getResult(), this.widthSelector.getResult()))
        ));
        inv.setItem(15, this.borrowItem);

        return inv;
    }

    @Override
    public boolean onGuiClick(final InventoryClickEvent event) {
        if (this.nameItem.equals(event.getCurrentItem())) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.panel.name.message"));
            GuiManager.singleton().setScreen(this.player, () -> this.nameSelector);
            return true;
        } else if (this.heightItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.heightSelector);
            return true;
        } else if (this.widthItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.widthSelector);
            return true;
        } else if (this.borrowItem.equals(event.getCurrentItem())) {
            event.getWhoClicked().closeInventory();
            if (MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartUser e = MMapartUserRepo.selectByPlayer(this.player.getUniqueId());
                if (e == null || e.getMaxLand() == null) {
                    return MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("limit.borrow.default");
                }
                return e.getMaxLand();
            }) <=
                    MapartSQLConfig.singleton().getTransactionManager().required(() ->
                            MMapartLandRepo.selectByOwner(this.player.getUniqueId())).size()) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(event.getWhoClicked(), I18n.of("mapart.land.limitReached"));
            } else {
                this.player.closeInventory();

                Bukkit.getScheduler().runTask(MapartPlugin.getPlugin(), () -> {
                    final MapartManager mgr = MapartManager.singleton(Math.max(
                            this.heightSelector.getResult(), this.widthSelector.getResult()
                    ));

                    final World w = mgr.getWorld();
                    if (w == null) {
                        MapartPlugin.MESSAGE_HELPER.sendMessage(
                                event.getWhoClicked(),
                                I18n.of("library.message.world.notFound")
                        );
                        return;
                    }

                    final MMapartLand landData = mgr.lent(this.player.getUniqueId(), this.name,
                            this.heightSelector.getResult(), this.widthSelector.getResult());
                    MapartPlugin.MESSAGE_HELPER.sendMessage(this.player,
                            I18n.of("mapart.land.borrowed", landData.getLandId()));
                    mgr.teleportLand(landData.getLocationId(), this.player, false);
                });
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onGuiOpen(final InventoryOpenEvent event) {
    }
}
