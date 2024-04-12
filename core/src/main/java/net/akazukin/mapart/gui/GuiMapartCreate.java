package net.akazukin.mapart.gui;

import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.ChestGuiBase;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.GuiSizeSelector;
import net.akazukin.library.gui.screens.sign.SignStringSelectorGui;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.StringUtils;
import net.akazukin.library.utils.TaskUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.entity.MMapartLand;
import net.akazukin.mapart.doma.repo.MMapartLandRepo;
import net.akazukin.mapart.mapart.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class GuiMapartCreate extends ChestGuiBase {
    private final GuiSizeSelector heightSelector;
    private final GuiSizeSelector widthSelector;
    private final ItemStack nameItem;
    private final ItemStack heightItem;
    private final ItemStack widthItem;
    private final ItemStack borrowItem;

    private String name;
    private final SignStringSelectorGui nameSelector = new SignStringSelectorGui(player, this);

    public GuiMapartCreate(final UUID player, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.create.main")),
                4, player, false, prevGui);
        heightSelector = new GuiSizeSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.select.height")),
                player, 1, 2, 1, this);
        widthSelector = new GuiSizeSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.select.width")),
                player, 1, 2, 1, this);
        name = MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.defaultName"), Bukkit.getPlayer(player).getName());


        final ItemStack nameItem = new ItemStack(Material.getMaterial("OAK_SIGN"));
        ItemUtils.setDisplayName(nameItem, "§eChange Name");
        this.nameItem = ItemUtils.setGuiItem(nameItem);

        final ItemStack heightItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(heightItem, "§eChange Height");
        this.heightItem = ItemUtils.setGuiItem(heightItem);

        final ItemStack widthItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(widthItem, "§eChange Width");
        this.widthItem = ItemUtils.setGuiItem(widthItem);


        final ItemStack borrowItem = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(borrowItem, "§aBorrow Land");
        this.borrowItem = ItemUtils.setGuiItem(borrowItem);
    }

    @Override
    protected Inventory getInventory() {
        final String lines = StringUtils.join("", nameSelector.getResult());
        if (0 < StringUtils.getLength(lines) && StringUtils.getLength(lines) < 30)
            name = lines;

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, MessageHelper.getLocale(player));
        InventoryUtils.fillCloseItem(inv, MessageHelper.getLocale(player));
        if (prevGui != null)
            InventoryUtils.fillBackItem(inv, MessageHelper.getLocale(player));

        inv.setItem(19, nameItem);
        inv.setItem(21, heightItem);
        inv.setItem(22, widthItem);

        ItemUtils.setLore(borrowItem, Arrays.asList(
                "§aName: §6" + StringUtils.getColoredString(name),
                "§aWidth: " + widthSelector.getResult() + "  §7| §aHeight:" + heightSelector.getResult()
        ));
        inv.setItem(24, borrowItem);

        return inv;
    }

    @Override
    protected void onGuiOpen(final InventoryOpenEvent event) {
    }

    @Override
    public boolean onGuiClick(final InventoryClickEvent event) {
        if (nameItem.equals(event.getCurrentItem())) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.panel.name.message"));
            GuiManager.singleton().setScreen(player, nameSelector);
            return true;
        } else if (heightItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(player, heightSelector);
            return true;
        } else if (widthItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(player, widthSelector);
            return true;
        } else if (borrowItem.equals(event.getCurrentItem())) {
            event.getWhoClicked().closeInventory();
            if (/*MapartManager.getMaxLand(player)*/100 <=
                    TaskUtils.addSynchronizedTask(() -> MapartSQLConfig.singleton().getTransactionManager().required(() ->
                            MMapartLandRepo.select(player))).size()) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(event.getWhoClicked(), I18n.of("mapart.land.limitReached"));
            } else {
                final Player p = Bukkit.getPlayer(player);
                if (p == null) return true;
                p.closeInventory();

                Bukkit.getScheduler().runTask(MapartPlugin.getPlugin(), () -> {
                    if (MapartManager.getWorld() == null) {
                        final World world = MapartManager.generateWorld();
                        if (world == null) {
                            MapartPlugin.MESSAGE_HELPER.sendMessage(event.getWhoClicked(), I18n.of("library.message.world.notfound"));
                            return;
                        }
                    }

                    final MMapartLand landData = MapartManager.lent(player, name, heightSelector.getResult(), widthSelector.getResult());
                    MapartPlugin.MESSAGE_HELPER.sendMessage(p, I18n.of("mapart.land.borrowed"), landData.getLandId());
                    MapartManager.teleportLand(landData.getLandId(), player);
                });
            }
            return true;
        }
        return false;
    }
}
