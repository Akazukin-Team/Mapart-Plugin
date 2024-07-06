package net.akazukin.mapart.gui;

import java.util.Arrays;
import java.util.UUID;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.ChestGuiBase;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.GuiSizeSelector;
import net.akazukin.library.gui.screens.sign.SignStringSelectorGui;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ArrayUtils;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.StringUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.entity.MMapartLand;
import net.akazukin.mapart.doma.entity.MMapartUser;
import net.akazukin.mapart.doma.repo.MMapartLandRepo;
import net.akazukin.mapart.doma.repo.MMapartUserRepo;
import net.akazukin.mapart.manager.MapartManager;
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

    public GuiMapartCreate(final UUID player, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(player),
                        I18n.of("mapart.panel.gui.create.main")
                ),
                4, player, false, prevGui);
        this.heightSelector = new GuiSizeSelector(
                MapartPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(player),
                        I18n.of("mapart.panel.gui.select.height")
                ),
                player, 1,
                MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("limit.land.size"),
                1, this);
        this.widthSelector = new GuiSizeSelector(
                MapartPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(player),
                        I18n.of("mapart.panel.gui.select.width")
                ),
                player, 1,
                MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("limit.land.size"),
                1, this);
        this.name = MapartPlugin.MESSAGE_HELPER.get(
                MessageHelper.getLocale(player),
                I18n.of("mapart.panel.defaultName"),
                Bukkit.getPlayer(player).getName()
        );


        final ItemStack nameItem = new ItemStack(Material.getMaterial("OAK_SIGN"));
        ItemUtils.setDisplayName(nameItem, MapartPlugin.MESSAGE_HELPER.get(
                MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.name")
        ));
        this.nameItem = ItemUtils.setGuiItem(nameItem);

        final ItemStack heightItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(heightItem, MapartPlugin.MESSAGE_HELPER.get(
                MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.height")
        ));
        this.heightItem = ItemUtils.setGuiItem(heightItem);

        final ItemStack widthItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(widthItem, MapartPlugin.MESSAGE_HELPER.get(
                MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.width")
        ));
        this.widthItem = ItemUtils.setGuiItem(widthItem);


        final ItemStack borrowItem = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(borrowItem, MapartPlugin.MESSAGE_HELPER.get(
                MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.borrow")
        ));
        this.borrowItem = ItemUtils.setGuiItem(borrowItem);
    }

    @Override
    protected Inventory getInventory() {
        final String lines = ArrayUtils.join("", this.nameSelector.getResult());
        if (0 < StringUtils.getLength(lines) && StringUtils.getLength(lines) < 30)
            this.name = lines;

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, MessageHelper.getLocale(this.player));
        InventoryUtils.fillCloseItem(inv, MessageHelper.getLocale(this.player));
        if (this.prevGui != null)
            InventoryUtils.fillBackItem(inv, MessageHelper.getLocale(this.player));

        inv.setItem(10, this.nameItem);
        inv.setItem(12, this.heightItem);
        inv.setItem(13, this.widthItem);

        ItemUtils.setLore(this.borrowItem, Arrays.asList(
                MapartPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.gui.land.borrow.name"),
                        StringUtils.getColoredString(this.name)),
                MapartPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.gui.land.borrow.size"),
                        this.heightSelector.getResult(), this.widthSelector.getResult())
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
                final MMapartUser e = MMapartUserRepo.selectByPlayer(this.player);
                if (e == null || e.getMaxLand() == null)
                    return MapartPlugin.CONFIG_UTILS.getConfig("config.yaml").getInt("limit.land.max");
                return e.getMaxLand();
            }) <=
                    MapartSQLConfig.singleton().getTransactionManager().required(() ->
                            MMapartLandRepo.select(this.player)).size()) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(event.getWhoClicked(), I18n.of("mapart.land.limitReached"));
            } else {
                final Player p = Bukkit.getPlayer(this.player);
                if (p == null) return true;
                p.closeInventory();

                Bukkit.getScheduler().runTask(MapartPlugin.getPlugin(), () -> {
                    final MapartManager mgr = MapartManager.singleton(Math.max(
                            this.heightSelector.getResult(), this.widthSelector.getResult()
                    ));
                    if (mgr.getWorld() == null) {
                        final World world = mgr.generateWorld();
                        if (world == null) {
                            MapartPlugin.MESSAGE_HELPER.sendMessage(
                                    event.getWhoClicked(),
                                    I18n.of("library.message.world.notfound")
                            );
                            return;
                        }
                    }

                    final MMapartLand landData = mgr.lent(this.player, this.name,
                            this.heightSelector.getResult(), this.widthSelector.getResult());
                    MapartPlugin.MESSAGE_HELPER.sendMessage(p, I18n.of("mapart.land.borrowed"), landData.getLandId());
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
