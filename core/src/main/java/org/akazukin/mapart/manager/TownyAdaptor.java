package org.akazukin.mapart.manager;

import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import org.akazukin.library.event.EventTarget;
import org.akazukin.library.event.Listenable;
import org.bukkit.Bukkit;

public class TownyAdaptor implements Listenable {

    @EventTarget(bktPriority = org.akazukin.library.event.EventPriority.HIGH)
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
