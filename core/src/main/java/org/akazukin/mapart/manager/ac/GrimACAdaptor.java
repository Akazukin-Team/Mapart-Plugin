package org.akazukin.mapart.manager.ac;

import ac.grim.grimac.api.events.FlagEvent;
import org.akazukin.event.EventTarget;
import org.akazukin.event.Listenable;
import org.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;

public class GrimACAdaptor implements Listenable {

    @EventTarget(libraryPriority = 3)
    public void onFlag(final FlagEvent event) {
        if (!MapartManager.isMapartWorld(Bukkit.getPlayer(event.getPlayer().getUniqueId()).getWorld())) {
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("GrimAC");
    }
}
