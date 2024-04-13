package net.akazukin.mapart.gui;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.akazukin.library.compat.worldguard.WorldGuardCompat;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.ChestGuiBase;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.chest.GuiSizeSelector;
import net.akazukin.library.gui.screens.chest.YesOrNoGui;
import net.akazukin.library.gui.screens.chest.paged.GuiPagedMultiPlayerSelector;
import net.akazukin.library.gui.screens.sign.SignStringSelectorGui;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.StringUtils;
import net.akazukin.mapart.MapartPlugin;
import net.akazukin.mapart.doma.MapartSQLConfig;
import net.akazukin.mapart.doma.dto.MapartLandDto;
import net.akazukin.mapart.doma.entity.DMapartLandCollaborator;
import net.akazukin.mapart.doma.entity.MMapartLand;
import net.akazukin.mapart.doma.repo.DMapartLandCollaboratorRepo;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapartLandGui extends ChestGuiBase {
    private final int landId;

    private final SignStringSelectorGui nameSelector = new SignStringSelectorGui(player, this);
    private final GuiSizeSelector heightSelector;
    private final GuiSizeSelector widthSelector;
    private final ItemStack teleportLandItem;
    private final ItemStack removeLandItem;
    private final ItemStack cleanLandItem;
    private GuiPagedMultiPlayerSelector addCollaboGui = null;
    private GuiPagedMultiPlayerSelector removeCollaboGui = null;
    private final YesOrNoGui cleanLandGui;
    private final YesOrNoGui removeLandGui;
    private boolean isWaiting;

    private final ItemStack nameSelectorItem;
    private final ItemStack addCollaboGuiItem;
    private final ItemStack removeCollaboGuiItem;


    private final ItemStack heightItem;
    private final ItemStack widthItem;

    public MapartLandGui(final UUID player, final int landId, final GuiBase prevGui) {
        super(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.manage.main")),
                5, player, false, prevGui);
        this.landId = landId;
        final MMapartLand land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MMapartLandRepo.select(landId));
        if (land == null) {
            throw new IllegalStateException();
        }
        isWaiting = false;

        heightSelector = new GuiSizeSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.select.height")),
                player, 1, 2, 1, this);
        widthSelector = new GuiSizeSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.select.width")),
                player, 1, 2, 1, this);

        removeLandGui = new YesOrNoGui(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.land.delete")), player, this);
        cleanLandGui = new YesOrNoGui(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.land.clean")), player, this);


        final ItemStack nameItem = new ItemStack(Material.getMaterial("OAK_SIGN"));
        ItemUtils.setDisplayName(nameItem, "§eChange Name");
        this.nameSelectorItem = ItemUtils.setGuiItem(nameItem);

        final ItemStack heightItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(heightItem, "§eChange Height");
        this.heightItem = ItemUtils.setGuiItem(heightItem);

        final ItemStack widthItem = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(widthItem, "§eChange Width");
        this.widthItem = ItemUtils.setGuiItem(widthItem);

        final ItemStack addGuiItem = new ItemStack(Material.getMaterial("CHEST"));
        ItemUtils.setDisplayName(addGuiItem, "§eAdd collaborators (Online Only)");
        this.addCollaboGuiItem = ItemUtils.setGuiItem(addGuiItem);

        final ItemStack removeGuiItem = new ItemStack(Material.getMaterial("CHEST"));
        ItemUtils.setDisplayName(removeGuiItem, "§eRemove collaborators");
        this.removeCollaboGuiItem = ItemUtils.setGuiItem(removeGuiItem);

        final ItemStack teleportItem = new ItemStack(Material.getMaterial("ENDER_PEARL"));
        ItemUtils.setDisplayName(teleportItem, "§eTeleport land");
        this.teleportLandItem = ItemUtils.setGuiItem(teleportItem);

        final ItemStack removeItem = new ItemStack(Material.getMaterial("REDSTONE_BLOCK"));
        ItemUtils.setDisplayName(removeItem, "§eRemove land");
        this.removeLandItem = ItemUtils.setGuiItem(removeItem);

        final ItemStack cleanLandItem = new ItemStack(Material.getMaterial("TNT"));
        ItemUtils.setDisplayName(cleanLandItem, "§eClean land");
        this.cleanLandItem = ItemUtils.setGuiItem(cleanLandItem);
    }

    @Override
    protected Inventory getInventory() {

        final MapartLandDto land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                MapartLandRepo.selectByLand(landId));

        if (land == null || !land.getOwnerUUID().equals(player)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(MapartPlugin.getPlugin(), () ->
                    GuiManager.singleton().setScreen(player, new GuiMapartPanel(player)));
            return super.getInventory();
        }

        final String lines = StringUtils.join("", nameSelector.getResult());
        if (0 < StringUtils.getLength(lines) && StringUtils.getLength(lines) < 30) {
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand land_ = MMapartLandRepo.select(landId);

                land_.setName(lines);
                MMapartLandRepo.save(land_);
            });
        }

        if (addCollaboGui != null && addCollaboGui.isDone() && addCollaboGui.getSelectedPlayers().length != 0) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.land.collaborators.added"), StringUtils.join(", ", Arrays.stream(addCollaboGui.getSelectedPlayers()).map(OfflinePlayer::getName).collect(Collectors.toList())));
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                for (final OfflinePlayer selectedPlayer : addCollaboGui.getSelectedPlayers()) {
                    final DMapartLandCollaborator entity = new DMapartLandCollaborator();
                    entity.setLandId(landId);
                    entity.setCollaboratorUuid(selectedPlayer.getUniqueId());
                    DMapartLandCollaboratorRepo.save(entity);
                }
            });
            for (final OfflinePlayer selectedPlayer : addCollaboGui.getSelectedPlayers()) {
                WorldGuardCompat.addMember(MapartManager.getWorld(), "mapart-" + landId, selectedPlayer.getUniqueId());
            }
            addCollaboGui = null;
        }
        if (removeCollaboGui != null && removeCollaboGui.isDone() && removeCollaboGui.getSelectedPlayers().length != 0) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.land.collaborators.removed"), StringUtils.join(", ", Arrays.stream(removeCollaboGui.getSelectedPlayers()).map(OfflinePlayer::getName).collect(Collectors.toList())));
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                for (final OfflinePlayer selectedPlayer : removeCollaboGui.getSelectedPlayers()) {
                    final DMapartLandCollaborator entity = DMapartLandCollaboratorRepo.selectByLandAndCollaborator(landId, selectedPlayer.getUniqueId());
                    if (entity != null) DMapartLandCollaboratorRepo.delete(entity);
                }
            });
            for (final OfflinePlayer selectedPlayer : removeCollaboGui.getSelectedPlayers()) {
                WorldGuardCompat.removeMember(MapartManager.getWorld(), "mapart-" + landId, selectedPlayer.getUniqueId());
            }
            removeCollaboGui = null;
        }
        if (heightSelector.isDone() && heightSelector.getResult() != land.getHeight()) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.land.height.set"), heightSelector.getResult());
            Bukkit.getPlayer(player).sendMessage("Selected Height: " + heightSelector.getResult());
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand entity = MMapartLandRepo.select(landId);

                entity.setHeight(heightSelector.getResult());
                MMapartLandRepo.save(entity);
            });
            heightSelector.setDefaultSize(heightSelector.getResult());
            heightSelector.reset();

            final ProtectedRegion rg = WorldGuardCompat.getRegion(MapartManager.getWorld(), "mapart-" + landId);
            rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())).forEach(player -> MapartManager.teleportLand(landId, player.getUniqueId()));
        }
        if (widthSelector.isDone() && widthSelector.getResult() != land.getWidth()) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.land.width.set"), widthSelector.getResult());
            MapartSQLConfig.singleton().getTransactionManager().required(() -> {
                final MMapartLand entity = MMapartLandRepo.select(landId);

                entity.setWidth(widthSelector.getResult());
                MMapartLandRepo.save(entity);
            });
            widthSelector.setDefaultSize(widthSelector.getResult());
            widthSelector.reset();

            final ProtectedRegion rg = WorldGuardCompat.getRegion(MapartManager.getWorld(), "mapart-" + landId);
            rg.getMembers().getUniqueIds().stream().map(Bukkit::getPlayer).filter(player -> player != null && rg.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())).forEach(player -> MapartManager.teleportLand(landId, player.getUniqueId()));
        }
        if (removeLandGui.getResult() != null && removeLandGui.getResult()) {
            removeLandGui.reset();

            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.land.removing"));

            MapartManager.deleteLand(landId, () ->
                    MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.land.removed")));
            Bukkit.getScheduler().scheduleSyncDelayedTask(MapartPlugin.getPlugin(), () ->
                    GuiManager.singleton().setScreen(player, new GuiMapartPanel(player)));
            return super.getInventory();
        }
        if (cleanLandGui.getResult() != null && cleanLandGui.getResult()) {
            cleanLandGui.reset();

            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.land.cleaning"));

            MapartManager.cleanLand(landId, () ->
                    MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.land.cleaned")));
            Bukkit.getScheduler().scheduleSyncDelayedTask(MapartPlugin.getPlugin(), () ->
                    GuiManager.singleton().setScreen(player, new GuiMapartPanel(player)));
            return super.getInventory();
        }

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, MessageHelper.getLocale(player));
        InventoryUtils.fillCloseItem(inv, MessageHelper.getLocale(player));
        InventoryUtils.fillBackItem(inv, MessageHelper.getLocale(player));


        inv.setItem(10, teleportLandItem);
        inv.setItem(12, nameSelectorItem);
        inv.setItem(14, addCollaboGuiItem);
        inv.setItem(15, removeCollaboGuiItem);

        inv.setItem(21, heightItem);
        inv.setItem(22, widthItem);
        inv.setItem(24, cleanLandItem);
        inv.setItem(25, removeLandItem);

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
        inv.setItem(4, landItem);

        return inv;
    }

    @Override
    protected void onGuiOpen(final InventoryOpenEvent event) {
        if (isWaiting &&
                (StringUtils.getLength(StringUtils.getIndex(nameSelector.getResult(), 0)) <= 0 || StringUtils.getIndex(nameSelector.getResult(), 0).equalsIgnoreCase("cancel rename!"))) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(event.getPlayer(), I18n.of("mapart.panel.name.cancel"));
        }
        isWaiting = false;
    }

    @Override
    public boolean onGuiClick(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return false;
        if (nameSelectorItem.equals(event.getCurrentItem())) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("mapart.panel.name.message"));
            isWaiting = true;
            GuiManager.singleton().setScreen(player, nameSelector);
            return true;
        } else if (addCollaboGuiItem.equals(event.getCurrentItem())) {
            final MapartLandDto land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                    MapartLandRepo.selectByLand(landId));
            addCollaboGui = new GuiPagedMultiPlayerSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.manage.collabo.add")),
                    6, 6, player, Arrays.stream(Bukkit.getOfflinePlayers()).filter(online -> !land.getOwnerUUID().equals(online.getUniqueId()) && !Arrays.asList(land.getCollaboratorsUUID()).contains(online.getUniqueId())).toArray(OfflinePlayer[]::new), this);
            GuiManager.singleton().setScreen(player, addCollaboGui);
            return true;
        } else if (removeCollaboGuiItem.equals(event.getCurrentItem())) {
            final MapartLandDto land = MapartSQLConfig.singleton().getTransactionManager().required(() ->
                    MapartLandRepo.selectByLand(landId));
            removeCollaboGui = new GuiPagedMultiPlayerSelector(MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.panel.gui.manage.collabo.remove")),
                    6, 6, player, Arrays.stream(land.getCollaboratorsUUID()).filter(collabo -> !land.getOwnerUUID().equals(collabo) && Arrays.asList(land.getCollaboratorsUUID()).contains(collabo)).map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new), this);
            GuiManager.singleton().setScreen(player, removeCollaboGui);
            return true;
        } else if (cleanLandItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(player, cleanLandGui);
            return true;
        } else if (removeLandItem.equals(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(player, removeLandGui);
            return true;
        } else if (teleportLandItem.equals(event.getCurrentItem())) {
            MapartPlugin.MESSAGE_HELPER.sendMessage(player, I18n.of("library.message.teleporting"));
            final Player p = Bukkit.getPlayer(player);
            p.closeInventory();

            MapartManager.teleportLand(landId, player);
        }
        return false;
    }
}
