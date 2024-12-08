package org.akazukin.mapart.manager.ac;

import com.gmail.olexorus.themis.api.ViolationEvent;
import org.akazukin.library.event.EventTarget;
import org.akazukin.library.event.Listenable;
import org.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;

public class ThemisAdaptor implements Listenable {

    @EventTarget(bktPriority = org.akazukin.library.event.EventPriority.HIGH)
    public void onViolation(final ViolationEvent event) {
        if (!MapartManager.isMapartWorld(event.getPlayer().getWorld())) {
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("Themis");
    }
}
