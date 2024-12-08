package org.akazukin.mapart.manager.ac;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.akazukin.library.event.EventTarget;
import org.akazukin.library.event.Listenable;
import org.akazukin.mapart.manager.MapartManager;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class NCPAdaptor implements Listenable {

    @EventTarget(bktPriority = org.akazukin.library.event.EventPriority.NORMAL)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        if (!MapartManager.isMapartWorld(event.getPlayer().getWorld()))
            return;

        if (MapartManager.isMapartWorld(event.getFrom())) {
            NCPExemptionManager.unexempt(event.getPlayer());
        } else if (MapartManager.isMapartWorld(event.getPlayer().getWorld())) {
            NCPExemptionManager.exemptPermanently(event.getPlayer());
        }
    }

    @Override
    public boolean handleEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("NoCheatPlus");
    }
}
