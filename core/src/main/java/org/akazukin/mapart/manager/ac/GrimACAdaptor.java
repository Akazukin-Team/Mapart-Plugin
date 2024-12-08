package org.akazukin.mapart.manager.ac;

import ac.grim.grimac.api.events.FlagEvent;
import org.akazukin.library.event.EventTarget;
import org.akazukin.library.event.Listenable;
import org.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;

public class GrimACAdaptor implements Listenable {

    @EventTarget(bktPriority = org.akazukin.library.event.EventPriority.HIGH)
    public void onFlag(final FlagEvent event) {
        if (!MapartManager.isMapartWorld(Bukkit.getPlayer(event.getPlayer().getUniqueId()).getWorld()))
            return;

        event.setCancelled(true);
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("GrimAC");
    }
}
