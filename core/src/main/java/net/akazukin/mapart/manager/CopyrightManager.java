package net.akazukin.mapart.manager;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.StringUtils;
import net.akazukin.mapart.MapartPlugin;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CopyrightManager {
    public static boolean hasCopyright(final ItemStack itemStack) {
        return LibraryPlugin.COMPAT.containsNBT(itemStack, "AKZ_MAPART_COPYRIGHT_OWNER") && LibraryPlugin.COMPAT.containsNBT(itemStack, "AKZ_MAPART_COPYRIGHT_LORE");
    }

    public static boolean isOwner(final ItemStack itemStack, @Nonnull final UUID player) {
        return player.equals(StringUtils.toUuid(LibraryPlugin.COMPAT.getNBTString(itemStack, "AKZ_MAPART_COPYRIGHT_OWNER")));
    }

    public static ItemStack setCopyright(final ItemStack itemStack, final UUID player) {
        final String lore = MapartPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("mapart.copyright.lore"));
        ItemStack item = LibraryPlugin.COMPAT.setNBT(itemStack, "AKZ_MAPART_COPYRIGHT_OWNER", String.valueOf(player));
        item = LibraryPlugin.COMPAT.setNBT(item, "AKZ_MAPART_COPYRIGHT_LORE", lore);
        final List<String> lores = ItemUtils.getLore(itemStack);
        lores.add(lore);
        ItemUtils.setLore(item, lores);
        return item;
    }

    @Nullable
    public static ItemStack removeCopyright(final ItemStack itemStack) {
        if (LibraryPlugin.COMPAT.containsNBT(itemStack, "AKZ_MAPART_COPYRIGHT_LORE") ||
                LibraryPlugin.COMPAT.containsNBT(itemStack, "AKZ_MAPART_COPYRIGHT_OWNER")) return null;

        final List<String> lores = ItemUtils.getLore(itemStack);
        if (lores.contains(LibraryPlugin.COMPAT.getNBTString(itemStack, "AKZ_MAPART_COPYRIGHT_LORE"))) {
            lores.remove(LibraryPlugin.COMPAT.getNBTString(itemStack, "AKZ_MAPART_COPYRIGHT_LORE"));
            ItemStack item = LibraryPlugin.COMPAT.removeNBT(itemStack, "AKZ_MAPART_COPYRIGHT_LORE");
            item = LibraryPlugin.COMPAT.removeNBT(item, "AKZ_MAPART_COPYRIGHT_OWNER");
            ItemUtils.setLore(item, lores);
            return item;
        }
        return null;
    }
}
