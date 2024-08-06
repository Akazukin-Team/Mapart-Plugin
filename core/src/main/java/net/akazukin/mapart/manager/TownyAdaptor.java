package net.akazukin.mapart.manager;

import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import org.bukkit.Bukkit;

public class TownyAdaptor implements Listenable {

    @EventTarget(bktPriority = net.akazukin.library.event.EventPriority.HIGH)
    public void onTownClaimEvent(final TownPreClaimEvent event) {
        if (MapartManager.isMapartWorld(event.getTownBlock().getWorld().getBukkitWorld())) return;

        event.setCancelled(true);
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("Towny");
    }
}
