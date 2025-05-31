package org.akazukin.mapart.manager;

import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import org.akazukin.event.EventTarget;
import org.akazukin.event.Listenable;
import org.akazukin.mapart.manager.mapart.MapartManager;
import org.bukkit.Bukkit;

public final class TownyAdaptor implements Listenable {

    @EventTarget(libraryPriority = 3)
    public void onTownClaimEvent(final TownPreClaimEvent event) {
        if (MapartManager.isMapartWorld(event.getTownBlock().getWorld().getBukkitWorld())) {
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("Towny");
    }
}
