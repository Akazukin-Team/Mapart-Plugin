package net.akazukin.mapart.gui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.ChestGuiBase;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.GuiSizeSelector;
import net.akazukin.library.gui.screens.chest.YesOrNoGui;
import net.akazukin.library.gui.screens.chest.paged.GuiPagedMultiPlayerSelector;
import net.akazukin.library.gui.screens.sign.SignStringSelectorGui;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ArrayUtils;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.StringUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dto.MapartLandDto;
import net.akazukin.mapart.doma.entity.MMapartLand;
import net.akazukin.mapart.doma.repo.MMapartLandRepo;
import net.akazukin.mapart.doma.repo.MapartLandRepo;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MapartLandGui extends ChestGuiBase {
    private final int landId;

    private final SignStringSelectorGui nameSelector = new SignStringSelectorGui(this.player, this);
    private final GuiSizeSelector heightSelector;
    private final GuiSizeSelector widthSelector;
    private final ItemStack teleportLandItem;
    private final ItemStack removeLandItem;
    private final ItemStack cleanLandItem;
    private final YesOrNoGui cleanLandGui;
    private final YesOrNoGui removeLandGui;
    private final ItemStack nameSelectorItem;
    private final ItemStack addCollaboGuiItem;
    private final ItemStack removeCollaboGuiItem;
    private final ItemStack heightItem;
    private final ItemStack widthItem;
    private GuiPagedMultiPlayerSelector addCollaboGui = null;
    private GuiPagedMultiPlayerSelector removeCollaboGui = null;
    private boolean isWaiting;

    public MapartLandGui(final UUID player, final int landId, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.manage.main")),
                5, player, false, prevGui);
        this.landId = landId;
        final MMapartLand land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MMapartLandRepo.selectByOwner(landId));
        if (land == null) {
            throw new IllegalStateException();
        }
        this.isWaiting = false;

        this.heightSelector = new GuiSizeSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.select.height")),
                player, 1, 2, 1, this);
        this.widthSelector = new GuiSizeSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.select.width")),
                player, 1, 2, 1, this);

        this.removeLandGui = new YesOrNoGui(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of(
                "mapart.panel.gui.land.delete")), player, this);
        this.cleanLandGui = new YesOrNoGui(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of(
                "mapart.panel.gui.land.clean")), player, this);


        final ItemStack nameItem = new ItemStack(Material.getMaterial("OAK_SIGN"));
        ItemUtils.setDisplayName(nameItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of(
                "mapart.panel.gui.land.item.name")));
        this.nameSelectorItem = ItemUtils.setGuiItem(nameItem);

        final ItemStack heightItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(heightItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.height")));
        this.heightItem = ItemUtils.setGuiItem(heightItem);

        final ItemStack widthItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(widthItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of(
                "mapart.panel.gui.land.item.width")));
        this.widthItem = ItemUtils.setGuiItem(widthItem);

        final ItemStack addGuiItem = new ItemStack(Material.getMaterial("CHEST"));
        ItemUtils.setDisplayName(addGuiItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.collaborators.add")));
        this.addCollaboGuiItem = ItemUtils.setGuiItem(addGuiItem);

        final ItemStack removeGuiItem = new ItemStack(Material.getMaterial("CHEST"));
        ItemUtils.setDisplayName(removeGuiItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.collaborators.remove")));
        this.removeCollaboGuiItem = ItemUtils.setGuiItem(removeGuiItem);

        final ItemStack teleportItem = new ItemStack(Material.getMaterial("ENDER_PEARL"));
        ItemUtils.setDisplayName(teleportItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.teleport")));
        this.teleportLandItem = ItemUtils.setGuiItem(teleportItem);

        final ItemStack removeItem = new ItemStack(Material.getMaterial("REDSTONE_BLOCK"));
        ItemUtils.setDisplayName(removeItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.delete")));
        this.removeLandItem = ItemUtils.setGuiItem(removeItem);

        final ItemStack cleanLandItem = new ItemStack(Material.getMaterial("TNT"));
        ItemUtils.setDisplayName(cleanLandItem, MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.clean")));
        this.cleanLandItem = ItemUtils.setGuiItem(cleanLandItem);
    }

    @Override
    protected Inventory getInventory() {
        final MapartLandDto land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MapartLandRepo.selectByLand(this.landId));

        if (land == null || !land.getOwnerUUID().equals(this.player)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(MapartPlugin.getPlugin(), () ->
                    GuiManager.singleton().setScreen(this.player, () -> this.prevGui));
            return super.getInventory();
        }

        final MapartManager mgr = MapartManager.singleton(land.getSize());

        final String lines = ArrayUtils.join("", this.nameSelector.getResult());
        if (0 < StringUtils.getLength(lines) && StringUtils.getLength(lines) < 30) {
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand land_ = MMapartLandRepo.selectByOwner(this.landId);

                land_.setName(lines);
                MMapartLandRepo.save(land_);
            });
        }

        if (this.addCollaboGui != null && this.addCollaboGui.isDone() && this.addCollaboGui.getSelectedPlayers().length != 0) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.collaborators.added"),
                    ArrayUtils.join(", ",
                            Arrays.stream(this.addCollaboGui.getSelectedPlayers()).map(OfflinePlayer::getName).collect(Collectors.toList())));

            MapartManager.addCollaborator(this.landId,
                    Arrays.stream(this.removeCollaboGui.getSelectedPlayers())
                            .map(OfflinePlayer::getUniqueId)
                            .toArray(UUID[]::new));
            this.addCollaboGui = null;
        }
        if (this.removeCollaboGui != null && this.removeCollaboGui.isDone() && this.removeCollaboGui.getSelectedPlayers().length != 0) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.collaborators.removed"),
                    ArrayUtils.join(", ",
                            Arrays.stream(this.removeCollaboGui.getSelectedPlayers()).map(OfflinePlayer::getName).collect(Collectors.toList())));

            MapartManager.removeCollaborator(this.landId,
                    Arrays.stream(this.removeCollaboGui.getSelectedPlayers())
                            .map(OfflinePlayer::getUniqueId)
                            .toArray(UUID[]::new));
            this.removeCollaboGui = null;
        }
        /*if (this.heightSelector.isDone() && this.heightSelector.getResult() != land.getHeight()) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.height.set"),
                    this.heightSelector.getResult());
            Bukkit.getPlayer(this.player).sendMessage("Selected Height: " + this.heightSelector.getResult());
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand entity = MMapartLandRepo.select(this.landId);

                entity.setHeight(this.heightSelector.getResult());
                MMapartLandRepo.save(entity);
            });
            this.heightSelector.setDefaultSize(this.heightSelector.getResult());
            this.heightSelector.reset();

            final ProtectedRegion rg = WorldGuardCompat.getRegion(MapartManager.getWorld(), "mapart-" + this.landId);
            rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg
            .contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation()
            .getBlockZ())).forEach(player -> MapartManager.teleportLand(this.landId, player.getUniqueId(), false));
        }
        if (this.widthSelector.isDone() && this.widthSelector.getResult() != land.getWidth()) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.width.set"),
                    this.widthSelector.getResult());
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand entity = MMapartLandRepo.select(this.landId);

                entity.setWidth(this.widthSelector.getResult());
                MMapartLandRepo.save(entity);
            });
            this.widthSelector.setDefaultSize(this.widthSelector.getResult());
            this.widthSelector.reset();

            final ProtectedRegion rg = WorldGuardCompat.getRegion(MapartManager.getWorld(), "mapart-" + this.landId);
            rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg
            .contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation()
            .getBlockZ())).forEach(player -> MapartManager.teleportLand(this.landId, player.getUniqueId(), false));
        }*/
        if (this.removeLandGui.getResult() != null && this.removeLandGui.getResult()) {
            this.removeLandGui.reset();

            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.removing"));

            mgr.deleteLand(land.getLocationId(), () ->
                    MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.removed")));

            GuiManager.singleton().setScreen(this.player, () -> this.prevGui);

            return super.getInventory();
        }
        if (this.cleanLandGui.getResult() != null && this.cleanLandGui.getResult()) {
            this.cleanLandGui.reset();

            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.cleaning"));

            mgr.cleanLand(land.getLocationId(), () ->
                    MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.land.cleaned")));
            Bukkit.getScheduler().scheduleSyncDelayedTask(MapartPlugin.getPlugin(), () ->
                    GuiManager.singleton().setScreen(this.player, () -> this.prevGui));
            return super.getInventory();
        }

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, MessageHelper.getLocale(this.player));
        InventoryUtils.fillCloseItem(inv, MessageHelper.getLocale(this.player));
        InventoryUtils.fillBackItem(inv, MessageHelper.getLocale(this.player));


        inv.setItem(10, this.teleportLandItem);
        inv.setItem(12, this.nameSelectorItem);
        inv.setItem(14, this.addCollaboGuiItem);
        inv.setItem(15, this.removeCollaboGuiItem);

        inv.setItem(21, this.heightItem);
        inv.setItem(22, this.widthItem);
        inv.setItem(24, this.cleanLandItem);
        inv.setItem(25, this.removeLandItem);

        final OfflinePlayer owner = Bukkit.getOfflinePlayer(land.getOwnerUUID());

        final ItemStack landItem = new ItemStack(Material.getMaterial("PAPER"));
        ItemUtils.setDisplayName(landItem,
                StringUtils.getColoredString(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.lands.info.name"), land.getName())));
        final List<String> lore = new ArrayList<>(Arrays.asList(
                MapartPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.lands.info.owner"),
                        owner.getName()
                ),
                MapartPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.lands.info.landId"),
                        land.getLandId()
                )
        ));

        if (land.getCollaboratorsUUID().length != 0)
            lore.add("");
        lore.add(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(this.player), I18n.of("mapart.panel.lands" +
                ".info.collaborators"), land.getCollaboratorsUUID().length));
        lore.addAll(Arrays.stream(land.getCollaboratorsUUID()).parallel()
                .map(uuid -> {
                    final OfflinePlayer collabo = Bukkit.getOfflinePlayer(uuid);
                    return MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(this.player), I18n.of("mapart" +
                            ".panel.lands" +
                            ".info.collaborator"), collabo.getName());
                }).collect(Collectors.toList()));
        if (land.getCollaboratorsUUID().length != 0)
            lore.add("");

        final LocalDateTime date = land.getCreatedDate().toLocalDateTime();
        final int milliSec = date.getNano() / 1000000;
        lore.add(
                MapartPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(this.player),
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
        inv.setItem(4, landItem);

        return inv;
    }

    @Override
    public boolean onGuiClick(final InventoryClickEvent event) {
        final MapartLandDto land = MapartManager.getLandData(this.landId);
        final MapartManager mgr = MapartManager.singleton(land.getSize());

        if (event.getCurrentItem() == null) return false;
        if (this.nameSelectorItem.equals(event.getCurrentItem())) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("mapart.panel.name.message"));
            this.isWaiting = true;
            GuiManager.singleton().setScreen(this.player, () -> this.nameSelector);
            return true;
        } else if (this.addCollaboGuiItem.equals(event.getCurrentItem())) {
            this.addCollaboGui =
                    new GuiPagedMultiPlayerSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(this.player), I18n.of("mapart.panel.gui.manage.collaborators.add")),
                            6, 6, this.player,
                            Arrays.stream(Bukkit.getOfflinePlayers()).filter(online -> !land.getOwnerUUID().equals(online.getUniqueId()) && !Objects.equals(land.getCollaboratorsUUID(), online.getUniqueId())).toArray(OfflinePlayer[]::new), this);
            GuiManager.singleton().setScreen(this.player, () -> this.addCollaboGui);
            return true;
        } else if (this.removeCollaboGuiItem.equals(event.getCurrentItem())) {
            this.removeCollaboGui =
                    new GuiPagedMultiPlayerSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(this.player), I18n.of("mapart.panel.gui.manage.collaborators.remove")),
                            6, 6, this.player,
                            Arrays.stream(land.getCollaboratorsUUID()).parallel()
                                    .filter(collabo -> !land.getOwnerUUID().equals(collabo) && Objects.equals(land.getCollaboratorsUUID(), collabo))
                                    .map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new), this);
            GuiManager.singleton().setScreen(this.player, () -> this.removeCollaboGui);
            return true;
        } else if (this.cleanLandItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.cleanLandGui);
            return true;
        } else if (this.removeLandItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.removeLandGui);
            return true;
        } else if (this.teleportLandItem.equals(event.getCurrentItem())) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(this.player, I18n.of("library.message.teleporting"));
            final Player p = Bukkit.getPlayer(this.player);
            p.closeInventory();

            mgr.teleportLand(land.getLocationId(), this.player, false);
        }
        return false;
    }

    @Override
    protected void onGuiOpen(final InventoryOpenEvent event) {
        if (this.isWaiting &&
                (StringUtils.getLength(ArrayUtils.getIndex(this.nameSelector.getResult(), 0)) <= 0 || ArrayUtils.getIndex(this.nameSelector.getResult(), 0).equalsIgnoreCase("cancel rename!"))) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(event.getPlayer(), I18n.of("mapart.panel.name.cancel"));
        }
        this.isWaiting = false;
    }
}
