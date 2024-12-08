package org.akazukin.mapart.manager;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.event.EventTarget;
import org.akazukin.library.event.Listenable;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.library.utils.ItemUtils;
import org.akazukin.library.utils.UUIDUtils;
import org.akazukin.mapart.MapartPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CopyrightManager implements Listenable {
    public static ItemStack setCopyright(final ItemStack itemStack, final Player player) {
        final String lore = MapartPlugin.MESSAGE_HELPER.get(BukkitMessageHelper.getLocale(player),
                I18n.of("mapart.copyright.lore", player.getName()));
        ItemStack item = LibraryPlugin.COMPAT.setPlData(itemStack, "AKZ_MAPART_COPYRIGHT_OWNER",
                String.valueOf(player.getUniqueId()));
        item = LibraryPlugin.COMPAT.setPlData(item, "AKZ_MAPART_COPYRIGHT_LORE", lore);
        final List<String> lores = ItemUtils.getLore(itemStack);
        lores.add(lore);
        ItemUtils.setLore(item, lores);
        return item;
    }

    @Nullable
    public static ItemStack removeCopyright(final ItemStack itemStack) {
        if (!LibraryPlugin.COMPAT.containsPlData(itemStack, "AKZ_MAPART_COPYRIGHT_LORE") ||
                !LibraryPlugin.COMPAT.containsPlData(itemStack, "AKZ_MAPART_COPYRIGHT_OWNER")) {
            return null;
        }

        final List<String> lores = ItemUtils.getLore(itemStack);
        if (lores.contains(LibraryPlugin.COMPAT.getPlDataString(itemStack, "AKZ_MAPART_COPYRIGHT_LORE"))) {
            lores.remove(LibraryPlugin.COMPAT.getPlDataString(itemStack, "AKZ_MAPART_COPYRIGHT_LORE"));
            ItemStack item = LibraryPlugin.COMPAT.removePlData(itemStack, "AKZ_MAPART_COPYRIGHT_LORE");
            item = LibraryPlugin.COMPAT.removePlData(item, "AKZ_MAPART_COPYRIGHT_OWNER");
            ItemUtils.setLore(item, lores);
            return item;
        }
        return null;
    }

    @EventTarget(bktPriority = org.akazukin.library.event.EventPriority.HIGH, ignoreSuperClasses = false)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof org.bukkit.inventory.CartographyInventory) {
            final ItemStack result = event.getInventory().getItem(2);
            if (result != null && hasCopyright(result) && !isOwner(result, event.getWhoClicked().getUniqueId())) {
                event.getView().setItem(2, null);
            }
        }
    }

    public static boolean hasCopyright(final ItemStack itemStack) {
        return LibraryPlugin.COMPAT.containsPlData(itemStack, "AKZ_MAPART_COPYRIGHT_OWNER") && LibraryPlugin.COMPAT.containsPlData(itemStack, "AKZ_MAPART_COPYRIGHT_LORE") &&
                UUIDUtils.isUUID(LibraryPlugin.COMPAT.getPlDataString(itemStack, "AKZ_MAPART_COPYRIGHT_OWNER"));
    }

    public static boolean isOwner(final ItemStack itemStack, @Nonnull final UUID player) {
        return player.equals(UUIDUtils.toUuid(LibraryPlugin.COMPAT.getPlDataString(itemStack,
                "AKZ_MAPART_COPYRIGHT_OWNER")));
    }

    @EventTarget(bktPriority = org.akazukin.library.event.EventPriority.HIGH)
    public void onPrepareItemCraft(final PrepareItemCraftEvent event) {
        if (event.getInventory().getResult() != null && hasCopyright(event.getInventory().getResult()) && isOwner(event.getInventory().getResult(), event.getView().getPlayer().getUniqueId())) {
            event.getInventory().setResult(null);
        }
    }

    @EventTarget(bktPriority = org.akazukin.library.event.EventPriority.HIGH)
    public void onPrepareAnvil(final PrepareAnvilEvent event) {
        if (event.getResult() != null && hasCopyright(event.getResult()) && isOwner(event.getResult(),
                event.getView().getPlayer().getUniqueId())) {
            event.setResult(null);
        }
    }

    @EventTarget(bktPriority = org.akazukin.library.event.EventPriority.HIGH)
    public void onPrepareEnch(final PrepareItemEnchantEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (hasCopyright(event.getItem()) && isOwner(event.getItem(), event.getView().getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
