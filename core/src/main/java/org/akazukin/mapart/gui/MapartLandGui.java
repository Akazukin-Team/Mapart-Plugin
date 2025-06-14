package org.akazukin.mapart.gui;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.akazukin.i18n.I18n;
import org.akazukin.library.compat.worldguard.WorldGuardCompat;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.ChestGuiBase;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.gui.screens.chest.GuiSizeSelector;
import org.akazukin.library.gui.screens.chest.YesOrNoGui;
import org.akazukin.library.gui.screens.chest.paged.GuiPagedMultiPlayerSelector;
import org.akazukin.library.gui.screens.sign.SignStringSelectorGui;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.library.utils.ArrayUtils;
import org.akazukin.library.utils.InventoryUtils;
import org.akazukin.library.utils.ItemUtils;
import org.akazukin.library.utils.StringUtils;
import org.akazukin.mapart.MapartPlugin;
import org.akazukin.mapart.doma.MapartSQLConfig;
import org.akazukin.mapart.doma.dto.MapartLandDto;
import org.akazukin.mapart.doma.entity.MMapartLand;
import org.akazukin.mapart.doma.repo.MMapartLandRepo;
import org.akazukin.mapart.doma.repo.MapartLandRepo;
import org.akazukin.mapart.manager.mapart.MapartManager;
import org.akazukin.mapart.manager.mapart.MapartWorldListener;
import org.akazukin.util.utils.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class MapartLandGui extends ChestGuiBase {
    private final long landId;

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
    private GuiPagedMultiPlayerSelector addCollaboGui;
    private GuiPagedMultiPlayerSelector removeCollaboGui;
    private boolean isWaiting;

    public MapartLandGui(final Player player, final long landId, final GuiBase prevGui) {
        super(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                        I18n.of("mapart.panel.gui.manage.main")),
                5, player, false, prevGui);
        this.landId = landId;
        final MMapartLand land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MMapartLandRepo.selectByLand(landId));
        if (land == null) {
            throw new IllegalStateException();
        }
        this.isWaiting = false;

        this.heightSelector =
                new GuiSizeSelector(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                        I18n.of("mapart.panel.gui.select.height")),
                        player, 1, (int) land.getSize(), land.getHeight(), this);
        this.widthSelector = new GuiSizeSelector(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.select.width")),
                player, 1, (int) land.getSize(), land.getWidth(), this);

        this.removeLandGui = new YesOrNoGui(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of(
                        "mapart.panel.gui.land.delete")), player, this);
        this.cleanLandGui = new YesOrNoGui(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of(
                        "mapart.panel.gui.land.clean")), player, this);


        final ItemStack nameItem = new ItemStack(Material.getMaterial("OAK_SIGN"));
        ItemUtils.setDisplayName(nameItem, MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of(
                        "mapart.panel.gui.land.item.name")));
        this.nameSelectorItem = ItemUtils.setGuiItem(nameItem);

        final ItemStack heightItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(heightItem, MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.height")));
        this.heightItem = ItemUtils.setGuiItem(heightItem);

        final ItemStack widthItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(widthItem, MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of(
                        "mapart.panel.gui.land.item.width")));
        this.widthItem = ItemUtils.setGuiItem(widthItem);

        final ItemStack addGuiItem = new ItemStack(Material.getMaterial("CHEST"));
        ItemUtils.setDisplayName(addGuiItem, MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.collaborators.add")));
        this.addCollaboGuiItem = ItemUtils.setGuiItem(addGuiItem);

        final ItemStack removeGuiItem = new ItemStack(Material.getMaterial("CHEST"));
        ItemUtils.setDisplayName(removeGuiItem, MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.collaborators.remove")));
        this.removeCollaboGuiItem = ItemUtils.setGuiItem(removeGuiItem);

        final ItemStack teleportItem = new ItemStack(Material.getMaterial("ENDER_PEARL"));
        ItemUtils.setDisplayName(teleportItem, MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.teleport")));
        this.teleportLandItem = ItemUtils.setGuiItem(teleportItem);

        final ItemStack removeItem = new ItemStack(Material.getMaterial("REDSTONE_BLOCK"));
        ItemUtils.setDisplayName(removeItem, MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.delete")));
        this.removeLandItem = ItemUtils.setGuiItem(removeItem);

        final ItemStack cleanLandItem = new ItemStack(Material.getMaterial("TNT"));
        ItemUtils.setDisplayName(cleanLandItem, MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.panel.gui.land.item.clean")));
        this.cleanLandItem = ItemUtils.setGuiItem(cleanLandItem);
    }

    @Override
    protected Inventory getInventory() {
        final MapartLandDto land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MapartLandRepo.selectByLand(this.landId));

        if (land == null || !land.getOwnerUUID().equals(this.player.getUniqueId())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(MapartPlugin.getPlugin(), () ->
                    GuiManager.singleton().setScreen(this.player, () -> this.prevGui));
            return super.getInventory();
        }

        final MapartManager mgr = MapartManager.singleton(land.getSize());

        final String lines = org.akazukin.util.utils.ArrayUtils.join("", this.nameSelector.getResult());
        if (0 < org.akazukin.util.utils.StringUtils.getLength(lines) && org.akazukin.util.utils.StringUtils.getLength(lines) < 30) {
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand land_ = MMapartLandRepo.selectByLand(this.landId);

                land_.setName(lines);
                MMapartLandRepo.save(land_);
            });
        }

        if (this.addCollaboGui != null && this.addCollaboGui.isDone() && this.addCollaboGui.getSelectedPlayers().length != 0) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.land.collaborators.added",
                    ListUtils.join(", ",
                            Arrays.stream(this.addCollaboGui.getSelectedPlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()))));

            MapartManager.addCollaborator(this.landId,
                    Arrays.stream(this.addCollaboGui.getSelectedPlayers())
                            .map(OfflinePlayer::getUniqueId)
                            .toArray(UUID[]::new));
            this.addCollaboGui = null;
        }
        if (this.removeCollaboGui != null && this.removeCollaboGui.isDone() && this.removeCollaboGui.getSelectedPlayers().length != 0) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.land.collaborators.removed",
                    ListUtils.join(", ",
                            Arrays.stream(this.removeCollaboGui.getSelectedPlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()))));

            MapartManager.removeCollaborator(this.landId,
                    Arrays.stream(this.removeCollaboGui.getSelectedPlayers())
                            .map(OfflinePlayer::getUniqueId)
                            .toArray(UUID[]::new));
            this.removeCollaboGui = null;
        }
        if (this.widthSelector.isDone() && this.widthSelector.getResult() != land.getWidth()) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.land.width.set",
                    this.widthSelector.getResult()));
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand entity = MMapartLandRepo.selectByLand(this.landId);

                entity.setWidth(this.widthSelector.getResult());
                MMapartLandRepo.save(entity);
            });
            this.widthSelector.setDefaultSize(this.widthSelector.getResult());
            this.widthSelector.reset();

            final ProtectedRegion rg = WorldGuardCompat.getRegion(mgr.getWorld(), "mapart-" + land.getLocationId());
            rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer)
                    .filter(player -> player != null &&
                            rg.contains(
                                    player.getLocation().getBlockX(),
                                    player.getLocation().getBlockY(),
                                    player.getLocation().getBlockZ()))
                    .forEach(player -> mgr.teleportLand(land.getLocationId(), player, true));
        }
        if (this.heightSelector.isDone() && this.heightSelector.getResult() != land.getHeight()) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.land.height.set",
                    this.heightSelector.getResult()));
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand entity = MMapartLandRepo.selectByLand(this.landId);

                entity.setHeight(this.heightSelector.getResult());
                MMapartLandRepo.save(entity);
            });
            this.heightSelector.setDefaultSize(this.heightSelector.getResult());
            this.heightSelector.reset();

            final ProtectedRegion rg = WorldGuardCompat.getRegion(mgr.getWorld(), "mapart-" + land.getLocationId());
            rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg
                    .contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation()
                            .getBlockZ())).forEach(player -> mgr.teleportLand(land.getLocationId(), player, true));
        }
        final boolean isOwner = MapartManager.getLandData(this.landId).getOwnerUUID().equals(this.player.getUniqueId());
        if (this.removeLandGui.getResult() != null && this.removeLandGui.getResult()) {
            this.removeLandGui.reset();
            if (!isOwner || MapartManager.canRemove(this.player.getUniqueId())) {
                if (isOwner) {
                    MapartManager.CLEANING.add(this.player.getUniqueId());
                }

                MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.land.removing"));

                final ProtectedRegion rg = WorldGuardCompat.getRegion(mgr.getWorld(), "mapart-" + land.getLocationId());
                rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg
                        .contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(),
                                player.getLocation()
                                        .getBlockZ())).forEach(player -> {
                    if (!MapartWorldListener.teleportLastPos(player)) {
                        if (player.getBedSpawnLocation() != null) {
                            player.teleport(player.getBedSpawnLocation());
                        } else {
                            player.damage(Double.MAX_VALUE);
                        }
                    }
                });

                mgr.deleteLand(land.getLocationId(), () -> {
                    if (isOwner) {
                        MapartManager.CLEANING.remove(this.player.getUniqueId());
                    }
                    if (isOwner) {
                        MapartManager.LAST_DELETED.put(this.player.getUniqueId(), System.currentTimeMillis());
                    }
                    MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.land.removed"));
                });

                GuiManager.singleton().setScreen(this.player, () -> this.prevGui);

                return super.getInventory();
            }
        }
        if (this.cleanLandGui.getResult() != null && this.cleanLandGui.getResult()) {
            this.cleanLandGui.reset();
            if (!isOwner || MapartManager.canRemove(this.player.getUniqueId())) {
                if (isOwner) {
                    MapartManager.CLEANING.add(this.player.getUniqueId());
                }

                MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.land.cleaning"));
                mgr.cleanLand(land.getLocationId(), () -> {
                    if (isOwner) {
                        MapartManager.CLEANING.remove(this.player.getUniqueId());
                    }
                    if (isOwner) {
                        MapartManager.LAST_DELETED.put(this.player.getUniqueId(), System.currentTimeMillis());
                    }
                    MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.land.cleaned"));
                });

                Bukkit.getScheduler().scheduleSyncDelayedTask(MapartPlugin.getPlugin(), () ->
                        GuiManager.singleton().setScreen(this.player, () -> this.prevGui));
                return super.getInventory();
            }
        }

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, BukkitMessageHelper.getLocale(this.player));
        InventoryUtils.fillCloseItem(inv, BukkitMessageHelper.getLocale(this.player));
        InventoryUtils.fillBackItem(inv, BukkitMessageHelper.getLocale(this.player));


        inv.setItem(10, this.teleportLandItem);
        inv.setItem(12, this.nameSelectorItem);
        inv.setItem(14, this.addCollaboGuiItem);
        inv.setItem(15, this.removeCollaboGuiItem);

        //inv.setItem(21, this.heightItem);
        //inv.setItem(22, this.widthItem);
        inv.setItem(24, this.cleanLandItem);
        inv.setItem(25, this.removeLandItem);

        final OfflinePlayer owner = Bukkit.getOfflinePlayer(land.getOwnerUUID());

        final ItemStack landItem = new ItemStack(Material.getMaterial("PAPER"));
        ItemUtils.setDisplayName(landItem,
                StringUtils.getColoredString(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.lands.info.name", land.getName()))));
        final List<String> lore = new ArrayList<>(Arrays.asList(
                MapartPlugin.getPlugin().getMessageHelper().get(
                        BukkitMessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.lands.info.owner", owner.getName())
                ),
                MapartPlugin.getPlugin().getMessageHelper().get(
                        BukkitMessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.lands.info.landId", land.getLandId())
                )
        ));

        if (land.getCollaboratorsUUID().length != 0) {
            lore.add("");
        }
        lore.add(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player), I18n.of("mapart.panel.lands.info.collaborators", land.getCollaboratorsUUID().length)));
        lore.addAll(Arrays.stream(land.getCollaboratorsUUID()).parallel()
                .map(uuid -> {
                    final OfflinePlayer collabo = Bukkit.getOfflinePlayer(uuid);
                    return MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player),
                            I18n.of("mapart.panel.lands.info.collaborator", collabo.getName()));
                }).collect(Collectors.toList()));
        if (land.getCollaboratorsUUID().length != 0) {
            lore.add("");
        }

        final LocalDateTime date = land.getCreatedDate().toLocalDateTime();
        final int milliSec = date.getNano() / 1000000;
        lore.add(
                MapartPlugin.getPlugin().getMessageHelper().get(
                        BukkitMessageHelper.getLocale(this.player),
                        I18n.of("mapart.panel.lands.info.createdAt",
                                date.getYear(),
                                (date.getMonthValue() >= 10 ? "" : "0") + date.getMonthValue(),
                                (date.getDayOfMonth() >= 10 ? "" : "0") + date.getDayOfMonth(),

                                (date.getHour() >= 10 ? "" : "0") + date.getHour(),
                                (date.getMinute() >= 10 ? "" : "0") + date.getMinute(),
                                (date.getSecond() >= 10 ? "" : "0") + date.getSecond(),
                                (milliSec >= 100 ? "" : (milliSec >= 10 ? "0" : "00")) + milliSec
                        )));
        ItemUtils.setLore(landItem, lore);
        inv.setItem(4, landItem);

        return inv;
    }

    @Override
    public boolean onGuiClick(final InventoryClickEvent event) {
        final MapartLandDto land = MapartManager.getLandData(this.landId);
        final MapartManager mgr = MapartManager.singleton(land.getSize());

        if (event.getCurrentItem() == null) {
            return false;
        }
        if (this.nameSelectorItem.equals(event.getCurrentItem())) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(this.player, I18n.of("mapart.panel.name.message"));
            this.isWaiting = true;
            GuiManager.singleton().setScreen(this.player, () -> this.nameSelector);
            return true;
        } else if (this.addCollaboGuiItem.equals(event.getCurrentItem())) {
            this.addCollaboGui =
                    new GuiPagedMultiPlayerSelector(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player), I18n.of("mapart.panel.gui.manage.collaborators.add")),
                            6, 6, this.player,
                            Arrays.stream(Bukkit.getOfflinePlayers())
                                    .filter(online -> !land.getOwnerUUID().equals(online.getUniqueId()) &&
                                            !Arrays.asList(land.getCollaboratorsUUID()).contains(online.getUniqueId()))
                                    .toArray(OfflinePlayer[]::new), this);
            GuiManager.singleton().setScreen(this.player, () -> this.addCollaboGui);
            return true;
        } else if (this.removeCollaboGuiItem.equals(event.getCurrentItem())) {
            this.removeCollaboGui =
                    new GuiPagedMultiPlayerSelector(MapartPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player), I18n.of("mapart.panel.gui.manage.collaborators.remove")),
                            6, 6, this.player,
                            Arrays.stream(land.getCollaboratorsUUID()).parallel()
                                    .filter(collabo -> !land.getOwnerUUID().equals(collabo) && Arrays.asList(land.getCollaboratorsUUID()).contains(collabo))
                                    .map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new), this);
            GuiManager.singleton().setScreen(this.player, () -> this.removeCollaboGui);
            return true;
        } else if (this.cleanLandItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.cleanLandGui);
            return true;
        } else if (this.removeLandItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.removeLandGui);
            return true;
        } else if (this.widthItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.widthSelector);
            return true;
        } else if (this.heightItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(this.player, () -> this.heightSelector);
            return true;
        } else if (this.teleportLandItem.equals(event.getCurrentItem())) {
            this.player.closeInventory();
            mgr.teleportLand(land.getLocationId(), this.player, false);
        }
        return false;
    }

    @Override
    protected void onGuiOpen(final InventoryOpenEvent event) {
        if (this.isWaiting &&
                (org.akazukin.util.utils.StringUtils.getLength(ArrayUtils.getIndex(this.nameSelector.getResult(), 0)) <= 0 || ArrayUtils.getIndex(this.nameSelector.getResult(), 0).equalsIgnoreCase("cancel rename!"))) {
            MapartPlugin.getPlugin().getMessageHelper().sendMessage(event.getPlayer(), I18n.of("mapart.panel.name.cancel"));
        }
        this.isWaiting = false;
    }
}
