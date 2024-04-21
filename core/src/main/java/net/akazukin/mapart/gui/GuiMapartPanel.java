package net.akazukin.mapart.gui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.paged.GuiPagedSingleSelector;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.StringUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dto.MapartLandDto;
import net.akazukin.mapart.doma.repo.MapartLandRepo;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiMapartPanel extends GuiPagedSingleSelector {
    private final UUID guiUserUuid;
    private final ItemStack createItem;
    private final ItemStack myMapartsItem;
    private final ItemStack collaboMapartsItem;
    private final ItemStack headItem;

    private final boolean isAdmin;

    public GuiMapartPanel(final UUID player, final GuiBase prevGui) {
        this(player, player, true, prevGui);
    }

    public GuiMapartPanel(final UUID player, final UUID guiUserUuid, final boolean isAdmin, final GuiBase prevGui) {
        super(
                MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.list." + (player == guiUserUuid ? "own" : "others")), (player == guiUserUuid ? null : Bukkit.getOfflinePlayer(guiUserUuid).getName())),
                6, 6, player, MapartSQLConfig.singleton().getTransactionManager().required(() ->
                                MapartLandRepo.selectByPlayer(guiUserUuid))
                        .stream()
                        .filter(land -> isAdmin || player.equals(guiUserUuid) ||
                                Arrays.asList(land.getCollaboratorsUUID()).contains(player))
                        .map(land -> {
                            final OfflinePlayer owner = Bukkit.getOfflinePlayer(land.getOwnerUUID());

                            final ItemStack landItem = new ItemStack(Material.getMaterial("PAPER"));
                            ItemUtils.setDisplayName(landItem, StringUtils.getColoredString(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.lands.info.name"), land.getName())));
                            final List<String> lore = new ArrayList<>(Arrays.asList(
                                    MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.lands.info.owner"), owner.getName()),
                                    MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.lands.info.landId"), land.getLandId())
                            ));

                            if (land.getCollaboratorsUUID().length != 0)
                                lore.add("");
                            lore.add(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.lands.info.collaborators"), land.getCollaboratorsUUID().length));
                            lore.addAll(Arrays.stream(land.getCollaboratorsUUID()).map(uuid -> {
                                final OfflinePlayer collabo = Bukkit.getOfflinePlayer(uuid);
                                return MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.lands.info.collaborator"), collabo.getName());
                            }).collect(Collectors.toList()));
                            if (land.getCollaboratorsUUID().length != 0)
                                lore.add("");

                            final LocalDateTime date = land.getCreatedDate().toLocalDateTime();
                            final int milliSec = date.getNano() / 1000000;
                            lore.add(
                                    MapartPlugin.MESSAGE_HELPER.get(
                                            MessageHelper.getLocale(player),
                                            I18n.of("mapart.panel.lands.info.createdAt"),
                                            date.getYear(),
                                            (date.getMonthValue() >= 10 ? "" : "0") + date.getMonthValue(),
                                            (date.getDayOfMonth() >= 10 ? "" : "0") + date.getDayOfMonth(),

                                            (date.getHour() >= 10 ? "" : "0") + date.getHour(),
                                            (date.getMinute() >= 10 ? "" : "0") + date.getMinute(),
                                            (date.getSecond() >= 10 ? "" : "0") + date.getSecond(),
                                            (milliSec >= 100 ? "" : (milliSec >= 10 ? "0" : "00")) + milliSec
                                    ));
                            ItemUtils.setLore(landItem, lore);
                            return LibraryPlugin.COMPAT.setNBT(landItem, "landId", land.getLandId());
                        }).toArray(ItemStack[]::new),
                prevGui);

        this.isAdmin = isAdmin;

        this.guiUserUuid = guiUserUuid;

        final ItemStack createItem = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(createItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.land.item.borrow")));
        this.createItem = ItemUtils.setGuiItem(createItem);

        final OfflinePlayer guiUser = Bukkit.getOfflinePlayer(guiUserUuid);
        final ItemStack myMapartsItem = ItemUtils.getSkullItem(guiUser);
        ItemUtils.setDisplayName(myMapartsItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.land.item.your")));
        this.myMapartsItem = ItemUtils.setGuiItem(myMapartsItem);

        final ItemStack collaboMapartsItem = new ItemStack(Material.getMaterial("CREEPER_HEAD"));
        ItemUtils.setDisplayName(collaboMapartsItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.land.item.others")));
        this.collaboMapartsItem = ItemUtils.setGuiItem(collaboMapartsItem);

        final OfflinePlayer owner = Bukkit.getOfflinePlayer(guiUserUuid);
        final ItemStack headItem = ItemUtils.getSkullItem(owner);
        ItemUtils.setDisplayName(headItem, "§a" + owner.getName());
        ItemUtils.setLore(headItem, Collections.singletonList(
                "§7UUID: " + owner.getUniqueId()
        ));
        this.headItem = ItemUtils.setGuiItem(headItem);
    }

    @Override
    protected Inventory getInventory() {
        final Inventory inv = super.getInventory();

        inv.setItem(4, this.headItem);

        if (this.player == this.guiUserUuid) {
            inv.setItem(48, this.createItem);
            if (!this.player.equals(this.guiUserUuid)) inv.setItem(51, this.myMapartsItem);
            inv.setItem(52, this.collaboMapartsItem);
        }

        return inv;
    }

    @Override
    public boolean onGuiClick(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return false;

        if (event.getCurrentItem().getType() == Material.getMaterial("PAPER") &&
                LibraryPlugin.COMPAT.containsNBT(event.getCurrentItem(), "landId")
        ) {
            final MapartLandDto land = MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final Long landId = LibraryPlugin.COMPAT.getNBTLong(event.getCurrentItem(), "landId");
                return landId != null ? MapartLandRepo.selectByLand(landId) : null;
            });
            if (land == null) {
                MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.notFound"));
            } else if ((land.getOwnerUUID().equals(this.player) && land.getStatus().equals("A")) || this.isAdmin) {
                GuiManager.singleton().setScreen(this.player, new MapartLandGui(this.player, land.getLandId(), this));
            } else if (Arrays.asList(land.getCollaboratorsUUID()).contains(this.player)) {
                if (MapartManager.getWorld() == null) {
                    MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("library.message.world.notFound"));
                } else {
                    MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("library.message.teleporting"));
                    MapartManager.teleportLand(land.getLandId(), this.player, false);
                }
            } else {
                MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("library.message.requirePerm"));
            }
            return true;
        } else if (this.createItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, new GuiMapartCreate(this.player, this));
            return true;
        } else if (this.myMapartsItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, this);
            return true;
        } else if (this.collaboMapartsItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, new GuiMapartCollaboPanel(this.player, this));
            return true;
        }
        return false;
    }
}
