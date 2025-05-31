package org.akazukin.mapart.manager.ac;

import com.gmail.olexorus.themis.api.ViolationEvent;
import org.akazukin.event.EventTarget;
import org.akazukin.event.Listenable;
import org.akazukin.mapart.manager.mapart.MapartManager;
import org.bukkit.Bukkit;

public final class ThemisAdaptor implements Listenable {

    @EventTarget(libraryPriority = 3)
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
