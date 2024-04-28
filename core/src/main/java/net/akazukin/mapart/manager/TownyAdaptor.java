package net.akazukin.mapart.manager;

import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

public class TownyAdaptor implements Listenable {

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onTownClaimEvent(final TownPreClaimEvent event) {
        if (event.getTownBlock().getWorld().getBukkitWorld().getUID() != MapartManager.getWorld().getUID()) return;

        event.setCancelled(true);
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("Towny");
    }
}
