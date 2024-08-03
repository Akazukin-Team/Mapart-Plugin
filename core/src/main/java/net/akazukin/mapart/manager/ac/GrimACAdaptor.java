package net.akazukin.mapart.manager.ac;

import ac.grim.grimac.api.events.FlagEvent;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

public class GrimACAdaptor implements Listenable {

    @EventTarget(bktPriority = EventPriority.HIGH)
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
