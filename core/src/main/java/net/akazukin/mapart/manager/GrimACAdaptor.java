package net.akazukin.mapart.manager;

import ac.grim.grimac.api.events.FlagEvent;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

public class GrimACAdaptor implements Listenable {

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onGrimACFlagEvent(final FlagEvent event) {
        if (Bukkit.getPlayer(event.getPlayer().getUniqueId()).getWorld().getUID() != MapartManager.getWorld().getUID())
            return;

        switch (event.getCheck().getCheckName()) {
            case "GroundSpoof":
            case "Simulation":
            case "PositionPlace":
            case "Post": {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("GrimAC");
    }
}
